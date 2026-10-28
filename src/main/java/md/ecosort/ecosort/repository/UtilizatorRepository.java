package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.Rol;
import md.ecosort.ecosort.model.Utilizator;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorRepository {

    public record ContCuParola(Utilizator utilizator, String parolaHash) { }

    public record Profil(Utilizator utilizator, BigDecimal totalKg, int numarInregistrari) { }

    public Optional<ContCuParola> cautaPentruAutentificare(String username) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_autentificare(?)}")) {
            s.setString(1, username);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(new ContCuParola(utilizator(rs), rs.getString("parola_hash")))
                                 : Optional.empty();
            }
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public Optional<Profil> profil(int id) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_profil(?)}")) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(new Profil(utilizator(rs), rs.getBigDecimal("total_kg"),
                                                          rs.getInt("numar_inregistrari")))
                                 : Optional.empty();
            }
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public List<Utilizator> lista() {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_lista()}");
             ResultSet rs = s.executeQuery()) {
            List<Utilizator> lista = new ArrayList<>();
            while (rs.next()) lista.add(utilizator(rs));
            return lista;
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public boolean existaAdmin() {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_exista_admin(?)}")) {
            s.registerOutParameter(1, Types.BOOLEAN);
            s.execute();
            return s.getBoolean(1);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public int creeazaPrimulAdmin(String username, String numeComplet, String parolaHash) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_creeaza_primul_admin(?, ?, ?, ?)}")) {
            s.setString(1, username);
            s.setString(2, numeComplet);
            s.setString(3, parolaHash);
            s.registerOutParameter(4, Types.INTEGER);
            s.execute();
            return s.getInt(4);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public int adauga(String username, String numeComplet, String parolaHash, Rol rol) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_adauga(?, ?, ?, ?, ?)}")) {
            s.setString(1, username);
            s.setString(2, numeComplet);
            s.setString(3, parolaHash);
            s.setString(4, rol.name());
            s.registerOutParameter(5, Types.INTEGER);
            s.execute();
            return s.getInt(5);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void modifica(int id, String numeComplet, Rol rol, boolean activ) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_modifica(?, ?, ?, ?)}")) {
            s.setInt(1, id);
            s.setString(2, numeComplet);
            s.setString(3, rol.name());
            s.setBoolean(4, activ);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void schimbaParola(int id, String parolaHash) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_utilizator_schimba_parola(?, ?)}")) {
            s.setInt(1, id);
            s.setString(2, parolaHash);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    private static Utilizator utilizator(ResultSet rs) throws SQLException {
        return new Utilizator(rs.getInt("id"), rs.getString("username"), rs.getString("nume_complet"),
                Rol.valueOf(rs.getString("rol")), rs.getBoolean("activ"));
    }
}
