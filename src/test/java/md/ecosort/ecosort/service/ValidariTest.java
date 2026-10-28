package md.ecosort.ecosort.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidariTest {

    @Test
    void cantitateaSeRotunjesteSiRespectaLimiteleDecimal() {
        assertEquals(new BigDecimal("1.24"), Validari.cantitate(new BigDecimal("1.235"), false));
        assertThrows(IllegalArgumentException.class, () -> Validari.cantitate(BigDecimal.ZERO, false));
        assertThrows(IllegalArgumentException.class, () -> Validari.cantitate(new BigDecimal("0.001"), false));
        assertThrows(IllegalArgumentException.class, () -> Validari.cantitate(new BigDecimal("-1"), true));
        assertThrows(IllegalArgumentException.class, () -> Validari.cantitate(new BigDecimal("100000000"), false));
        assertEquals(new BigDecimal("0.00"), Validari.cantitate(BigDecimal.ZERO, true));
    }

    @Test
    void dataNuPoateFiInViitor() {
        assertEquals(LocalDate.now(), Validari.dataColectare(LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> Validari.dataColectare(LocalDate.now().plusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> Validari.dataColectare(null));
    }

    @Test
    void contulSiParola() {
        assertDoesNotThrow(() -> Validari.cont("ion.popescu", "Ion Popescu"));
        assertThrows(IllegalArgumentException.class, () -> Validari.cont("io", "Ion"));
        assertThrows(IllegalArgumentException.class, () -> Validari.cont("ion popescu", "Ion"));
        assertThrows(IllegalArgumentException.class, () -> Validari.cont("ion'--", "Ion"));
        assertThrows(IllegalArgumentException.class, () -> Validari.cont("ion", "   "));

        assertDoesNotThrow(() -> Validari.parolaNoua("12345678".toCharArray(), "12345678".toCharArray()));
        assertThrows(IllegalArgumentException.class, () -> Validari.parolaNoua("1234567".toCharArray(), "1234567".toCharArray()));
        assertThrows(IllegalArgumentException.class, () -> Validari.parolaNoua("12345678".toCharArray(), "12345679".toCharArray()));
    }

    @Test
    void culoareaHex() {
        assertEquals("#3B82F6", Validari.culoare("#3b82f6"));
        assertThrows(IllegalArgumentException.class, () -> Validari.culoare("albastru"));
        assertThrows(IllegalArgumentException.class, () -> Validari.culoare("#3b82f"));
    }
}
