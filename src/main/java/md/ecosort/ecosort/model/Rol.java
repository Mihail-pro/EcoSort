package md.ecosort.ecosort.model;

// Valorile corespund ENUM-ului `utilizatori.rol` din baza de date.
public enum Rol {
    ADMIN("Administrator"),
    UTILIZATOR("Utilizator");

    private final String eticheta;

    Rol(String eticheta) { this.eticheta = eticheta; }

    public String getEticheta() { return eticheta; }

    @Override
    public String toString() { return eticheta; }
}
