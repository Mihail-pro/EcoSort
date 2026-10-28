package md.ecosort.ecosort.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @TempDir
    Path dir;

    @AfterEach
    void reseteaza() {
        Database.reseteaza();
    }

    @Test
    void citesteEnvSiVariabileleDeMediuAuPrioritate() throws IOException {
        Path env = dir.resolve(".env");
        Files.writeString(env, """
                # comentariu
                ECOSORT_DB_HOST=gazda-din-fisier
                ECOSORT_DB_PORT=3307\r
                ECOSORT_DB_NAME=ecosort_test
                ECOSORT_DB_USER=app
                ECOSORT_DB_PASSWORD=p=a#s
                """);

        Database.incarca(Map.of("ECOSORT_DB_HOST", "gazda-din-mediu", "PATH", "x"), env);

        assertEquals("jdbc:mysql://gazda-din-mediu:3307/ecosort_test?connectTimeout=5000&socketTimeout=30000",
                Database.url());
    }

    @Test
    void lipsaConfiguratieiDaUnMesajClar() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> Database.incarca(Map.of(), dir.resolve("nu-exista.env")));
        assertTrue(e.getMessage().contains("ECOSORT_DB_HOST"), e.getMessage());
    }
}
