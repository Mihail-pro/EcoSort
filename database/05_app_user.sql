-- =====================================================================
-- EcoSort — 05. Contul MySQL folosit de aplicație (privilegii minime)
--
-- Contul primește DOAR dreptul EXECUTE pe procedurile din `ecosort`:
-- nu poate citi sau modifica tabelele direct.
--
-- Parola NU se scrie în acest fișier. Înainte de execuție, în aceeași
-- sesiune (același tab din Workbench), rulați:
--     SET @app_password = 'o-parola-puternica';
--     SET @app_user     = 'ecosort_app';  -- opțional; implicit ecosort_app
--     SET @app_host     = 'localhost';    -- opțional; '%' pentru acces din rețea
-- Scriptul se poate rula repetat (actualizează parola).
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS _creeaza_utilizator_app;

DELIMITER $$
CREATE PROCEDURE _creeaza_utilizator_app(IN p_user VARCHAR(32), IN p_parola VARCHAR(255), IN p_host VARCHAR(255))
BEGIN
    IF p_parola IS NULL OR CHAR_LENGTH(p_parola) < 8 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Setați mai întâi @app_password (minim 8 caractere).';
    END IF;

    SET @cont = CONCAT(QUOTE(COALESCE(NULLIF(p_user, ''), 'ecosort_app')), '@',
                       QUOTE(COALESCE(NULLIF(p_host, ''), 'localhost')));

    SET @ddl = CONCAT('CREATE USER IF NOT EXISTS ', @cont, ' IDENTIFIED BY ', QUOTE(p_parola));
    PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @ddl = CONCAT('ALTER USER ', @cont, ' IDENTIFIED BY ', QUOTE(p_parola));
    PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @ddl = CONCAT('GRANT EXECUTE ON ecosort.* TO ', @cont);
    PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @ddl = NULL;
END$$
DELIMITER ;

CALL _creeaza_utilizator_app(@app_user, @app_password, @app_host);

-- Procedura temporară nu trebuie să rămână accesibilă contului aplicației.
DROP PROCEDURE IF EXISTS _creeaza_utilizator_app;
SET @app_password = NULL;
