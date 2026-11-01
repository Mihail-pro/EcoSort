-- EcoSort — Proceduri: senzori și măsurători
USE ecosort;

DROP PROCEDURE IF EXISTS sp_senzor_leaga;
DROP PROCEDURE IF EXISTS sp_senzor_dezleaga;
DROP PROCEDURE IF EXISTS sp_masuratoare_inregistreaza;
DROP PROCEDURE IF EXISTS sp_cos_stare_lista;

DELIMITER $$

-- Leagă un punct de colectare de un canal al unui dispozitiv (dispozitivul se creează dacă nu există).
-- Apelat de administrator din aplicație. Rulat din nou pe același punct, actualizează calibrările.
CREATE PROCEDURE sp_senzor_leaga(
    IN p_punct_id      INT UNSIGNED,
    IN p_cod           VARCHAR(40),
    IN p_canal         TINYINT UNSIGNED,
    IN p_capacitate_kg DECIMAL(6,2),
    IN p_goala_mm      SMALLINT UNSIGNED,
    IN p_plina_mm      SMALLINT UNSIGNED
)
BEGIN
    DECLARE v_disp INT UNSIGNED;

    IF NOT EXISTS (SELECT 1 FROM puncte_colectare WHERE id = p_punct_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Punctul de colectare nu există.';
END IF;
    IF p_cod IS NULL OR CHAR_LENGTH(TRIM(p_cod)) = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Codul dispozitivului este obligatoriu.';
END IF;
    IF p_canal IS NULL OR p_canal NOT BETWEEN 1 AND 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Canalul trebuie să fie 1, 2 sau 3.';
END IF;
    IF p_capacitate_kg IS NULL OR p_capacitate_kg <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Capacitatea trebuie să fie pozitivă.';
END IF;
    IF p_goala_mm IS NULL OR p_plina_mm IS NULL OR p_goala_mm <= p_plina_mm THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Distanța pentru coș gol trebuie să fie mai mare decât cea pentru coș plin.';
END IF;

    INSERT IGNORE INTO dispozitive (cod, denumire) VALUES (TRIM(p_cod), TRIM(p_cod));
SELECT id INTO v_disp FROM dispozitive WHERE cod = TRIM(p_cod);

IF EXISTS (SELECT 1 FROM senzori_cos
               WHERE dispozitiv_id = v_disp AND canal = p_canal AND punct_id <> p_punct_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Acest canal al dispozitivului este deja legat de alt punct de colectare.';
END IF;

INSERT INTO senzori_cos (punct_id, dispozitiv_id, canal, capacitate_kg, inaltime_goala_mm, inaltime_plina_mm)
VALUES (p_punct_id, v_disp, p_canal, p_capacitate_kg, p_goala_mm, p_plina_mm) AS nou
ON DUPLICATE KEY UPDATE dispozitiv_id = nou.dispozitiv_id, canal = nou.canal,
                     capacitate_kg = nou.capacitate_kg,
                     inaltime_goala_mm = nou.inaltime_goala_mm, inaltime_plina_mm = nou.inaltime_plina_mm;
END$$

-- Istoricul rămâne; punctul revine la nivelul de umplere necunoscut.
CREATE PROCEDURE sp_senzor_dezleaga(IN p_punct_id INT UNSIGNED)
BEGIN
DELETE FROM senzori_cos WHERE punct_id = p_punct_id;
IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Punctul nu are niciun senzor legat.';
END IF;
END$$

-- Apelat de serviciul de ingestie. p_masurat_la = NULL → ora serverului.
-- Ultima stare se actualizează la fiecare mesaj; în istoric se scrie dacă au trecut 60 s
-- de la ultima măsurătoare salvată sau greutatea s-a schimbat cu cel puțin 20 g.
CREATE PROCEDURE sp_masuratoare_inregistreaza(
    IN p_cod         VARCHAR(40),
    IN p_canal       TINYINT UNSIGNED,
    IN p_greutate_g  INT,
    IN p_distanta_mm INT,
    IN p_masurat_la  DATETIME(3)
)
BEGIN
    DECLARE v_punct  INT UNSIGNED;
    DECLARE v_goala  INT;
    DECLARE v_plina  INT;
    DECLARE v_ult_g  BIGINT;
    DECLARE v_salvat DATETIME(3);
    DECLARE v_acum   DATETIME(3);
    DECLARE v_g      INT;
    DECLARE v_pct    INT DEFAULT NULL;

    SET v_acum = COALESCE(p_masurat_la, NOW(3));

SELECT s.punct_id, s.inaltime_goala_mm, s.inaltime_plina_mm, s.ultima_greutate_g
INTO v_punct, v_goala, v_plina, v_ult_g
FROM senzori_cos s
         JOIN dispozitive d ON d.id = s.dispozitiv_id
WHERE d.cod = p_cod AND s.canal = p_canal;

IF v_punct IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Dispozitivul sau canalul nu este legat de niciun punct de colectare.';
END IF;

    SET v_g = GREATEST(COALESCE(p_greutate_g, 0), 0);   -- HX711 poate da valori ușor negative
    IF p_distanta_mm IS NOT NULL AND p_distanta_mm BETWEEN 0 AND 4000 THEN
        SET v_pct = LEAST(100, GREATEST(0,
            ROUND(100 * (v_goala - p_distanta_mm) / (v_goala - v_plina))));
ELSE
        SET p_distanta_mm = NULL;                        -- măsurătoare invalidă (ex. -1)
END IF;

SELECT MAX(masurat_la) INTO v_salvat FROM masuratori WHERE punct_id = v_punct;
IF v_salvat IS NULL
       OR TIMESTAMPDIFF(SECOND, v_salvat, v_acum) >= 60
       OR ABS(CAST(v_g AS SIGNED) - COALESCE(v_ult_g, 0)) >= 20 THEN
        INSERT INTO masuratori (punct_id, masurat_la, greutate_g, distanta_mm, umplere_pct)
        VALUES (v_punct, v_acum, v_g, p_distanta_mm, v_pct);
END IF;

UPDATE senzori_cos
SET ultima_masurare = v_acum, ultima_greutate_g = v_g,
    ultima_distanta_mm = p_distanta_mm, ultima_umplere_pct = v_pct
WHERE punct_id = v_punct;
UPDATE dispozitive SET ultima_activitate = v_acum WHERE cod = p_cod;
END$$

-- Citit de hartă. vechime_s = secunde de la ultima măsurătoare (NULL dacă nu a venit niciuna);
-- calculul se face în MySQL ca să nu depindă de fusul orar al clientului JDBC.
CREATE PROCEDURE sp_cos_stare_lista()
BEGIN
SELECT s.punct_id, d.cod AS dispozitiv, s.canal, s.capacitate_kg,
       s.ultima_greutate_g AS greutate_g, s.ultima_distanta_mm AS distanta_mm,
       s.ultima_umplere_pct AS umplere_pct,
       TIMESTAMPDIFF(SECOND, s.ultima_masurare, NOW(3)) AS vechime_s
FROM senzori_cos s
         JOIN dispozitive d ON d.id = s.dispozitiv_id
ORDER BY s.punct_id;
END$$

DELIMITER ;