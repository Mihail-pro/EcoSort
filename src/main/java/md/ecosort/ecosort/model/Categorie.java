package md.ecosort.ecosort.model;

// culoare: "#RRGGBB"
public record Categorie(int id, String denumire, String culoare) {

    @Override
    public String toString() { return denumire; }
}
