package md.ecosort.ecosort.session;

import md.ecosort.ecosort.model.Utilizator;

// Utilizatorul autentificat în această instanță a aplicației (una singură, desktop).
public final class Sesiune {

    private static volatile Utilizator curent;

    private Sesiune() { }

    public static void porneste(Utilizator utilizator) { curent = utilizator; }

    public static void inchide() { curent = null; }

    public static Utilizator curent() {
        Utilizator u = curent;
        if (u == null) throw new SecurityException("Sesiunea a expirat. Autentificați-vă din nou.");
        return u;
    }

    public static Utilizator cereAdmin() {
        Utilizator u = curent();
        if (!u.esteAdmin()) throw new SecurityException("Doar administratorul poate efectua această operație.");
        return u;
    }
}
