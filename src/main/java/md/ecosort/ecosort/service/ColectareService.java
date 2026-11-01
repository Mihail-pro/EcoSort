package md.ecosort.ecosort.service;

import md.ecosort.ecosort.model.*;
import md.ecosort.ecosort.repository.*;
import md.ecosort.ecosort.session.Sesiune;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

// Regulile de business pentru categorii, puncte de colectare, înregistrări și obiective.
// Fiecare operație verifică sesiunea: administrarea e permisă doar rolului ADMIN,
// iar un utilizator obișnuit vede și modifică doar propriile înregistrări.
public class ColectareService {

    public enum Perioada {
        AZI("Astăzi"), LUNA("Luna aceasta"), AN("Anul acesta"), TOTAL("Total");

        private final String eticheta;

        Perioada(String eticheta) { this.eticheta = eticheta; }

        LocalDate deLa(LocalDate azi) {
            return switch (this) {
                case AZI -> azi;
                case LUNA -> azi.withDayOfMonth(1);
                case AN -> azi.withDayOfYear(1);
                case TOTAL -> null;
            };
        }

        LocalDate panaLa(LocalDate azi) { return this == TOTAL ? null : azi; }

        @Override
        public String toString() { return eticheta; }
    }

    private final CategorieRepository categorii = new CategorieRepository();
    private final PunctRepository puncte = new PunctRepository();
    private final InregistrareRepository inregistrari = new InregistrareRepository();
    private final ObiectivRepository obiective = new ObiectivRepository();
    private final StareCosRepository stare = new StareCosRepository();

    // ---------- Categorii ----------

    public List<Categorie> categorii() {
        Sesiune.curent();
        return categorii.lista();
    }

    public void adaugaCategorie(String denumire, String culoare) {
        Sesiune.cereAdmin();
        categorii.adauga(Validari.text(denumire, "Denumirea", 50), Validari.culoare(culoare));
    }

    public void modificaCategorie(int id, String denumire, String culoare) {
        Sesiune.cereAdmin();
        categorii.modifica(id, Validari.text(denumire, "Denumirea", 50), Validari.culoare(culoare));
    }

    public void stergeCategorie(int id) {
        Sesiune.cereAdmin();
        categorii.sterge(id);
    }

    // ---------- Puncte de colectare ----------

    public List<PunctColectare> puncte() {
        Sesiune.curent();
        return puncte.lista();
    }

    public void adaugaPunct(String denumire, String bloc, int etaj, String incapere, Categorie categorie) {
        Sesiune.cereAdmin();
        if (categorie == null) throw new IllegalArgumentException("Alegeți categoria.");
        puncte.adauga(Validari.text(denumire, "Denumirea", 100), Validari.text(bloc, "Blocul", 10),
                etaj, Validari.text(incapere, "Încăperea", 100), categorie.id());
    }

    public void modificaPunct(int id, String denumire, Categorie categorie) {
        Sesiune.cereAdmin();
        if (categorie == null) throw new IllegalArgumentException("Alegeți categoria.");
        puncte.modifica(id, Validari.text(denumire, "Denumirea", 100), categorie.id());
    }

    public void stergePunct(int id) {
        Sesiune.cereAdmin();
        puncte.sterge(id);
    }

    // ----------- Stare Cos ------------
    public List<StareCos> stareCosuri() {
        Sesiune.curent();
        return stare.lista();
    }

    // ---------- Înregistrări ----------

    public void inregistreaza(Categorie categorie, PunctColectare punct, BigDecimal cantitate, LocalDate data) {
        Utilizator eu = Sesiune.curent();
        if (categorie == null) throw new IllegalArgumentException("Alegeți categoria.");
        inregistrari.adauga(eu.id(), categorie.id(), punct == null ? null : punct.id(),
                Validari.cantitate(cantitate, false), Validari.dataColectare(data));
    }

    public boolean poateModifica(Inregistrare inregistrare) {
        Utilizator eu = Sesiune.curent();
        return eu.esteAdmin() || inregistrare.utilizatorId() == eu.id();
    }

    public void modificaInregistrare(Inregistrare inregistrare, Categorie categorie, PunctColectare punct,
                                     BigDecimal cantitate, LocalDate data) {
        if (!poateModifica(inregistrare)) throw new SecurityException("Puteți modifica doar propriile înregistrări.");
        if (categorie == null) throw new IllegalArgumentException("Alegeți categoria.");
        inregistrari.modifica(inregistrare.id(), proprietarImpus(), categorie.id(),
                punct == null ? null : punct.id(), Validari.cantitate(cantitate, false), Validari.dataColectare(data));
    }

    public void stergeInregistrare(Inregistrare inregistrare) {
        if (!poateModifica(inregistrare)) throw new SecurityException("Puteți șterge doar propriile înregistrări.");
        inregistrari.sterge(inregistrare.id(), proprietarImpus());
    }

    // Administratorul vede toate înregistrările; ceilalți doar pe ale lor. categorie = null → toate.
    public List<Inregistrare> inregistrari(Perioada perioada, Categorie categorie) {
        LocalDate azi = LocalDate.now();
        return inregistrari.lista(proprietarImpus(), categorie == null ? null : categorie.id(),
                perioada.deLa(azi), perioada.panaLa(azi));
    }

    public List<Inregistrare> istoriculMeu() {
        return inregistrari.lista(Sesiune.curent().id(), null, null, null);
    }

    // Totalurile sunt la nivelul întregului colegiu, vizibile tuturor.
    public Map<Categorie, BigDecimal> totaluriPeCategorii(Perioada perioada) {
        Sesiune.curent();
        LocalDate azi = LocalDate.now();
        return inregistrari.totaluriPeCategorii(perioada.deLa(azi), perioada.panaLa(azi), null);
    }

    // ---------- Obiective lunare ----------

    public List<ProgresLunar> progres(YearMonth luna) {
        Sesiune.curent();
        return obiective.progres(luna);
    }

    // O țintă de 0 kg elimină obiectivul pentru luna respectivă.
    public void seteazaObiectiv(Categorie categorie, YearMonth luna, BigDecimal tinta) {
        Sesiune.cereAdmin();
        if (categorie == null) throw new IllegalArgumentException("Alegeți categoria.");
        BigDecimal t = Validari.cantitate(tinta, true);
        if (t.signum() == 0) obiective.sterge(categorie.id(), luna);
        else obiective.seteaza(categorie.id(), luna, t);
    }

    // null pentru administrator (orice înregistrare), altfel id-ul utilizatorului curent.
    private static Integer proprietarImpus() {
        Utilizator eu = Sesiune.curent();
        return eu.esteAdmin() ? null : eu.id();
    }
}
