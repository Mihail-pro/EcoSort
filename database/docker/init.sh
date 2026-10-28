#!/bin/bash
# EcoSort — inițializarea bazei în containerul MySQL (docker-compose.yml).
# Imaginea oficială rulează acest script o singură dată, când volumul de date e gol.
# Execută scripturile din database/ în ordinea din database/README.md și creează
# contul aplicației (ECOSORT_DB_USER / ECOSORT_DB_PASSWORD) cu drept doar de EXECUTE.
#
# Totul rulează într-un subshell: imaginea poate face `source` pe acest fișier,
# iar opțiunile shell-ului nu trebuie să-i afecteze propriul script de pornire.
(
    set -euo pipefail

    sql() {
        MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql --protocol=socket -uroot --default-character-set=utf8mb4 "$@"
    }
    # Valoare SQL între apostrofuri (se dublează ' și \).
    quote() {
        printf "'%s'" "$(printf '%s' "$1" | sed -e 's/\\/\\\\/g' -e "s/'/''/g")"
    }

    cd /ecosort-db
    for f in 01_schema.sql 02_constraints_indexes.sql \
             03_procedures/utilizatori.sql 03_procedures/categorii.sql 03_procedures/puncte.sql \
             03_procedures/inregistrari.sql 03_procedures/obiective.sql 03_procedures/rapoarte.sql \
             04_seed.sql; do
        echo "[EcoSort] $f"
        sql < "$f"
    done

    echo "[EcoSort] 05_app_user.sql (cont: $ECOSORT_DB_USER)"
    # Aplicația ajunge la container prin rețeaua Docker, deci contul acceptă orice gazdă ('%');
    # portul e publicat doar pe 127.0.0.1 al calculatorului (vezi docker-compose.yml).
    sql <<EOF
SET @app_user = $(quote "$ECOSORT_DB_USER");
SET @app_password = $(quote "$ECOSORT_DB_PASSWORD");
SET @app_host = '%';
source /ecosort-db/05_app_user.sql
EOF

    echo "[EcoSort] Baza de date este gata."
)
