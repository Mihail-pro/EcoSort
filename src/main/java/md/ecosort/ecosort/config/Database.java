package md.ecosort.ecosort.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// Conexiunea la MySQL. Configurația vine din variabile de mediu ECOSORT_DB_*,
// iar ce lipsește se citește din fișierul .env din directorul de lucru.
public final class Database {

    private static final String PREFIX = "ECOSORT_DB_";

    private static String url;
    private static Properties credentiale;

    private Database() { }

    public static Connection conexiune() throws SQLException {
        if (url == null) {
            incarca(System.getenv(), Path.of(".env"));
        }
        return DriverManager.getConnection(url, credentiale);
    }

    // Pachet-vizibil pentru teste.
    static synchronized void incarca(Map<String, String> mediu, Path fisierEnv) {
        Map<String, String> cfg = new HashMap<>(citesteEnv(fisierEnv));
        mediu.forEach((k, v) -> { if (k.startsWith(PREFIX) && !v.isBlank()) cfg.put(k, v); });

        String host = obligatoriu(cfg, "HOST");
        String port = cfg.getOrDefault(PREFIX + "PORT", "3306");
        String nume = obligatoriu(cfg, "NAME");
        String parametri = cfg.getOrDefault(PREFIX + "PARAMS", "connectTimeout=5000&socketTimeout=30000");

        Properties p = new Properties();
        p.setProperty("user", obligatoriu(cfg, "USER"));
        p.setProperty("password", cfg.getOrDefault(PREFIX + "PASSWORD", ""));

        url = "jdbc:mysql://" + host + ":" + port + "/" + nume + "?" + parametri;
        credentiale = p;
    }

    static String url() { return url; }

    // Pentru teste: următoarea conexiune recitește configurația.
    static synchronized void reseteaza() {
        url = null;
        credentiale = null;
    }

    private static String obligatoriu(Map<String, String> cfg, String cheie) {
        String v = cfg.get(PREFIX + cheie);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Lipsește configurația " + PREFIX + cheie
                    + ". Copiați .env.example în .env și completați valorile.");
        }
        return v;
    }

    private static Map<String, String> citesteEnv(Path fisier) {
        Map<String, String> cfg = new HashMap<>();
        if (!Files.isRegularFile(fisier)) return cfg;
        try {
            for (String linie : Files.readAllLines(fisier)) {
                linie = linie.strip();
                int egal = linie.indexOf('=');
                if (linie.isEmpty() || linie.startsWith("#") || egal < 1) continue;
                cfg.put(linie.substring(0, egal).strip(), linie.substring(egal + 1).strip());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Nu se poate citi " + fisier.toAbsolutePath(), e);
        }
        return cfg;
    }
}
