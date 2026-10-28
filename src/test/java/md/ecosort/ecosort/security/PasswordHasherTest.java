package md.ecosort.ecosort.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashulEsteArgon2idSiSeVerifica() {
        String hash = PasswordHasher.hash("parola-Corecta1".toCharArray());

        assertTrue(hash.startsWith("$argon2id$v=19$m=19456,t=2,p=1$"), hash);
        assertTrue(PasswordHasher.verifica("parola-Corecta1".toCharArray(), hash));
        assertFalse(PasswordHasher.verifica("parola-corecta1".toCharArray(), hash));
        assertFalse(PasswordHasher.verifica(new char[0], hash));
    }

    @Test
    void aceeasiParolaDaHashuriDiferite() {
        // Sare aleatorie: același input nu produce același hash.
        assertNotEquals(PasswordHasher.hash("abcdefgh".toCharArray()), PasswordHasher.hash("abcdefgh".toCharArray()));
    }

    @Test
    void diacriticeleSuntSuportate() {
        String hash = PasswordHasher.hash("Ștefan-ăîâț".toCharArray());
        assertTrue(PasswordHasher.verifica("Ștefan-ăîâț".toCharArray(), hash));
        assertFalse(PasswordHasher.verifica("Stefan-aiat".toCharArray(), hash));
    }

    @Test
    void hashurileInvalideSuntRespinseFaraExceptie() {
        for (String invalid : new String[]{null, "", "admin123", "$argon2i$v=19$m=19456,t=2,p=1$YWJj$YWJj",
                "$argon2id$v=19$m=999999999,t=2,p=1$c2FyZXNhcmVzYXJl$aGFzaGhhc2hoYXNoaGFzaA",
                "$argon2id$v=19$m=19456,t=2,p=1$!!!$???", "$argon2id$v=19$x=1$a$b"}) {
            assertFalse(PasswordHasher.verifica("orice".toCharArray(), invalid), String.valueOf(invalid));
        }
    }
}
