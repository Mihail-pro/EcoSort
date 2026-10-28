package md.ecosort.ecosort.model;

// incapere = identificatorul încăperii de pe hartă (codul ei sau, dacă lipsește, denumirea).
public record PunctColectare(int id, String denumire, String bloc, int etaj, String incapere, Categorie categorie) {

    @Override
    public String toString() { return denumire; }
}
