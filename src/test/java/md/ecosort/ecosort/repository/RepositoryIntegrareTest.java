package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.Categorie;
import md.ecosort.ecosort.model.PunctColectare;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// Rulează pe baza de date configurată în .env / ECOSORT_DB_* (datele demonstrative din 04_seed.sql).
// Se sare automat dacă baza nu este accesibilă. Nu lasă date în urmă: scrierile testate
// sunt cele respinse de procedurile stocate înainte de orice INSERT/DELETE.
class RepositoryIntegrareTest {

    private final CategorieRepository categorii = new CategorieRepository();
    private final PunctRepository puncte = new PunctRepository();
    private final InregistrareRepository inregistrari = new InregistrareRepository();
    private final ObiectivRepository obiective = new ObiectivRepository();
    private final UtilizatorRepository utilizatori = new UtilizatorRepository();

    @BeforeAll
    static void bazaDisponibila() {
        boolean ok;
        try (Connection c = Database.conexiune()) {
            ok = c.isValid(3);
        } catch (Exception e) {
            System.err.println("Teste de integrare sărite: " + e);
            ok = false;
        }
        assumeTrue(ok, "Baza de date nu este disponibilă — testele de integrare sunt sărite.");
    }

    private Categorie categorie(String denumire) {
        return categorii.lista().stream().filter(c -> c.denumire().equals(denumire)).findFirst().orElseThrow();
    }

    @Test
    void citireaNomenclatoarelorSiRapoartelor() {
        List<Categorie> lista = categorii.lista();
        assertTrue(lista.size() >= 5, "categorii din seed");
        assertTrue(lista.stream().allMatch(c -> c.culoare().matches("#[0-9A-F]{6}")));

        List<PunctColectare> p = puncte.lista();
        assertTrue(p.stream().anyMatch(x -> x.bloc().equals("C") && x.incapere().equals("Bufet")));

        assertEquals(lista.size(), inregistrari.totaluriPeCategorii(null, null, null).size());
        assertEquals(lista.size(), obiective.progres(YearMonth.now()).size());
        assertNotNull(inregistrari.lista(null, lista.get(0).id(), LocalDate.now().minusYears(1), LocalDate.now()));
        assertNotNull(new StareCosRepository().lista());
    }

    @Test
    void regulileDinProceduriSuntAplicate() {
        Categorie plastic = categorie("Plastic");

        // Categoria e folosită de puncte de colectare → nu se poate șterge.
        DataException e1 = assertThrows(DataException.class, () -> categorii.sterge(plastic.id()));
        assertTrue(e1.getMessage().contains("folosită"), e1.getMessage());

        // Denumire duplicată (comparația ignoră majusculele și diacriticele).
        DataException e2 = assertThrows(DataException.class, () -> categorii.adauga("plastic", "#000000"));
        assertTrue(e2.getMessage().contains("Există deja"), e2.getMessage());

        // Un punct de altă categorie nu acceptă Plastic (verificat înainte de INSERT).
        PunctColectare altPunct = puncte.lista().stream()
                .filter(x -> !x.categorie().equals(plastic)).findFirst().orElseThrow();
        DataException e3 = assertThrows(DataException.class,
                () -> inregistrari.adauga(1, plastic.id(), altPunct.id(), BigDecimal.ONE, LocalDate.now()));
        assertTrue(e3.getMessage().contains("nu acceptă"), e3.getMessage());

        // Înregistrare inexistentă.
        assertThrows(DataException.class, () -> inregistrari.sterge(Integer.MAX_VALUE, null));
    }

    @Test
    void parametriiNuSuntInterpretatiCaSql() {
        assertTrue(utilizatori.cautaPentruAutentificare("' OR '1'='1").isEmpty());
        assertTrue(utilizatori.cautaPentruAutentificare("nu_exista_" + System.nanoTime()).isEmpty());
    }
}
