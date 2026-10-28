package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.ProgresLunar;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ObiectivRepository {

    public void seteaza(int categorieId, YearMonth luna, BigDecimal tinta) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_obiectiv_seteaza(?, ?, ?, ?)}")) {
            s.setInt(1, categorieId);
            s.setInt(2, luna.getYear());
            s.setInt(3, luna.getMonthValue());
            s.setBigDecimal(4, tinta);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void sterge(int categorieId, YearMonth luna) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_obiectiv_sterge(?, ?, ?)}")) {
            s.setInt(1, categorieId);
            s.setInt(2, luna.getYear());
            s.setInt(3, luna.getMonthValue());
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public List<ProgresLunar> progres(YearMonth luna) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_raport_progres_lunar(?, ?)}")) {
            s.setInt(1, luna.getYear());
            s.setInt(2, luna.getMonthValue());
            try (ResultSet rs = s.executeQuery()) {
                List<ProgresLunar> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new ProgresLunar(CategorieRepository.categorie(rs, "categorie_id", "denumire", "culoare"),
                            rs.getBigDecimal("tinta_kg"), rs.getBigDecimal("colectat_kg")));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }
}
