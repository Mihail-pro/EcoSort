package md.ecosort.ecosort.repository;

import java.sql.SQLException;

// Eroare de acces la date, cu un mesaj care poate fi afișat utilizatorului.
// Detaliile tehnice rămân în cauză (getCause) și în jurnal.
public class DataException extends RuntimeException {

    private static final System.Logger LOG = System.getLogger(DataException.class.getName());

    public DataException(String mesaj, Throwable cauza) {
        super(mesaj, cauza);
    }

    public static DataException din(SQLException e) {
        String stare = e.getSQLState() == null ? "" : e.getSQLState();
        // Erorile de business din procedurile stocate (SIGNAL SQLSTATE '45000').
        if (stare.equals("45000")) {
            return new DataException(e.getMessage(), e);
        }
        LOG.log(System.Logger.Level.WARNING, "Eroare SQL " + stare + "/" + e.getErrorCode(), e);
        String mesaj;
        if (stare.startsWith("08")) {
            mesaj = "Nu se poate conecta la baza de date. Verificați că serverul MySQL rulează "
                    + "și că datele din .env sunt corecte.";
        } else if (stare.equals("28000")) {
            mesaj = "Acces refuzat la baza de date: verificați utilizatorul și parola din .env.";
        } else if (stare.equals("23000")) {
            mesaj = "Operația încalcă o regulă a bazei de date (valoare duplicată sau referință invalidă).";
        } else if (e.getErrorCode() == 1370 || e.getErrorCode() == 1305) {
            mesaj = "Baza de date nu este instalată complet (lipsesc proceduri sau drepturi). Vezi database/README.md.";
        } else {
            mesaj = "Eroare la baza de date (cod " + e.getErrorCode() + ").";
        }
        return new DataException(mesaj, e);
    }
}
