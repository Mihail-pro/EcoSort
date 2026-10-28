-- =====================================================================
-- EcoSort — Proceduri: înregistrări de deșeuri (CRUD + filtrare)
-- p_proprietar_id: NULL = orice înregistrare (administrator),
--                  altfel operația se limitează la înregistrările acelui utilizator.
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_inregistrare_lista;
DROP PROCEDURE IF EXISTS sp_inregistrare_adauga;
DROP PROCEDURE IF EXISTS sp_inregistrare_modifica;
DROP PROCEDURE IF EXISTS sp_inregistrare_sterge;
DROP PROCEDURE IF EXISTS _verifica_punct_categorie;

DELIMITER $$

-- Punctul de colectare (dacă e ales) trebuie să fie de aceeași categorie.
CREATE PROCEDURE _verifica_punct_categorie(IN p_punct_id INT UNSIGNED, IN p_categorie_id INT UNSIGNED)
BEGIN
    IF p_punct_id IS NOT NULL
       AND NOT EXISTS (SELECT 1 FROM puncte_colectare WHERE id = p_punct_id AND categorie_id = p_categorie_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Punctul de colectare ales nu există sau nu acceptă această categorie.';
    END IF;
END$$

-- Toți parametrii de filtrare sunt opționali (NULL = fără filtru). Intervalul de date este inclusiv.
CREATE PROCEDURE sp_inregistrare_lista(
    IN p_utilizator_id INT UNSIGNED,
    IN p_categorie_id  INT UNSIGNED,
    IN p_de_la         DATE,
    IN p_pana_la       DATE
)
BEGIN
    SELECT i.id, i.utilizator_id, u.username,
           i.categorie_id, c.denumire AS categorie, c.culoare,
           i.punct_id, p.denumire AS punct,
           i.cantitate, i.data_colectare
    FROM inregistrari_deseuri i
    JOIN utilizatori u        ON u.id = i.utilizator_id
    JOIN categorii_deseuri c  ON c.id = i.categorie_id
    LEFT JOIN puncte_colectare p ON p.id = i.punct_id
    WHERE (p_utilizator_id IS NULL OR i.utilizator_id = p_utilizator_id)
      AND (p_categorie_id  IS NULL OR i.categorie_id  = p_categorie_id)
      AND (p_de_la   IS NULL OR i.data_colectare >= p_de_la)
      AND (p_pana_la IS NULL OR i.data_colectare <= p_pana_la)
    ORDER BY i.data_colectare DESC, i.id DESC;
END$$

CREATE PROCEDURE sp_inregistrare_adauga(
    IN  p_utilizator_id  INT UNSIGNED,
    IN  p_categorie_id   INT UNSIGNED,
    IN  p_punct_id       INT UNSIGNED,
    IN  p_cantitate      DECIMAL(10,2),
    IN  p_data_colectare DATE,
    OUT p_id             INT UNSIGNED
)
BEGIN
    CALL _verifica_punct_categorie(p_punct_id, p_categorie_id);
    INSERT INTO inregistrari_deseuri (utilizator_id, categorie_id, punct_id, cantitate, data_colectare)
    VALUES (p_utilizator_id, p_categorie_id, p_punct_id, p_cantitate, p_data_colectare);
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_inregistrare_modifica(
    IN p_id             INT UNSIGNED,
    IN p_proprietar_id  INT UNSIGNED,
    IN p_categorie_id   INT UNSIGNED,
    IN p_punct_id       INT UNSIGNED,
    IN p_cantitate      DECIMAL(10,2),
    IN p_data_colectare DATE
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM inregistrari_deseuri
                   WHERE id = p_id AND (p_proprietar_id IS NULL OR utilizator_id = p_proprietar_id)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Înregistrarea nu există sau nu vă aparține.';
    END IF;
    CALL _verifica_punct_categorie(p_punct_id, p_categorie_id);
    UPDATE inregistrari_deseuri
    SET categorie_id = p_categorie_id, punct_id = p_punct_id,
        cantitate = p_cantitate, data_colectare = p_data_colectare
    WHERE id = p_id;
END$$

CREATE PROCEDURE sp_inregistrare_sterge(
    IN p_id            INT UNSIGNED,
    IN p_proprietar_id INT UNSIGNED
)
BEGIN
    DELETE FROM inregistrari_deseuri
    WHERE id = p_id AND (p_proprietar_id IS NULL OR utilizator_id = p_proprietar_id);
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Înregistrarea nu există sau nu vă aparține.';
    END IF;
END$$

DELIMITER ;
