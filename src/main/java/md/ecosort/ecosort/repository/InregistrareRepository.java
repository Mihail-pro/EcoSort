package md.ecosort.ecosort.repository;

import md.ecosort.ecosort.config.Database;
import md.ecosort.ecosort.model.Categorie;
import md.ecosort.ecosort.model.Inregistrare;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InregistrareRepository {

    // Toți parametrii sunt opționali (null = fără filtru); intervalul de date este inclusiv.
    public List<Inregistrare> lista(Integer utilizatorId, Integer categorieId, LocalDate deLa, LocalDate panaLa) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_inregistrare_lista(?, ?, ?, ?)}")) {
            seteaza(s, 1, utilizatorId, Types.INTEGER);
            seteaza(s, 2, categorieId, Types.INTEGER);
            seteaza(s, 3, deLa, Types.DATE);
            seteaza(s, 4, panaLa, Types.DATE);
            try (ResultSet rs = s.executeQuery()) {
                List<Inregistrare> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new Inregistrare(rs.getInt("id"), rs.getInt("utilizator_id"), rs.getString("username"),
                            CategorieRepository.categorie(rs, "categorie_id", "categorie", "culoare"),
                            rs.getObject("punct_id", Integer.class), rs.getString("punct"),
                            rs.getBigDecimal("cantitate"), rs.getObject("data_colectare", LocalDate.class)));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public int adauga(int utilizatorId, int categorieId, Integer punctId, BigDecimal cantitate, LocalDate data) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_inregistrare_adauga(?, ?, ?, ?, ?, ?)}")) {
            s.setInt(1, utilizatorId);
            s.setInt(2, categorieId);
            seteaza(s, 3, punctId, Types.INTEGER);
            s.setBigDecimal(4, cantitate);
            s.setObject(5, data, Types.DATE);
            s.registerOutParameter(6, Types.INTEGER);
            s.execute();
            return s.getInt(6);
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    // proprietarId = null permite modificarea oricărei înregistrări (administrator).
    public void modifica(int id, Integer proprietarId, int categorieId, Integer punctId,
                         BigDecimal cantitate, LocalDate data) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_inregistrare_modifica(?, ?, ?, ?, ?, ?)}")) {
            s.setInt(1, id);
            seteaza(s, 2, proprietarId, Types.INTEGER);
            s.setInt(3, categorieId);
            seteaza(s, 4, punctId, Types.INTEGER);
            s.setBigDecimal(5, cantitate);
            s.setObject(6, data, Types.DATE);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    public void sterge(int id, Integer proprietarId) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_inregistrare_sterge(?, ?)}")) {
            s.setInt(1, id);
            seteaza(s, 2, proprietarId, Types.INTEGER);
            s.execute();
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    // Total pe fiecare categorie (inclusiv cele cu 0 kg), în ordinea categoriilor.
    public Map<Categorie, BigDecimal> totaluriPeCategorii(LocalDate deLa, LocalDate panaLa, Integer utilizatorId) {
        try (Connection c = Database.conexiune();
             CallableStatement s = c.prepareCall("{call sp_raport_totaluri_categorii(?, ?, ?)}")) {
            seteaza(s, 1, deLa, Types.DATE);
            seteaza(s, 2, panaLa, Types.DATE);
            seteaza(s, 3, utilizatorId, Types.INTEGER);
            try (ResultSet rs = s.executeQuery()) {
                Map<Categorie, BigDecimal> totaluri = new LinkedHashMap<>();
                while (rs.next()) {
                    totaluri.put(CategorieRepository.categorie(rs, "categorie_id", "denumire", "culoare"),
                            rs.getBigDecimal("total_kg"));
                }
                return totaluri;
            }
        } catch (SQLException e) {
            throw DataException.din(e);
        }
    }

    static void seteaza(CallableStatement s, int index, Object valoare, int tipSql) throws SQLException {
        if (valoare == null) s.setNull(index, tipSql);
        else s.setObject(index, valoare, tipSql);
    }
}
