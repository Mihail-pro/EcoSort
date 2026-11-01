-- EcoSort — 06. Senzori: dispozitive, legarea lor de coșuri, măsurători
-- Se poate executa repetat: nu șterge și nu modifică date existente.
USE ecosort;

-- Placa ESP32 de pe un coș (un dispozitiv poate avea până la 3 canale = 3 compartimente).
CREATE TABLE IF NOT EXISTS dispozitive (
                                           id                INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                           cod               VARCHAR(40)  NOT NULL,            -- ex. BIN-C1-01, înscris în firmware
    denumire          VARCHAR(100) NOT NULL,
    ultima_activitate DATETIME(3)  NULL,
    creat_la          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_dispozitive_cod UNIQUE (cod),
    CONSTRAINT ck_dispozitive_cod CHECK (CHAR_LENGTH(TRIM(cod)) > 0)
    ) ENGINE = InnoDB;

-- Un punct de colectare are cel mult un senzor. Aici sunt și calibrările și ultima stare
-- (harta citește un singur rând per coș, nu caută ultima măsurătoare în istoric).
CREATE TABLE IF NOT EXISTS senzori_cos (
                                           punct_id           INT UNSIGNED      NOT NULL,
                                           dispozitiv_id      INT UNSIGNED      NOT NULL,
                                           canal              TINYINT UNSIGNED  NOT NULL,
                                           capacitate_kg      DECIMAL(6,2)      NOT NULL DEFAULT 10.00,
    inaltime_goala_mm  SMALLINT UNSIGNED NOT NULL,      -- distanța VL53L1X când coșul e gol
    inaltime_plina_mm  SMALLINT UNSIGNED NOT NULL DEFAULT 50,  -- distanța când e plin
    ultima_masurare    DATETIME(3)       NULL,
    ultima_greutate_g  INT UNSIGNED      NULL,
    ultima_distanta_mm SMALLINT UNSIGNED NULL,
    ultima_umplere_pct TINYINT UNSIGNED  NULL,
    PRIMARY KEY (punct_id),
    CONSTRAINT uq_senzori_dispozitiv_canal UNIQUE (dispozitiv_id, canal),
    CONSTRAINT fk_senzori_punct FOREIGN KEY (punct_id)
    REFERENCES puncte_colectare (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_senzori_dispozitiv FOREIGN KEY (dispozitiv_id)
    REFERENCES dispozitive (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_senzori_canal CHECK (canal BETWEEN 1 AND 3),
    CONSTRAINT ck_senzori_capacitate CHECK (capacitate_kg > 0),
    CONSTRAINT ck_senzori_inaltimi CHECK (inaltime_goala_mm > inaltime_plina_mm)
    ) ENGINE = InnoDB;

-- Istoricul măsurătorilor (fără duplicate apropiate; vezi sp_masuratoare_inregistreaza).
CREATE TABLE IF NOT EXISTS masuratori (
                                          id          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT,
                                          punct_id    INT UNSIGNED      NOT NULL,
                                          masurat_la  DATETIME(3)       NOT NULL,
    greutate_g  INT UNSIGNED      NOT NULL,
    distanta_mm SMALLINT UNSIGNED NULL,
    umplere_pct TINYINT UNSIGNED  NULL,
    PRIMARY KEY (id),
    KEY ix_masuratori_punct_timp (punct_id, masurat_la),
    CONSTRAINT fk_masuratori_punct FOREIGN KEY (punct_id)
    REFERENCES puncte_colectare (id) ON UPDATE CASCADE ON DELETE CASCADE
    ) ENGINE = InnoDB;