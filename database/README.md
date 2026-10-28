# EcoSort — baza de date MySQL

Toate scripturile sunt idempotente: se pot rula de mai multe ori fără să șteargă date.
Necesită MySQL 8.0.16+ (pentru `CHECK`).

## Docker (recomandat, orice sistem de operare)

Din rădăcina proiectului: `docker compose up -d`. Containerul rulează automat pașii de mai jos
(prin `docker/init.sh`) la prima pornire. Detalii în README-ul principal.

## Instalare rapidă (Windows)

Completați `.env` (vezi `.env.example`), apoi din rădăcina proiectului:

```powershell
powershell -ExecutionPolicy Bypass -File database\install.ps1
```

Scriptul cere parola `root` (nu o salvează), rulează pașii de mai jos și creează contul
`ECOSORT_DB_USER` cu parola `ECOSORT_DB_PASSWORD` din `.env`.

### Fără parola root a serverului existent

`local-mysql.ps1` creează o instanță MySQL separată (același MySQL instalat, date în
`%LOCALAPPDATA%\EcoSort\mysql`, port 3307, accesibilă doar local) și apoi rulează `install.ps1`:

```powershell
powershell -ExecutionPolicy Bypass -File database\local-mysql.ps1 -Install
```

Puneți `ECOSORT_DB_PORT=3307` în `.env`. Instanța nu este serviciu Windows: după o repornire a
calculatorului, rulați din nou scriptul fără `-Install` ca s-o porniți.

## Ordinea de execuție

Rulați cu un cont care poate crea baze de date și utilizatori (de ex. `root`):

| # | Fișier | Ce face |
|---|--------|---------|
| 1 | `01_schema.sql` | Creează baza `ecosort` și tabelele |
| 2 | `02_constraints_indexes.sql` | Chei străine, UNIQUE, CHECK, indexuri |
| 3 | `03_procedures/utilizatori.sql` | Autentificare, profil, administrare conturi |
| 4 | `03_procedures/categorii.sql` | CRUD categorii |
| 5 | `03_procedures/puncte.sql` | CRUD puncte de colectare |
| 6 | `03_procedures/inregistrari.sql` | CRUD + filtrare înregistrări |
| 7 | `03_procedures/obiective.sql` | Obiective lunare |
| 8 | `03_procedures/rapoarte.sql` | Totaluri și progres lunar |
| 9 | `04_seed.sql` | *(opțional)* categorii și coșuri demonstrative |
| 10 | `05_app_user.sql` | Contul `ecosort_app` (doar `EXECUTE`) |

### MySQL Workbench

Deschideți fiecare fișier (File → Open SQL Script) în ordinea de mai sus și executați-l cu ⚡ (Execute All).
Pentru pasul 10, în **același tab**, rulați întâi:

```sql
SET @app_password = 'o-parola-puternica-de-minim-12-caractere';
```

### Linie de comandă (din folderul `database/`)

```bash
mysql -u root -p < 01_schema.sql
mysql -u root -p < 02_constraints_indexes.sql
for f in utilizatori categorii puncte inregistrari obiective rapoarte; do mysql -u root -p < 03_procedures/$f.sql; done
mysql -u root -p < 04_seed.sql
mysql -u root -p -e "SET @app_password='o-parola-puternica'; SOURCE 05_app_user.sql;"
```

Apoi completați `.env` din rădăcina proiectului cu `ECOSORT_DB_USER=ecosort_app` și parola aleasă.

## Model de date (3NF)

```
utilizatori 1 ──< inregistrari_deseuri >── 1 categorii_deseuri 1 ──< obiective_lunare
                         │                         1
                         └──>0..1 puncte_colectare >┘
```

- `inregistrari_deseuri.punct_id` e opțional; la ștergerea punctului devine `NULL` (istoricul rămâne).
- O categorie folosită în înregistrări/puncte nu poate fi ștearsă; obiectivele ei se șterg în cascadă.
- Un punct de colectare e unic pe (bloc, etaj, încăpere, categorie); înregistrarea legată de un punct
  trebuie să aibă aceeași categorie (verificat în proceduri).
- Cantitățile sunt `DECIMAL(10,2)`.

## Securitate și limite

- Parolele sunt stocate doar ca hash **Argon2id** (format PHC); hash-ul și verificarea se fac în Java.
- Aplicația se conectează cu `ecosort_app`, care are **doar** `EXECUTE` pe procedurile din `ecosort`:
  nu poate rula `SELECT`/`UPDATE` direct pe tabele și nu poate modifica schema.
- **Limită:** fiind o aplicație desktop care vorbește direct cu MySQL, oricine are credențialele
  `ecosort_app` (de ex. extrase de pe un calculator unde rulează aplicația) poate apela direct procedurile,
  ocolind verificările de rol din Java (inclusiv citirea hash-urilor prin `sp_utilizator_autentificare`).
  Pentru o separare reală a drepturilor ar fi nevoie de un server intermediar. Mitigări: cont dedicat cu
  privilegii minime, MySQL accesibil doar din rețeaua colegiului, parole Argon2id.
