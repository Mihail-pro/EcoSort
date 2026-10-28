-- =====================================================================
-- EcoSort — 01. Schema bazei de date (tabele)
-- Se poate executa repetat: nu șterge și nu modifică date existente.
-- Cheile străine, UNIQUE, CHECK și indexurile sunt în 02_constraints_indexes.sql.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS ecosort
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE ecosort;

-- Conturile aplicației. Parola se păstrează doar ca hash Argon2id (format PHC).
CREATE TABLE IF NOT EXISTS utilizatori (
    id            INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)   NOT NULL,
    nume_complet  VARCHAR(100)  NOT NULL,
    parola_hash   VARCHAR(255)  NOT NULL,
    rol           ENUM('ADMIN', 'UTILIZATOR') NOT NULL DEFAULT 'UTILIZATOR',
    activ         BOOLEAN       NOT NULL DEFAULT TRUE,
    creat_la      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Categoriile de deșeuri (Plastic, Hârtie, ...). Culoarea e folosită în hartă și grafice.
CREATE TABLE IF NOT EXISTS categorii_deseuri (
    id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
    denumire   VARCHAR(50)  NOT NULL,
    culoare    CHAR(7)      NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Coșurile de colectare amplasate într-o încăpere de pe harta colegiului.
-- `incapere` este identificatorul încăperii de pe hartă (codul sau denumirea ei).
CREATE TABLE IF NOT EXISTS puncte_colectare (
    id            INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    denumire      VARCHAR(100)     NOT NULL,
    bloc          VARCHAR(10)      NOT NULL,
    etaj          TINYINT UNSIGNED NOT NULL,
    incapere      VARCHAR(100)     NOT NULL,
    categorie_id  INT UNSIGNED     NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Deșeurile predate. Punctul de colectare este opțional.
CREATE TABLE IF NOT EXISTS inregistrari_deseuri (
    id              INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    utilizator_id   INT UNSIGNED  NOT NULL,
    categorie_id    INT UNSIGNED  NOT NULL,
    punct_id        INT UNSIGNED  NULL,
    cantitate       DECIMAL(10,2) NOT NULL,
    data_colectare  DATE          NOT NULL,
    creat_la        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Ținta lunară (kg) pentru fiecare categorie.
CREATE TABLE IF NOT EXISTS obiective_lunare (
    id               INT UNSIGNED      NOT NULL AUTO_INCREMENT,
    categorie_id     INT UNSIGNED      NOT NULL,
    an               SMALLINT UNSIGNED NOT NULL,
    luna             TINYINT UNSIGNED  NOT NULL,
    cantitate_tinta  DECIMAL(10,2)     NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;
