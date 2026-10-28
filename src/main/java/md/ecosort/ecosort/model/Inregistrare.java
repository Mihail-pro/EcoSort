package md.ecosort.ecosort.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// punctId / punct sunt null când deșeul nu a fost predat la un punct anume.
public record Inregistrare(int id, int utilizatorId, String username, Categorie categorie,
                           Integer punctId, String punct, BigDecimal cantitate, LocalDate data) {
}
