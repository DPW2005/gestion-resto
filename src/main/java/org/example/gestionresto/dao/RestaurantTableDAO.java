package org.example.gestionresto.dao;

import org.example.gestionresto.models.RestaurantTable;
import org.example.gestionresto.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) gérant la table `restaurant_tables`.
 */
public class RestaurantTableDAO {

    /**
     * Récupère la liste complète des tables du restaurant, triées par numéro.
     * Utilisé pour remplir la liste déroulante (ComboBox) côté Client.
     * 
     * @return Liste d'objets RestaurantTable.
     */
    public List<RestaurantTable> getAllTables() {
        List<RestaurantTable> tables = new ArrayList<>();
        String query = "SELECT * FROM restaurant_tables ORDER BY table_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RestaurantTable table = new RestaurantTable(
                        rs.getInt("id"),
                        rs.getInt("table_number"),
                        rs.getString("status")
                );
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Met à jour le statut de la table (ex: pour la passer de AVAILABLE à OCCUPIED).
     * 
     * @param tableId L'ID de la table à modifier.
     * @param newStatus Le nouveau statut (chaîne de caractères).
     * @throws SQLException Si la mise à jour échoue.
     */
    public void updateTableStatus(int tableId, String newStatus) throws SQLException {
        String query = "UPDATE restaurant_tables SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, tableId);
            pstmt.executeUpdate();
        }
    }
}
