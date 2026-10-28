| 00NYXth - Valentin
| TEST 1

# EcoSort — Evidența colectării selective a deșeurilor

Aplicație desktop JavaFX pentru CEITI: înregistrarea deșeurilor predate, harta colegiului cu punctele de
colectare, statistici, obiective lunare și administrarea categoriilor și a conturilor. Datele sunt păstrate
într-o bază MySQL accesată prin JDBC și proceduri stocate.

## Cerințe

- JDK 21+ (testat cu JDK 25)
- **Docker Desktop** (recomandat) sau MySQL Server 8.0.16+
- Maven nu e necesar: proiectul include `mvnw`

## Pornire rapidă (Docker)

```bash
git clone https://github.com/Mihail-pro/EcoSort.git
cd EcoSort
cp .env.example .env          # Windows: copy .env.example .env  — apoi completați ECOSORT_DB_PASSWORD
docker compose up -d          # pornește MySQL și creează automat baza de date
./mvnw javafx:run             # Windows: mvnw.cmd javafx:run
```

La prima pornire, containerul rulează scripturile din `database/`: tabele, constrângeri, proceduri, date
demonstrative și contul aplicației (`ECOSORT_DB_USER` / `ECOSORT_DB_PASSWORD` din `.env`, doar `EXECUTE`).
Datele rămân în volumul Docker `ecosort-mysql` între reporniri; serverul pornește automat odată cu Docker.

| Comandă | Efect |
|---------|-------|
| `docker compose up -d` | pornește serverul |
| `docker compose down` | îl oprește (datele rămân) |
| `docker compose down -v` | îl oprește și **șterge** baza (la următorul `up` se recreează de la zero) |

Dacă portul 3306 e ocupat de un MySQL instalat local, puneți alt port în `.env` (ex. `ECOSORT_DB_PORT=3310`).
Contul aplicației se creează doar la prima inițializare: dacă schimbați ulterior utilizatorul sau parola din
`.env`, rulați `docker compose down -v` și apoi `up -d`.

## Instalare fără Docker

1. **Configurarea** — copiați `.env.example` în `.env` și completați valorile:

   ```
   ECOSORT_DB_HOST=127.0.0.1
   ECOSORT_DB_PORT=3306
   ECOSORT_DB_NAME=ecosort
   ECOSORT_DB_USER=ecosort_app
   ECOSORT_DB_PASSWORD=<parola aleasă pentru contul aplicației>
   ```

   Variabilele de mediu cu aceleași nume au prioritate față de `.env`. Opțional,
   `ECOSORT_DB_PARAMS` suprascrie parametrii JDBC (implicit `connectTimeout=5000&socketTimeout=30000`).
   `.env` este în `.gitignore` și nu ajunge pe GitHub.

2. **Baza de date** — vezi [`database/README.md`](database/README.md). Pe Windows, cu parola root:

   ```powershell
   powershell -ExecutionPolicy Bypass -File database\install.ps1
   ```

   **Fără parola root** — instanță MySQL separată, doar pentru EcoSort (port 3307, date în
   `%LOCALAPPDATA%\EcoSort\mysql`); puneți `ECOSORT_DB_PORT=3307` în `.env`:

   ```powershell
   powershell -ExecutionPolicy Bypass -File database\local-mysql.ps1 -Install
   ```

   Instanța nu e serviciu Windows: după repornirea calculatorului rulați scriptul din nou (fără `-Install`).

3. **Rulare**

   ```bash
   ./mvnw javafx:run        # Windows: mvnw.cmd javafx:run
   ```

   La **prima pornire** (niciun administrator în baza de date) ecranul de login cere crearea contului de
   administrator. Celelalte conturi se creează din meniul *Utilizatori*.

## Teste

```bash
./mvnw test
```

Testele unitare (Argon2id, validări, configurare) rulează oricând. Testele de integrare
(`RepositoryIntegrareTest`) folosesc baza din `.env` și sunt sărite automat dacă aceasta nu e accesibilă;
nu lasă date în urmă.

## Arhitectură

```
JavaFX (LoginView, DashboardView, InteractiveMap)
   │  Async — operațiile JDBC rulează pe un fir separat, nu pe JavaFX Application Thread
   ▼
service/     AuthService, UtilizatorService, ColectareService  — reguli de business, validări, permisiuni
   ▼
repository/  *Repository — CallableStatement către procedurile stocate (fără SQL concatenat)
   ▼
config/Database — conexiune JDBC din ECOSORT_DB_* / .env
   ▼
MySQL — tabele, constrângeri, proceduri stocate (database/)
```

| Pachet | Rol |
|--------|-----|
| `model` | Entitățile: `Utilizator`, `Rol`, `Categorie`, `PunctColectare`, `Inregistrare`, `ProgresLunar` |
| `security` | `PasswordHasher` — Argon2id (Bouncy Castle), format PHC |
| `session` | `Sesiune` — utilizatorul autentificat și verificarea rolului |

## Roluri

| Funcție | Utilizator | Administrator |
|---------|:---------:|:-------------:|
| Înregistrare deșeu, profil și istoric propriu | ✔ | ✔ |
| Editare / ștergere înregistrări | doar proprii | toate |
| Statistici (grafice pentru tot colegiul) | ✔ | ✔ |
| Obiective lunare: vizualizare / setare | ✔ / — | ✔ / ✔ |
| Puncte de colectare pe hartă (click pe un coș pentru ștergere) | vizualizare | ✔ |
| Categorii, utilizatori | — | ✔ |

## Securitate — limite cunoscute

Parolele sunt stocate doar ca hash Argon2id. Aplicația folosește un cont MySQL care are **doar** drept de
`EXECUTE` pe procedurile stocate. Fiind însă o aplicație desktop conectată direct la MySQL, credențialele
contului de aplicație există pe fiecare calculator unde rulează și pot fi extrase; cine le are poate apela
procedurile direct, ocolind verificările de rol din Java. Pentru o separare reală ar fi nevoie de un server
intermediar. Detalii în [`database/README.md`](database/README.md).
