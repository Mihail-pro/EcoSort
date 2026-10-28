package md.ecosort.ecosort.service;

import md.ecosort.ecosort.model.Utilizator;
import md.ecosort.ecosort.repository.UtilizatorRepository;
import md.ecosort.ecosort.repository.UtilizatorRepository.ContCuParola;
import md.ecosort.ecosort.security.PasswordHasher;
import md.ecosort.ecosort.session.Sesiune;

import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private static final String EROARE_LOGIN = "Nume de utilizator sau parolă incorectă!";

    // Hash valid al unei parole aleatorii: verificarea unui cont inexistent durează cât una reală,
    // ca timpul de răspuns să nu dezvăluie ce conturi există. Calculat la prima folosire.
    private static final class HashFals {
        static final String VALOARE = PasswordHasher.hash(UUID.randomUUID().toString().toCharArray());
    }

    private final UtilizatorRepository repo = new UtilizatorRepository();

    // La prima pornire nu există niciun administrator: aplicația cere crearea lui.
    public boolean necesitaConfigurare() {
        return !repo.existaAdmin();
    }

    public Utilizator autentifica(String username, char[] parola) {
        if (username == null || username.isBlank() || parola == null || parola.length == 0) {
            throw new IllegalArgumentException("Completează toate câmpurile!");
        }
        Optional<ContCuParola> cont = repo.cautaPentruAutentificare(username.strip());
        String hash = cont.map(ContCuParola::parolaHash).orElse(HashFals.VALOARE);
        if (!PasswordHasher.verifica(parola, hash) || cont.isEmpty()) {
            throw new IllegalArgumentException(EROARE_LOGIN);
        }
        Utilizator u = cont.get().utilizator();
        if (!u.activ()) {
            throw new IllegalArgumentException("Contul este dezactivat. Contactați administratorul.");
        }
        Sesiune.porneste(u);
        return u;
    }

    public Utilizator creeazaPrimulAdmin(String username, String numeComplet, char[] parola, char[] confirmare) {
        Validari.cont(username, numeComplet);
        Validari.parolaNoua(parola, confirmare);
        repo.creeazaPrimulAdmin(username.strip(), numeComplet.strip(), PasswordHasher.hash(parola));
        return autentifica(username, parola);
    }

    public void deconecteaza() {
        Sesiune.inchide();
    }
}
