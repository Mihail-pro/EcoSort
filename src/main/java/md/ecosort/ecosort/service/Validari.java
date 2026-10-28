package md.ecosort.ecosort.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Pattern;

// Validarea datelor înainte de salvare. Limitele urmează coloanele și constrângerile CHECK din baza de date.
final class Validari {

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{3,50}");
    private static final Pattern CULOARE = Pattern.compile("#[0-9A-Fa-f]{6}");
    private static final BigDecimal CANTITATE_MAX = new BigDecimal("99999999.99"); // DECIMAL(10,2)
    private static final LocalDate DATA_MIN = LocalDate.of(2000, 1, 1);

    private Validari() { }

    static void cont(String username, String numeComplet) {
        if (username == null || !USERNAME.matcher(username.strip()).matches()) {
            throw new IllegalArgumentException(
                    "Numele de utilizator trebuie să aibă 3–50 caractere: litere, cifre, punct, minus sau _.");
        }
        text(numeComplet, "Numele complet", 100);
    }

    static void parolaNoua(char[] parola, char[] confirmare) {
        if (parola == null || parola.length < 8 || parola.length > 128) {
            throw new IllegalArgumentException("Parola trebuie să aibă între 8 și 128 de caractere.");
        }
        if (!Arrays.equals(parola, confirmare)) {
            throw new IllegalArgumentException("Parolele nu coincid.");
        }
    }

    static String text(String valoare, String camp, int lungimeMax) {
        if (valoare == null || valoare.isBlank()) {
            throw new IllegalArgumentException(camp + " este obligatoriu.");
        }
        String v = valoare.strip();
        if (v.length() > lungimeMax) {
            throw new IllegalArgumentException(camp + " poate avea cel mult " + lungimeMax + " caractere.");
        }
        return v;
    }

    static String culoare(String culoare) {
        if (culoare == null || !CULOARE.matcher(culoare).matches()) {
            throw new IllegalArgumentException("Culoarea trebuie să fie în formatul #RRGGBB.");
        }
        return culoare.toUpperCase();
    }

    // Rotunjește la 2 zecimale, ca în coloana DECIMAL(10,2).
    static BigDecimal cantitate(BigDecimal cantitate, boolean permiteZero) {
        if (cantitate == null) throw new IllegalArgumentException("Cantitatea este obligatorie.");
        BigDecimal c = cantitate.setScale(2, RoundingMode.HALF_UP);
        if (c.signum() < 0 || (!permiteZero && c.signum() == 0)) {
            throw new IllegalArgumentException("Cantitatea trebuie să fie mai mare decât 0.");
        }
        if (c.compareTo(CANTITATE_MAX) > 0) {
            throw new IllegalArgumentException("Cantitatea este prea mare.");
        }
        return c;
    }

    static LocalDate dataColectare(LocalDate data) {
        if (data == null) throw new IllegalArgumentException("Data este obligatorie.");
        if (data.isAfter(LocalDate.now())) throw new IllegalArgumentException("Data nu poate fi în viitor.");
        if (data.isBefore(DATA_MIN)) throw new IllegalArgumentException("Data este prea veche.");
        return data;
    }
}
