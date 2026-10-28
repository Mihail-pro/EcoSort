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

## Cum rulezi proiectul pe un alt calculator (pas cu pas)

> Pe GitHub se află doar fișierele proiectului, nu și baza de date. Docker construiește pe calculatorul tău
> o bază **nouă**, cu aceeași structură și cu datele demonstrative (categorii, coșuri). Conturile și
> înregistrările de pe alte calculatoare nu se transferă.

### Pasul 1 — Instalează programele necesare (o singură dată)

| Program | De unde | Verificare în terminal |
|---------|---------|------------------------|
| Git | <https://git-scm.com> | `git --version` |
| Java JDK 21 sau mai nou | <https://adoptium.net> | `java -version` |
| Docker Desktop | <https://www.docker.com/products/docker-desktop> | `docker --version` |

După instalare, **deschide Docker Desktop** și așteaptă mesajul „Engine running”. Pe Windows, la prima
instalare, Docker poate cere activarea WSL 2 sau o repornire — urmează instrucțiunile lui.
MySQL **nu** trebuie instalat separat.

### Pasul 2 — Descarcă proiectul

Deschide un terminal (PowerShell / Command Prompt) în folderul unde vrei proiectul:

```bash
git clone https://github.com/Mihail-pro/EcoSort.git
cd EcoSort
```

### Pasul 3 — Creează fișierul de configurare `.env`

```bash
copy .env.example .env        # Linux / macOS: cp .env.example .env
notepad .env                  # Linux / macOS: nano .env
```

Completează parola (minim 8 caractere) și salvează:

```
ECOSORT_DB_HOST=127.0.0.1
ECOSORT_DB_PORT=3306
ECOSORT_DB_NAME=ecosort
ECOSORT_DB_USER=ecosort_app
ECOSORT_DB_PASSWORD=OParolaAleasaDeTine
```

Dacă pe calculator este deja instalat MySQL, portul 3306 e ocupat: pune `ECOSORT_DB_PORT=3310`.
`.env` este în `.gitignore` și nu ajunge pe GitHub.

### Pasul 4 — Pornește baza de date

```bash
docker compose up -d --wait
```

Prima dată durează 1–2 minute (se descarcă imaginea MySQL și se creează baza). E gata când comanda se
termină cu `Container ecosort-mysql-1 Healthy`. Verificare opțională:

```bash
docker compose logs mysql     # trebuie să apară: [EcoSort] Baza de date este gata.
```

### Pasul 5 — Pornește aplicația

**Din terminal** (în folderul `EcoSort`):

```bash
mvnw.cmd javafx:run           # Linux / macOS: ./mvnw javafx:run
```

**Din IntelliJ IDEA:** *File → Open* → folderul `EcoSort` (IntelliJ recunoaște proiectul Maven și descarcă
bibliotecile), apoi alege configurarea **EcoSortApplication** din dreapta-sus și apasă ▶ *Run*.
Aplicația caută `.env` în directorul de lucru — lăsați „Working directory” pe rădăcina proiectului.

Prima rulare durează câteva minute (se descarcă bibliotecile Java).

### Pasul 6 — Prima autentificare

Ecranul de login apare cu titlul **„PRIMA PORNIRE — CREEAZĂ CONTUL DE ADMINISTRATOR”**. Completează numele
de utilizator, numele complet și parola — acesta devine administratorul. Celelalte conturi se creează din
meniul **Utilizatori**.

### Folosirea de zi cu zi

| Ce vrei | Comandă |
|---------|---------|
| Pornești aplicația | `mvnw.cmd javafx:run` (sau ▶ în IntelliJ) |
| Oprești baza de date | `docker compose down` — datele rămân |
| Pornești din nou baza | `docker compose up -d` |
| Ștergi baza și o iei de la zero | `docker compose down -v`, apoi `docker compose up -d` |

După repornirea calculatorului, baza pornește singură, dacă Docker Desktop este deschis.

### Dacă ceva nu merge

| Problemă | Soluție |
|----------|---------|
| `port is already allocated` | Schimbă `ECOSORT_DB_PORT` în `.env` (ex. 3310) și rulează din nou `docker compose up -d` |
| `Completați ECOSORT_DB_PASSWORD în .env` | Lipsește parola din `.env` (Pasul 3) |
| În aplicație: „Nu se poate conecta la baza de date” | Docker Desktop nu e pornit sau containerul e oprit → `docker compose up -d` |
| În aplicație: „Lipsește configurația ECOSORT_DB_…” | `.env` lipsește sau aplicația e pornită din alt folder decât rădăcina proiectului |
| „Acces refuzat la baza de date” | Parola din `.env` a fost schimbată după crearea bazei → `docker compose down -v` și `docker compose up -d` (**șterge datele**) |
| `java` / `git` / `docker` nu e recunoscut | Programul nu e instalat sau terminalul a fost deschis înainte de instalare — redeschide-l |

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
