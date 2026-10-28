package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.Categorie;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CategorieRepository {

    public List<Categorie> lista() {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_categorie_lista()}");
             ResultSet rs = s.executeQuery()) {
            List<Categorie> lista = new ArrayList<>();
            while (rs.next()) lista.add(categorie(rs, "id", "denumire", "culoare"));
            return lista;
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public int adauga(String denumire, String culoare) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_categorie_adauga(?, ?, ?)}")) {
            s.setString(1, denumire);
            s.setString(2, culoare);
            s.registerOutParameter(3, Types.INTEGER);
            s.execute();
            return s.getInt(3);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void modifica(int id, String denumire, String culoare) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_categorie_modifica(?, ?, ?)}")) {
            s.setInt(1, id);
            s.setString(2, denumire);
            s.setString(3, culoare);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void sterge(int id) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_categorie_sterge(?)}")) {
            s.setInt(1, id);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    static Categorie categorie(ResultSet rs, String id, String denumire, String culoare) throws SQLException {
        return new Categorie(rs.getInt(id), rs.getString(denumire), rs.getString(culoare));
    }
}
