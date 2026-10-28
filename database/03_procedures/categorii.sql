-- =====================================================================
-- EcoSort — Proceduri: categorii de deșeuri
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_categorie_lista;
DROP PROCEDURE IF EXISTS sp_categorie_adauga;
DROP PROCEDURE IF EXISTS sp_categorie_modifica;
DROP PROCEDURE IF EXISTS sp_categorie_sterge;

DELIMITER $$

CREATE PROCEDURE sp_categorie_lista()
BEGIN
    SELECT id, denumire, culoare
    FROM categorii_deseuri
    ORDER BY id;
END$$

CREATE PROCEDURE sp_categorie_adauga(
    IN  p_denumire VARCHAR(50),
    IN  p_culoare  CHAR(7),
    OUT p_id       INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM categorii_deseuri WHERE denumire = TRIM(p_denumire)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Există deja o categorie cu această denumire.';
    END IF;
    INSERT INTO categorii_deseuri (denumire, culoare) VALUES (TRIM(p_denumire), UPPER(p_culoare));
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_categorie_modifica(
    IN p_id       INT UNSIGNED,
    IN p_denumire VARCHAR(50),
    IN p_culoare  CHAR(7)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM categorii_deseuri WHERE id = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria nu există.';
    END IF;
    IF EXISTS (SELECT 1 FROM categorii_deseuri WHERE denumire = TRIM(p_denumire) AND id <> p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Există deja o categorie cu această denumire.';
    END IF;
    UPDATE categorii_deseuri
    SET denumire = TRIM(p_denumire), culoare = UPPER(p_culoare)
    WHERE id = p_id;
END$$

-- O categorie folosită în înregistrări sau puncte de colectare nu se șterge.
-- Obiectivele lunare ale categoriei se șterg automat (ON DELETE CASCADE).
CREATE PROCEDURE sp_categorie_sterge(IN p_id INT UNSIGNED)
BEGIN
    IF EXISTS (SELECT 1 FROM inregistrari_deseuri WHERE categorie_id = p_id)
       OR EXISTS (SELECT 1 FROM puncte_colectare WHERE categorie_id = p_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Categoria este folosită în înregistrări sau puncte de colectare și nu poate fi ștearsă.';
    END IF;
    DELETE FROM categorii_deseuri WHERE id = p_id;
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria nu există.';
    END IF;
END$$

DELIMITER ;
