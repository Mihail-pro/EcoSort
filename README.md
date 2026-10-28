# ♻ EcoSort — colectarea selectivă a deșeurilor la CEITI

![EcoSort](docs/ecosort-banner.png)

**EcoSort** este o aplicație pentru calculator în care elevii și profesorii notează deșeurile reciclate
(plastic, hârtie, sticlă, metal, baterii), văd pe harta colegiului unde sunt coșurile și urmăresc cât s-a
strâns în fiecare lună.

---

## 🚀 Cum pornești aplicația (3 pași)

### Pasul 1 — Instalează două programe *(doar prima dată)*

| Program | Link | Cum instalezi |
|---------|------|---------------|
| **Java** | [adoptium.net](https://adoptium.net) | Apasă butonul mare de descărcare → deschide fișierul → *Next, Next, Finish* |
| **Docker Desktop** | [docker.com](https://www.docker.com/products/docker-desktop) | *Download for Windows* → deschide fișierul → *Next, Next, Finish*. Dacă cere repornirea calculatorului, acceptă. |

> 💡 Nu trebuie să instalezi altceva. Contul Docker nu e necesar — poți apăsa *Skip* la autentificare.

### Pasul 2 — Descarcă proiectul

1. Sus pe această pagină apasă butonul verde **`<> Code`** → **Download ZIP**.
2. Deschide fișierul descărcat și extrage folderul (click dreapta → *Extract All…* / *Extrage tot…*).

### Pasul 3 — Pornește

În folderul extras, fă **dublu-click pe `porneste.bat`**.

O fereastră neagră îți arată ce se întâmplă. **Prima dată durează câteva minute** (se descarcă baza de date
și bibliotecile) — lasă fereastra deschisă. La final se deschide aplicația.

> 💡 Dacă Windows întreabă „Sigur doriți să rulați?”, apasă **Run** / **Executare**
> (sau *More info → Run anyway*).

### Prima intrare în aplicație

La prima pornire aplicația îți cere să creezi **contul de administrator**: alege un nume de utilizator,
scrie numele tău și o parolă de minimum 8 caractere.

![Prima pornire](docs/screenshots/00-prima-pornire.png)

Data viitoare intri cu acest cont. Conturile pentru elevi și profesori le creezi din meniul **Utilizatori**.

### Data viitoare

Doar **dublu-click pe `porneste.bat`**. Datele tale rămân salvate.

---

## 🖥️ Ce poți face în aplicație

### Autentificare
Fiecare persoană intră cu contul ei. Parolele sunt păstrate criptat.

![Autentificare](docs/screenshots/01-autentificare.png)

### Hartă și coșuri
Harta colegiului, pe etaje. Punctele colorate sunt coșurile de reciclare — culoarea arată ce se aruncă în
ele. Administratorul adaugă un coș cu **+ Punct colectare** și apoi click pe o încăpere; un click pe coș îl
șterge.

![Hartă și coșuri](docs/screenshots/02-harta.png)

### Înregistrare deșeu
Notezi ce ai predat: categoria, coșul (dacă vrei), cantitatea în kilograme și data.

![Înregistrare deșeu](docs/screenshots/03-inregistrare.png)

### Statistici
Grafice cu cât s-a reciclat în tot colegiul — azi, luna aceasta, anul acesta sau în total — și lista
înregistrărilor. O înregistrare se poate modifica (dublu-click) sau șterge.

![Statistici](docs/screenshots/04-statistici.png)

### Obiective lunare
Ținta de reciclare pentru fiecare categorie în luna curentă și cât s-a atins până acum.
Administratorul stabilește țintele.

![Obiective lunare](docs/screenshots/05-obiective.png)

### Profil și istoric
Cât ai reciclat tu și lista tuturor contribuțiilor tale.

![Profil și istoric](docs/screenshots/06-profil.png)

### Categorii *(administrator)*
Adaugi, redenumești sau ștergi categorii de deșeuri și le alegi culoarea.

![Categorii](docs/screenshots/07-categorii.png)

### Utilizatori *(administrator)*
Creezi conturi, schimbi rolul (utilizator / administrator), dezactivezi un cont sau resetezi o parolă.

![Utilizatori](docs/screenshots/08-utilizatori.png)

---

## ❓ Dacă ceva nu merge

| Ce vezi | Ce faci |
|---------|---------|
| *Java nu este instalat* | Instalează Java (Pasul 1) și rulează din nou `porneste.bat` |
| *Docker Desktop nu este instalat* | Instalează Docker Desktop (Pasul 1) și rulează din nou `porneste.bat` |
| *Docker Desktop nu a pornit* | Deschide Docker Desktop din meniul Start, așteaptă „Engine running”, apoi rulează din nou `porneste.bat` |
| În aplicație: *Nu se poate conecta la baza de date* | Închide aplicația și pornește din nou cu `porneste.bat` |
| Nu merge nimic când dai dublu-click | Verifică că ai **extras** arhiva ZIP (Pasul 2) — nu rula `porneste.bat` din interiorul arhivei |
| Altă eroare | Fă o poză la fereastra neagră și trimite-o echipei |

---

## 👩‍💻 Pentru dezvoltatori

Configurare, rulare din IntelliJ / terminal, baza de date, teste, arhitectură și securitate:
**[docs/DEZVOLTATORI.md](docs/DEZVOLTATORI.md)**.

## 👥 Echipa

- [00NYXth](https://github.com/00NYXth) — Valentin
- [Mihail-pro](https://github.com/Mihail-pro)
