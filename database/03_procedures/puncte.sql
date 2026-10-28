-- =====================================================================
-- EcoSort — Proceduri: puncte de colectare (coșuri pe harta colegiului)
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_punct_lista;
DROP PROCEDURE IF EXISTS sp_punct_adauga;
DROP PROCEDURE IF EXISTS sp_punct_modifica;
DROP PROCEDURE IF EXISTS sp_punct_sterge;

DELIMITER $$

CREATE PROCEDURE sp_punct_lista()
BEGIN
    SELECT p.id, p.denumire, p.bloc, p.etaj, p.incapere,
           p.categorie_id, c.denumire AS categorie, c.culoare
    FROM puncte_colectare p
    JOIN categorii_deseuri c ON c.id = p.categorie_id
    ORDER BY p.etaj, p.bloc, p.incapere, c.denumire;
END$$

CREATE PROCEDURE sp_punct_adauga(
    IN  p_denumire     VARCHAR(100),
    IN  p_bloc         VARCHAR(10),
    IN  p_etaj         TINYINT UNSIGNED,
    IN  p_incapere     VARCHAR(100),
    IN  p_categorie_id INT UNSIGNED,
    OUT p_id           INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM puncte_colectare
               WHERE bloc = p_bloc AND etaj = p_etaj AND incapere = p_incapere AND categorie_id = p_categorie_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'În această încăpere există deja un punct pentru categoria aleasă.';
    END IF;
    INSERT INTO puncte_colectare (denumire, bloc, etaj, incapere, categorie_id)
    VALUES (TRIM(p_denumire), p_bloc, p_etaj, p_incapere, p_categorie_id);
    SET p_id = LAST_INSERT_ID();
END$$

-- Locația (bloc/etaj/încăpere) nu se mută; se modifică denumirea și categoria.
CREATE PROCEDURE sp_punct_modifica(
    IN p_id           INT UNSIGNED,
    IN p_denumire     VARCHAR(100),
    IN p_categorie_id INT UNSIGNED
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM puncte_colectare WHERE id = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Punctul de colectare nu există.';
    END IF;
    IF EXISTS (SELECT 1 FROM puncte_colectare p
               JOIN puncte_colectare x ON x.bloc = p.bloc AND x.etaj = p.etaj AND x.incapere = p.incapere
               WHERE p.id = p_id AND x.id <> p_id AND x.categorie_id = p_categorie_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'În această încăpere există deja un punct pentru categoria aleasă.';
    END IF;
    IF EXISTS (SELECT 1 FROM inregistrari_deseuri
               WHERE punct_id = p_id AND categorie_id <> p_categorie_id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Categoria nu poate fi schimbată: punctul are deja înregistrări.';
    END IF;
    UPDATE puncte_colectare SET denumire = TRIM(p_denumire), categorie_id = p_categorie_id WHERE id = p_id;
END$$

-- Înregistrările legate rămân în istoric, cu punct_id = NULL (ON DELETE SET NULL).
CREATE PROCEDURE sp_punct_sterge(IN p_id INT UNSIGNED)
BEGIN
    DELETE FROM puncte_colectare WHERE id = p_id;
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Punctul de colectare nu există.';
    END IF;
END$$

DELIMITER ;
