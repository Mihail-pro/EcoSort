-- =====================================================================
-- EcoSort — Proceduri: utilizatori (autentificare, profil, administrare)
-- Erorile de business se semnalează cu SQLSTATE '45000'; MESSAGE_TEXT
-- este afișat direct utilizatorului de aplicație.
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_utilizator_autentificare;
DROP PROCEDURE IF EXISTS sp_utilizator_profil;
DROP PROCEDURE IF EXISTS sp_utilizator_lista;
DROP PROCEDURE IF EXISTS sp_utilizator_exista_admin;
DROP PROCEDURE IF EXISTS sp_utilizator_creeaza_primul_admin;
DROP PROCEDURE IF EXISTS sp_utilizator_adauga;
DROP PROCEDURE IF EXISTS sp_utilizator_modifica;
DROP PROCEDURE IF EXISTS sp_utilizator_schimba_parola;

DELIMITER $$

-- Returnează hash-ul parolei; verificarea Argon2id se face în Java.
CREATE PROCEDURE sp_utilizator_autentificare(IN p_username VARCHAR(50))
BEGIN
    SELECT id, username, nume_complet, parola_hash, rol, activ
    FROM utilizatori
    WHERE username = TRIM(p_username);
END$$

CREATE PROCEDURE sp_utilizator_profil(IN p_id INT UNSIGNED)
BEGIN
    SELECT u.id, u.username, u.nume_complet, u.rol, u.activ,
           COALESCE(SUM(i.cantitate), 0) AS total_kg,
           COUNT(i.id)                   AS numar_inregistrari
    FROM utilizatori u
    LEFT JOIN inregistrari_deseuri i ON i.utilizator_id = u.id
    WHERE u.id = p_id
    GROUP BY u.id, u.username, u.nume_complet, u.rol, u.activ;
END$$

CREATE PROCEDURE sp_utilizator_lista()
BEGIN
    SELECT id, username, nume_complet, rol, activ
    FROM utilizatori
    ORDER BY username;
END$$

CREATE PROCEDURE sp_utilizator_exista_admin(OUT p_exista BOOLEAN)
BEGIN
    SET p_exista = EXISTS (SELECT 1 FROM utilizatori WHERE rol = 'ADMIN');
END$$

-- Folosită o singură dată, la prima pornire, când nu există niciun administrator.
CREATE PROCEDURE sp_utilizator_creeaza_primul_admin(
    IN  p_username     VARCHAR(50),
    IN  p_nume_complet VARCHAR(100),
    IN  p_parola_hash  VARCHAR(255),
    OUT p_id           INT UNSIGNED
)
BEGIN
    INSERT INTO utilizatori (username, nume_complet, parola_hash, rol)
    SELECT TRIM(p_username), TRIM(p_nume_complet), p_parola_hash, 'ADMIN'
    FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM utilizatori WHERE rol = 'ADMIN');

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Există deja un administrator.';
    END IF;
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_utilizator_adauga(
    IN  p_username     VARCHAR(50),
    IN  p_nume_complet VARCHAR(100),
    IN  p_parola_hash  VARCHAR(255),
    IN  p_rol          VARCHAR(20),
    OUT p_id           INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM utilizatori WHERE username = TRIM(p_username)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Numele de utilizator este deja folosit.';
    END IF;
    INSERT INTO utilizatori (username, nume_complet, parola_hash, rol)
    VALUES (TRIM(p_username), TRIM(p_nume_complet), p_parola_hash, p_rol);
    SET p_id = LAST_INSERT_ID();
END$$

-- Nu permite rămânerea aplicației fără niciun administrator activ.
CREATE PROCEDURE sp_utilizator_modifica(
    IN p_id           INT UNSIGNED,
    IN p_nume_complet VARCHAR(100),
    IN p_rol          VARCHAR(20),
    IN p_activ        BOOLEAN
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM utilizatori WHERE id = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Utilizatorul nu există.';
    END IF;
    IF (p_rol <> 'ADMIN' OR NOT p_activ)
       AND NOT EXISTS (SELECT 1 FROM utilizatori
                       WHERE rol = 'ADMIN' AND activ AND id <> p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Trebuie să rămână cel puțin un administrator activ.';
    END IF;
    UPDATE utilizatori
    SET nume_complet = TRIM(p_nume_complet), rol = p_rol, activ = p_activ
    WHERE id = p_id;
END$$

CREATE PROCEDURE sp_utilizator_schimba_parola(
    IN p_id          INT UNSIGNED,
    IN p_parola_hash VARCHAR(255)
)
BEGIN
    UPDATE utilizatori SET parola_hash = p_parola_hash WHERE id = p_id;
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Utilizatorul nu există.';
    END IF;
END$$

DELIMITER ;
