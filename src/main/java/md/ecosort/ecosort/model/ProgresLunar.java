package md.ecosort.ecosort.model;

import java.math.BigDecimal;

// tinta = 0 înseamnă că pentru categoria respectivă nu e setat un obiectiv în acea lună.
public record ProgresLunar(Categorie categorie, BigDecimal tinta, BigDecimal colectat) {

    public double procent() {
        return tinta.signum() > 0 ? colectat.doubleValue() / tinta.doubleValue() : 0;
    }

    public boolean atins() {
        return tinta.signum() > 0 && colectat.compareTo(tinta) >= 0;
    }
}
