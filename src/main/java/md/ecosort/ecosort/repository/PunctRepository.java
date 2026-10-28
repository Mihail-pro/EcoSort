package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.PunctColectare;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PunctRepository {

    public List<PunctColectare> lista() {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_punct_lista()}");
             ResultSet rs = s.executeQuery()) {
            List<PunctColectare> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(new PunctColectare(rs.getInt("id"), rs.getString("denumire"), rs.getString("bloc"),
                        rs.getInt("etaj"), rs.getString("incapere"),
                        CategorieRepository.categorie(rs, "categorie_id", "categorie", "culoare")));
            }
            return lista;
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public int adauga(String denumire, String bloc, int etaj, String incapere, int categorieId) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_punct_adauga(?, ?, ?, ?, ?, ?)}")) {
            s.setString(1, denumire);
            s.setString(2, bloc);
            s.setInt(3, etaj);
            s.setString(4, incapere);
            s.setInt(5, categorieId);
            s.registerOutParameter(6, Types.INTEGER);
            s.execute();
            return s.getInt(6);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void modifica(int id, String denumire, int categorieId) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_punct_modifica(?, ?, ?)}")) {
            s.setInt(1, id);
            s.setString(2, denumire);
            s.setInt(3, categorieId);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void sterge(int id) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_punct_sterge(?)}")) {
            s.setInt(1, id);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }
}
