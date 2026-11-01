package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.StareCos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StareCosRepository {

    public List<StareCos> lista() {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_cos_stare_lista()}");
             ResultSet rs = s.executeQuery()) {
            List<StareCos> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(new StareCos(rs.getInt("punct_id"), rs.getString("dispozitiv"), rs.getInt("canal"),
                        rs.getDouble("capacitate_kg"),
                        rs.getObject("greutate_g", Integer.class), rs.getObject("distanta_mm", Integer.class),
                        rs.getObject("umplere_pct", Integer.class), rs.getObject("vechime_s", Long.class)));
            }
            return lista;
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void leaga(int punctId, String cod, int canal, BigDecimal capacitateKg, int goalaMm, int plinaMm) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_senzor_leaga(?, ?, ?, ?, ?, ?)}")) {
            s.setInt(1, punctId);
            s.setString(2, cod);
            s.setInt(3, canal);
            s.setBigDecimal(4, capacitateKg);
            s.setInt(5, goalaMm);
            s.setInt(6, plinaMm);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void dezleaga(int punctId) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_senzor_dezleaga(?)}")) {
            s.setInt(1, punctId);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }
}