package md.ecosort.ecosort.service;

import md.ecosort.ecosort.model.Rol;
import md.ecosort.ecosort.model.Utilizator;
import md.ecosort.ecosort.repository.DataException;
import md.ecosort.ecosort.repository.UtilizatorRepository;
import md.ecosort.ecosort.repository.UtilizatorRepository.Profil;
import md.ecosort.ecosort.security.PasswordHasher;
import md.ecosort.ecosort.session.Sesiune;

import java.util.List;

public class UtilizatorService {

    private final UtilizatorRepository repo = new UtilizatorRepository();

    public Profil profilulMeu() {
        Utilizator eu = Sesiune.curent();
        return repo.profil(eu.id())
                .orElseThrow(() -> new DataException("Contul nu mai există în baza de date.", null));
    }

    public List<Utilizator> lista() {
        Sesiune.cereAdmin();
        return repo.lista();
    }

    public void adauga(String username, String numeComplet, char[] parola, char[] confirmare, Rol rol) {
        Sesiune.cereAdmin();
        Validari.cont(username, numeComplet);
        Validari.parolaNoua(parola, confirmare);
        if (rol == null) throw new IllegalArgumentException("Alegeți rolul.");
        repo.adauga(username.strip(), numeComplet.strip(), PasswordHasher.hash(parola), rol);
    }

    public void modifica(int id, String numeComplet, Rol rol, boolean activ) {
        Utilizator eu = Sesiune.cereAdmin();
        Validari.text(numeComplet, "Numele complet", 100);
        if (rol == null) throw new IllegalArgumentException("Alegeți rolul.");
        if (id == eu.id() && (rol != Rol.ADMIN || !activ)) {
            throw new IllegalArgumentException("Nu vă puteți retrage propriile drepturi de administrator.");
        }
        repo.modifica(id, numeComplet.strip(), rol, activ);
    }

    public void reseteazaParola(int id, char[] parola, char[] confirmare) {
        Sesiune.cereAdmin();
        Validari.parolaNoua(parola, confirmare);
        repo.schimbaParola(id, PasswordHasher.hash(parola));
    }
}
