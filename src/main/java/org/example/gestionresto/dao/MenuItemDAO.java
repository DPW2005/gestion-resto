package org.example.gestionresto.dao;

import org.example.gestionresto.models.MenuItem;
import org.example.gestionresto.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) pour gérer les opérations CRUD sur la table `menu_items`.
 */
public class MenuItemDAO {

    /**
     * Récupère tous les articles du menu (y compris ceux en rupture de stock).
     * Utilisé principalement par l'Espace Gérant.
     * @return Une liste contenant tous les objets MenuItem trouvés en base.
     */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT * FROM menu_items ORDER BY id";

        // Try-with-resources assure la fermeture automatique des connexions
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Parcours du résultat SQL
            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("available_stock")
                );
                menuItems.add(item); // Ajout à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    /**
     * Récupère uniquement les articles ayant du stock disponible (> 0).
     * Utilisé par l'Espace Client pour ne pas afficher les plats indisponibles.
     * @return Une liste filtrée d'articles.
     */
    public List<MenuItem> getAvailableMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT * FROM menu_items WHERE available_stock > 0 ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("available_stock")
                );
                menuItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    /**
     * Diminue le stock d'un article spécifique après une commande.
     * @param menuItemId L'ID de l'article à décrémenter.
     * @param quantityToReduce La quantité à soustraire au stock.
     * @throws SQLException Si l'opération échoue.
     */
    public void updateStock(int menuItemId, int quantityToReduce) throws SQLException {
        String query = "UPDATE menu_items SET available_stock = available_stock - ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantityToReduce);
            pstmt.setInt(2, menuItemId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Crée un nouvel article dans le menu (Utilisé par le gérant).
     * @param item L'objet MenuItem contenant les nouvelles données.
     * @throws SQLException En cas d'erreur d'insertion.
     */
    public void createMenuItem(MenuItem item) throws SQLException {
        String query = "INSERT INTO menu_items (name, description, price, available_stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getAvailableStock());
            pstmt.executeUpdate();
        }
    }

    /**
     * Met à jour les informations (nom, prix, stock) d'un article existant.
     * @param item L'objet MenuItem mis à jour.
     * @throws SQLException En cas d'erreur de mise à jour.
     */
    public void updateMenuItem(MenuItem item) throws SQLException {
        String query = "UPDATE menu_items SET name = ?, description = ?, price = ?, available_stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getAvailableStock());
            pstmt.setInt(5, item.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Supprime un article de la base de données.
     * Attention : échouera si l'article est déjà lié à une commande via la clé étrangère order_items.
     * @param menuItemId L'ID de l'article à supprimer.
     * @throws SQLException S'il y a un conflit de clé étrangère ou autre erreur.
     */
    public void deleteMenuItem(int menuItemId) throws SQLException {
        String query = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, menuItemId);
            pstmt.executeUpdate();
        }
    }
}
