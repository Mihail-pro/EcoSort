-- =====================================================================
-- EcoSort — 04. Date inițiale / demonstrative
-- Opțional. INSERT IGNORE: rândurile existente (aceeași cheie UNIQUE) sunt păstrate.
-- Nu se creează conturi: primul administrator se creează din aplicație la prima pornire.
-- =====================================================================

USE ecosort;

INSERT IGNORE INTO categorii_deseuri (denumire, culoare) VALUES
    ('Plastic', '#3B82F6'),
    ('Hârtie',  '#F59E0B'),
    ('Sticlă',  '#10B981'),
    ('Metal',   '#6B7280'),
    ('Baterii', '#EF4444');

-- Coșuri demonstrative. `incapere` = codul încăperii de pe hartă sau, dacă lipsește, denumirea ei.
INSERT IGNORE INTO puncte_colectare (denumire, bloc, etaj, incapere, categorie_id)
SELECT d.denumire, d.bloc, d.etaj, d.incapere, c.id
FROM (
    SELECT 'Coș plastic — Bufet'        AS denumire, 'C' AS bloc, 1 AS etaj, 'Bufet'                  AS incapere, 'Plastic' AS categorie
    UNION ALL SELECT 'Coș metal — Bufet',          'C', 1, 'Bufet',                  'Metal'
    UNION ALL SELECT 'Coș hârtie — Bibliotecă',    'C', 1, 'Bibliotecă',             'Hârtie'
    UNION ALL SELECT 'Baterii — Sala de Lectură',  'C', 1, 'Sala de Lectură',        'Baterii'
    UNION ALL SELECT 'Coș sticlă — Coridor A',     'A', 1, 'Coridor Etaj 1, bloc A', 'Sticlă'
) d
JOIN categorii_deseuri c ON c.denumire = d.categorie;
