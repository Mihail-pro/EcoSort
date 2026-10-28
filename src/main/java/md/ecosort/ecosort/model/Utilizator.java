package md.ecosort.ecosort.model;

public record Utilizator(int id, String username, String numeComplet, Rol rol, boolean activ) {

    public boolean esteAdmin() { return rol == Rol.ADMIN; }

    @Override
    public String toString() { return username; }
}
