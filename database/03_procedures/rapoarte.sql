-- =====================================================================
-- EcoSort — Proceduri: rapoarte (totaluri, progres lunar)
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS sp_raport_totaluri_categorii;
DROP PROCEDURE IF EXISTS sp_raport_progres_lunar;

DELIMITER $$

-- Total colectat pe fiecare categorie (inclusiv cele cu 0 kg) într-un interval.
-- Parametrii NULL înseamnă fără limită / toți utilizatorii.
CREATE PROCEDURE sp_raport_totaluri_categorii(
    IN p_de_la         DATE,
    IN p_pana_la       DATE,
    IN p_utilizator_id INT UNSIGNED
)
BEGIN
    SELECT c.id AS categorie_id, c.denumire, c.culoare,
           COALESCE(SUM(i.cantitate), 0) AS total_kg
    FROM categorii_deseuri c
    LEFT JOIN inregistrari_deseuri i
           ON i.categorie_id = c.id
          AND (p_de_la   IS NULL OR i.data_colectare >= p_de_la)
          AND (p_pana_la IS NULL OR i.data_colectare <= p_pana_la)
          AND (p_utilizator_id IS NULL OR i.utilizator_id = p_utilizator_id)
    GROUP BY c.id, c.denumire, c.culoare
    ORDER BY c.id;
END$$

-- Pentru fiecare categorie: ținta lunii (0 dacă nu e setată) și cât s-a colectat în acea lună.
CREATE PROCEDURE sp_raport_progres_lunar(
    IN p_an   SMALLINT UNSIGNED,
    IN p_luna TINYINT UNSIGNED
)
BEGIN
    DECLARE v_inceput DATE DEFAULT MAKEDATE(p_an, 1) + INTERVAL (p_luna - 1) MONTH;

    SELECT c.id AS categorie_id, c.denumire, c.culoare,
           COALESCE(o.cantitate_tinta, 0) AS tinta_kg,
           COALESCE((SELECT SUM(i.cantitate) FROM inregistrari_deseuri i
                     WHERE i.categorie_id = c.id
                       AND i.data_colectare >= v_inceput
                       AND i.data_colectare <  v_inceput + INTERVAL 1 MONTH), 0) AS colectat_kg
    FROM categorii_deseuri c
    LEFT JOIN obiective_lunare o
           ON o.categorie_id = c.id AND o.an = p_an AND o.luna = p_luna
    ORDER BY c.id;
END$$

DELIMITER ;
