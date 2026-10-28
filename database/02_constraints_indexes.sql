-- =====================================================================
-- EcoSort — 02. Constrângeri (FK, UNIQUE, CHECK) și indexuri
-- MySQL 8.0 nu are "ADD CONSTRAINT IF NOT EXISTS", de aceea fiecare
-- modificare trece printr-o procedură temporară care verifică
-- information_schema. Scriptul se poate executa repetat.
-- =====================================================================

USE ecosort;

DROP PROCEDURE IF EXISTS _adauga_daca_lipseste;

DELIMITER $$
CREATE PROCEDURE _adauga_daca_lipseste(
    IN p_tabel VARCHAR(64),
    IN p_nume  VARCHAR(64),
    IN p_ddl   TEXT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE table_schema = DATABASE() AND table_name = p_tabel AND constraint_name = p_nume)
       AND NOT EXISTS (SELECT 1 FROM information_schema.statistics
                       WHERE table_schema = DATABASE() AND table_name = p_tabel AND index_name = p_nume)
    THEN
        SET @ddl = p_ddl;
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- ---------- utilizatori ----------
CALL _adauga_daca_lipseste('utilizatori', 'uq_utilizatori_username',
    'ALTER TABLE utilizatori ADD CONSTRAINT uq_utilizatori_username UNIQUE (username)');
CALL _adauga_daca_lipseste('utilizatori', 'ck_utilizatori_username',
    'ALTER TABLE utilizatori ADD CONSTRAINT ck_utilizatori_username CHECK (CHAR_LENGTH(TRIM(username)) >= 3)');
CALL _adauga_daca_lipseste('utilizatori', 'ck_utilizatori_nume',
    'ALTER TABLE utilizatori ADD CONSTRAINT ck_utilizatori_nume CHECK (CHAR_LENGTH(TRIM(nume_complet)) > 0)');

-- ---------- categorii_deseuri ----------
CALL _adauga_daca_lipseste('categorii_deseuri', 'uq_categorii_denumire',
    'ALTER TABLE categorii_deseuri ADD CONSTRAINT uq_categorii_denumire UNIQUE (denumire)');
CALL _adauga_daca_lipseste('categorii_deseuri', 'ck_categorii_denumire',
    'ALTER TABLE categorii_deseuri ADD CONSTRAINT ck_categorii_denumire CHECK (CHAR_LENGTH(TRIM(denumire)) > 0)');
CALL _adauga_daca_lipseste('categorii_deseuri', 'ck_categorii_culoare',
    'ALTER TABLE categorii_deseuri ADD CONSTRAINT ck_categorii_culoare CHECK (REGEXP_LIKE(culoare, ''^#[0-9A-Fa-f]{6}$''))');

-- ---------- puncte_colectare ----------
CALL _adauga_daca_lipseste('puncte_colectare', 'fk_puncte_categorie',
    'ALTER TABLE puncte_colectare ADD CONSTRAINT fk_puncte_categorie FOREIGN KEY (categorie_id)
     REFERENCES categorii_deseuri (id) ON UPDATE CASCADE ON DELETE RESTRICT');
-- Un singur coș de aceeași categorie într-o încăpere.
CALL _adauga_daca_lipseste('puncte_colectare', 'uq_puncte_loc_categorie',
    'ALTER TABLE puncte_colectare ADD CONSTRAINT uq_puncte_loc_categorie UNIQUE (bloc, etaj, incapere, categorie_id)');
CALL _adauga_daca_lipseste('puncte_colectare', 'ck_puncte_denumire',
    'ALTER TABLE puncte_colectare ADD CONSTRAINT ck_puncte_denumire CHECK (CHAR_LENGTH(TRIM(denumire)) > 0)');

-- ---------- inregistrari_deseuri ----------
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'fk_inregistrari_utilizator',
    'ALTER TABLE inregistrari_deseuri ADD CONSTRAINT fk_inregistrari_utilizator FOREIGN KEY (utilizator_id)
     REFERENCES utilizatori (id) ON UPDATE CASCADE ON DELETE RESTRICT');
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'fk_inregistrari_categorie',
    'ALTER TABLE inregistrari_deseuri ADD CONSTRAINT fk_inregistrari_categorie FOREIGN KEY (categorie_id)
     REFERENCES categorii_deseuri (id) ON UPDATE CASCADE ON DELETE RESTRICT');
-- La ștergerea unui punct, istoricul rămâne, doar fără punct.
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'fk_inregistrari_punct',
    'ALTER TABLE inregistrari_deseuri ADD CONSTRAINT fk_inregistrari_punct FOREIGN KEY (punct_id)
     REFERENCES puncte_colectare (id) ON UPDATE CASCADE ON DELETE SET NULL');
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'ck_inregistrari_cantitate',
    'ALTER TABLE inregistrari_deseuri ADD CONSTRAINT ck_inregistrari_cantitate CHECK (cantitate > 0)');
-- Filtrare după perioadă / categorie / utilizator.
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'ix_inregistrari_data',
    'CREATE INDEX ix_inregistrari_data ON inregistrari_deseuri (data_colectare)');
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'ix_inregistrari_categorie_data',
    'CREATE INDEX ix_inregistrari_categorie_data ON inregistrari_deseuri (categorie_id, data_colectare)');
CALL _adauga_daca_lipseste('inregistrari_deseuri', 'ix_inregistrari_utilizator_data',
    'CREATE INDEX ix_inregistrari_utilizator_data ON inregistrari_deseuri (utilizator_id, data_colectare)');

-- ---------- obiective_lunare ----------
CALL _adauga_daca_lipseste('obiective_lunare', 'fk_obiective_categorie',
    'ALTER TABLE obiective_lunare ADD CONSTRAINT fk_obiective_categorie FOREIGN KEY (categorie_id)
     REFERENCES categorii_deseuri (id) ON UPDATE CASCADE ON DELETE CASCADE');
CALL _adauga_daca_lipseste('obiective_lunare', 'uq_obiective_categorie_luna',
    'ALTER TABLE obiective_lunare ADD CONSTRAINT uq_obiective_categorie_luna UNIQUE (categorie_id, an, luna)');
CALL _adauga_daca_lipseste('obiective_lunare', 'ck_obiective_luna',
    'ALTER TABLE obiective_lunare ADD CONSTRAINT ck_obiective_luna CHECK (luna BETWEEN 1 AND 12)');
CALL _adauga_daca_lipseste('obiective_lunare', 'ck_obiective_an',
    'ALTER TABLE obiective_lunare ADD CONSTRAINT ck_obiective_an CHECK (an BETWEEN 2000 AND 2100)');
CALL _adauga_daca_lipseste('obiective_lunare', 'ck_obiective_tinta',
    'ALTER TABLE obiective_lunare ADD CONSTRAINT ck_obiective_tinta CHECK (cantitate_tinta >= 0)');

DROP PROCEDURE IF EXISTS _adauga_daca_lipseste;
