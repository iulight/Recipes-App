package com.retete.service;

import com.retete.model.ElementListaCumparaturi;
import com.retete.model.PurchaseEvent;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String dbPath;

    public PersistenceService() {
        File dbDir = new File(System.getProperty("user.home"), ".recipebook");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        dbPath = "jdbc:sqlite:" + new File(dbDir, "recipebook.db").getAbsolutePath();
        initDb();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbPath);
    }

    private void initDb() {
        String createShoppingItems = """
                CREATE TABLE IF NOT EXISTS shopping_items (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    cantitate REAL NOT NULL DEFAULT 1,
                    unitate TEXT NOT NULL DEFAULT 'buc',
                    din_reteta TEXT NOT NULL DEFAULT 'manual',
                    categorie TEXT DEFAULT '',
                    pret REAL DEFAULT 0,
                    cumparat INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL
                )
                """;
        String createPurchaseHistory = """
                CREATE TABLE IF NOT EXISTS purchase_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    shopping_item_id TEXT,
                    item_name TEXT NOT NULL,
                    cantitate REAL NOT NULL DEFAULT 1,
                    unitate TEXT NOT NULL DEFAULT 'buc',
                    categorie TEXT DEFAULT '',
                    pret_total REAL DEFAULT 0,
                    bought_at TEXT NOT NULL
                )
                """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createShoppingItems);
            stmt.execute(createPurchaseHistory);
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    // ---- Shopping Items ----

    public List<ElementListaCumparaturi> loadShoppingItems() {
        List<ElementListaCumparaturi> list = new ArrayList<>();
        String sql = "SELECT id, name, cantitate, unitate, din_reteta, categorie, pret, cumparat FROM shopping_items ORDER BY created_at";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ElementListaCumparaturi el = new ElementListaCumparaturi(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("cantitate"),
                        rs.getString("unitate"),
                        rs.getString("din_reteta"),
                        rs.getString("categorie"),
                        rs.getDouble("pret"),
                        rs.getInt("cumparat") == 1
                );
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("Load shopping items error: " + e.getMessage());
        }
        return list;
    }

    public void saveShoppingItem(ElementListaCumparaturi item) {
        String sql = "INSERT OR REPLACE INTO shopping_items (id, name, cantitate, unitate, din_reteta, categorie, pret, cumparat, created_at) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getNume());
            ps.setDouble(3, item.getCantitate());
            ps.setString(4, item.getUnitate());
            ps.setString(5, item.getDinReteta());
            ps.setString(6, item.getCategorie());
            ps.setDouble(7, item.getPret());
            ps.setInt(8, item.isCumparat() ? 1 : 0);
            ps.setString(9, LocalDateTime.now().format(FORMATTER));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Save shopping item error: " + e.getMessage());
        }
    }

    public void updateShoppingItemBought(String itemId, boolean cumparat) {
        String sql = "UPDATE shopping_items SET cumparat=? WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cumparat ? 1 : 0);
            ps.setString(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Update bought error: " + e.getMessage());
        }
    }

    public void deleteShoppingItem(String itemId) {
        String sql = "DELETE FROM shopping_items WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Delete shopping item error: " + e.getMessage());
        }
    }

    public void deleteAllShoppingItems() {
        String sql = "DELETE FROM shopping_items";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Delete all items error: " + e.getMessage());
        }
    }

    public void deleteBoughtShoppingItems() {
        String sql = "DELETE FROM shopping_items WHERE cumparat=1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Delete bought items error: " + e.getMessage());
        }
    }

    // ---- Purchase History ----

    public void savePurchaseEvent(PurchaseEvent event) {
        String sql = "INSERT INTO purchase_history (shopping_item_id, item_name, cantitate, unitate, categorie, pret_total, bought_at) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getShoppingItemId());
            ps.setString(2, event.getItemName());
            ps.setDouble(3, event.getCantitate());
            ps.setString(4, event.getUnitate());
            ps.setString(5, event.getCategorie());
            ps.setDouble(6, event.getPretTotal());
            ps.setString(7, event.getBoughtAt().format(FORMATTER));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Save purchase event error: " + e.getMessage());
        }
    }

    public List<PurchaseEvent> loadPurchaseHistory(LocalDateTime from, LocalDateTime to) {
        List<PurchaseEvent> list = new ArrayList<>();
        String sql = "SELECT id, shopping_item_id, item_name, cantitate, unitate, categorie, pret_total, bought_at FROM purchase_history WHERE bought_at BETWEEN ? AND ? ORDER BY bought_at DESC";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from.format(FORMATTER));
            ps.setString(2, to.format(FORMATTER));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseEvent ev = new PurchaseEvent();
                ev.setId(rs.getLong("id"));
                ev.setShoppingItemId(rs.getString("shopping_item_id"));
                ev.setItemName(rs.getString("item_name"));
                ev.setCantitate(rs.getDouble("cantitate"));
                ev.setUnitate(rs.getString("unitate"));
                ev.setCategorie(rs.getString("categorie"));
                ev.setPretTotal(rs.getDouble("pret_total"));
                String dtStr = rs.getString("bought_at");
                ev.setBoughtAt(LocalDateTime.parse(dtStr, FORMATTER));
                list.add(ev);
            }
        } catch (SQLException e) {
            System.err.println("Load purchase history error: " + e.getMessage());
        }
        return list;
    }

    public List<PurchaseEvent> loadAllPurchaseHistory() {
        List<PurchaseEvent> list = new ArrayList<>();
        String sql = "SELECT id, shopping_item_id, item_name, cantitate, unitate, categorie, pret_total, bought_at FROM purchase_history ORDER BY bought_at DESC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PurchaseEvent ev = new PurchaseEvent();
                ev.setId(rs.getLong("id"));
                ev.setShoppingItemId(rs.getString("shopping_item_id"));
                ev.setItemName(rs.getString("item_name"));
                ev.setCantitate(rs.getDouble("cantitate"));
                ev.setUnitate(rs.getString("unitate"));
                ev.setCategorie(rs.getString("categorie"));
                ev.setPretTotal(rs.getDouble("pret_total"));
                String dtStr = rs.getString("bought_at");
                ev.setBoughtAt(LocalDateTime.parse(dtStr, FORMATTER));
                list.add(ev);
            }
        } catch (SQLException e) {
            System.err.println("Load all purchase history error: " + e.getMessage());
        }
        return list;
    }
}
