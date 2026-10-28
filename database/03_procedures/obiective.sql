-- =====================================================================
-- EcoSort — Proceduri: obiective lunare
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_obiectiv_seteaza;
DROP PROCEDURE IF EXISTS sp_obiectiv_sterge;

DELIMITER $$

-- Creează sau actualizează ținta unei categorii pentru o lună.
CREATE PROCEDURE sp_obiectiv_seteaza(
    IN p_categorie_id INT UNSIGNED,
    IN p_an           SMALLINT UNSIGNED,
    IN p_luna         TINYINT UNSIGNED,
    IN p_tinta        DECIMAL(10,2)
)
BEGIN
    INSERT INTO obiective_lunare (categorie_id, an, luna, cantitate_tinta)
    VALUES (p_categorie_id, p_an, p_luna, p_tinta)
    ON DUPLICATE KEY UPDATE cantitate_tinta = VALUES(cantitate_tinta);
END$$

CREATE PROCEDURE sp_obiectiv_sterge(
    IN p_categorie_id INT UNSIGNED,
    IN p_an           SMALLINT UNSIGNED,
    IN p_luna         TINYINT UNSIGNED
)
BEGIN
    DELETE FROM obiective_lunare
    WHERE categorie_id = p_categorie_id AND an = p_an AND luna = p_luna;
END$$

DELIMITER ;
