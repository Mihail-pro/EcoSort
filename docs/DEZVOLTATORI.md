# EcoSort — documentație pentru dezvoltatori

Pentru pornirea simplă a aplicației vezi [README](../README.md). Aici sunt detaliile tehnice.

- [Tehnologii](#tehnologii)
- [Configurare (`.env`)](#configurare-env)
- [Rulare manuală](#rulare-manuală)
- [Baza de date](#baza-de-date)
- [Teste](#teste)
- [Arhitectură](#arhitectură)
- [Securitate](#securitate)

## Tehnologii

| | |
|---|---|
| Limbaj | Java 21+ (testat cu JDK 25) |
| Interfață | JavaFX 21 (fără FXML) |
| Bază de date | MySQL 8.0, proceduri stocate, JDBC (`mysql-connector-j`) |
| Parole | Argon2id (Bouncy Castle) |
| Build | Maven (prin `mvnw`, nu trebuie instalat) |
| Server MySQL local | Docker Compose |

## Configurare (`.env`)

Aplicația și `docker compose` citesc același fișier `.env` din rădăcina proiectului (creat automat de
`porneste.bat` sau copiat manual din `.env.example`):

| Cheie | Exemplu | Rol |
|-------|---------|-----|
| `ECOSORT_DB_HOST` | `127.0.0.1` | adresa serverului MySQL |
| `ECOSORT_DB_PORT` | `3306` | portul (3310 dacă 3306 e ocupat) |
| `ECOSORT_DB_NAME` | `ecosort` | numele bazei |
| `ECOSORT_DB_USER` | `ecosort_app` | contul aplicației (doar `EXECUTE`) |
| `ECOSORT_DB_PASSWORD` | — | parola contului aplicației |
| `ECOSORT_DB_PARAMS` | *(opțional)* | parametri JDBC, implicit `connectTimeout=5000&socketTimeout=30000` |

- Variabilele de mediu cu aceleași nume au prioritate față de `.env`.
- `.env` este în `.gitignore`. Nu se scriu credențiale în cod.
- Aplicația caută `.env` în directorul de lucru: rulați din rădăcina proiectului.

## Rulare manuală

```bash
docker compose up -d --wait      # baza de date
./mvnw javafx:run                # Windows: mvnw.cmd javafx:run
```

**IntelliJ IDEA:** *File → Open* → folderul proiectului → configurarea **EcoSortApplication** → ▶ Run.
„Working directory” trebuie să rămână rădăcina proiectului.

| Comandă Docker | Efect |
|----------------|-------|
| `docker compose up -d` | pornește MySQL (la prima pornire creează baza din `database/`) |
| `docker compose down` | oprește MySQL; datele rămân în volumul `ecosort-mysql` |
| `docker compose down -v` | oprește MySQL și **șterge** baza |
| `docker compose logs mysql` | jurnalul serverului |

Contul aplicației se creează doar la prima inițializare a volumului. Dacă schimbați `ECOSORT_DB_USER` sau
`ECOSORT_DB_PASSWORD` după aceea: `docker compose down -v` și `docker compose up -d` (șterge datele).

## Baza de date

Scripturile sunt în [`database/`](../database/README.md) — schema, constrângerile, procedurile stocate,
datele demonstrative și ordinea de execuție. Toate se pot rula repetat fără pierderi de date.

Fără Docker (Windows):

```powershell
# cu parola root a unui MySQL instalat
powershell -ExecutionPolicy Bypass -File database\install.ps1

# fără parola root: instanță MySQL separată pe portul 3307 (puneți ECOSORT_DB_PORT=3307 în .env)
powershell -ExecutionPolicy Bypass -File database\local-mysql.ps1 -Install
```

Instanța din `local-mysql.ps1` nu e serviciu Windows: după repornirea calculatorului se pornește din nou
cu același script, fără `-Install`.

## Teste

```bash
./mvnw test
```

| Test | Ce verifică |
|------|-------------|
| `PasswordHasherTest` | hash Argon2id, verificare, diacritice, hash-uri invalide |
| `ValidariTest` | cantități `DECIMAL(10,2)`, date, conturi, parole, culori |
| `DatabaseConfigTest` | citirea `.env` și prioritatea variabilelor de mediu |
| `RepositoryIntegrareTest` | proceduri stocate pe baza reală, reguli de business, SQL injection |

Testele de integrare folosesc baza din `.env`; dacă nu e accesibilă, sunt sărite automat. Nu lasă date în urmă.

## Arhitectură

```
JavaFX (LoginView, DashboardView, InteractiveMap)
   │  Async — operațiile JDBC rulează pe un fir separat, nu pe JavaFX Application Thread
   ▼
service/     AuthService, UtilizatorService, ColectareService — reguli de business, validări, permisiuni
   ▼
repository/  *Repository — CallableStatement către procedurile stocate (fără SQL concatenat)
   ▼
config/      Database — conexiune JDBC din ECOSORT_DB_* / .env
   ▼
MySQL        tabele, constrângeri, proceduri stocate (database/)
```

| Pachet | Conținut |
|--------|----------|
| `model` | `Utilizator`, `Rol`, `Categorie`, `PunctColectare`, `Inregistrare`, `ProgresLunar` |
| `security` | `PasswordHasher` — Argon2id, format PHC |
| `session` | `Sesiune` — utilizatorul autentificat și verificarea rolului |

### Roluri

| Funcție | Utilizator | Administrator |
|---------|------------|---------------|
| Înregistrare deșeu, profil și istoric propriu | da | da |
| Editare și ștergere înregistrări | doar proprii | toate |
| Statistici (grafice pentru tot colegiul) | da | da |
| Obiective lunare | vizualizare | vizualizare și setare |
| Puncte de colectare pe hartă | vizualizare | adăugare și ștergere |
| Categorii, utilizatori | nu | da |

## Securitate

- Parolele sunt stocate doar ca hash Argon2id; verificarea se face în Java.
- Toate apelurile SQL folosesc `CallableStatement` cu parametri.
- Contul MySQL al aplicației are **doar** drept de `EXECUTE` pe procedurile stocate.
- **Limită cunoscută:** fiind o aplicație desktop conectată direct la MySQL, credențialele contului de
  aplicație există pe fiecare calculator unde rulează și pot fi extrase; cine le are poate apela
  procedurile direct, ocolind verificările de rol din Java. O separare reală a drepturilor ar necesita un
  server intermediar.
