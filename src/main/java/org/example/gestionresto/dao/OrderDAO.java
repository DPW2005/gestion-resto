package org.example.gestionresto.dao;

import org.example.gestionresto.models.Order;
import org.example.gestionresto.models.OrderItem;
import org.example.gestionresto.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) pour la table `orders`.
 * Gère la création des commandes et de leurs lignes associées via transaction.
 */
public class OrderDAO {

    /**
     * Crée une nouvelle commande et insère simultanément toutes les lignes de commandes (OrderItems).
     * Utilise une transaction (commit/rollback) pour assurer que la commande ne s'enregistre pas 
     * à moitié en cas de crash lors de l'insertion d'un plat.
     * 
     * @param tableId L'ID de la table qui commande.
     * @param items La liste des articles commandés.
     * @param totalAmount Le montant total calculé.
     * @return L'ID généré de la nouvelle commande, ou -1 en cas d'erreur non bloquante.
     * @throws SQLException Si la transaction échoue.
     */
    public int createOrder(int tableId, List<OrderItem> items, double totalAmount) throws SQLException {
        String insertOrderQuery = "INSERT INTO orders (table_id, total_amount) VALUES (?, ?) RETURNING id";
        String insertItemQuery = "INSERT INTO order_items (order_id, menu_item_id, quantity, price_at_time) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Début de la Transaction

            int orderId = -1;
            // Étape 1 : Insérer la commande principale et récupérer son ID
            try (PreparedStatement pstmtOrder = conn.prepareStatement(insertOrderQuery)) {
                pstmtOrder.setInt(1, tableId);
                pstmtOrder.setDouble(2, totalAmount);
                ResultSet rs = pstmtOrder.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt("id"); // Récupération de la clé primaire générée
                }
            }

            // Étape 2 : Insérer chaque article commandé (liés à l'orderId)
            if (orderId != -1) {
                try (PreparedStatement pstmtItem = conn.prepareStatement(insertItemQuery)) {
                    for (OrderItem item : items) {
                        pstmtItem.setInt(1, orderId);
                        pstmtItem.setInt(2, item.getMenuItemId());
                        pstmtItem.setInt(3, item.getQuantity());
                        pstmtItem.setDouble(4, item.getPriceAtTime());
                        pstmtItem.addBatch(); // Préparation en lot pour optimiser les performances
                    }
                    pstmtItem.executeBatch(); // Exécution du lot SQL
                }
            }

            conn.commit(); // Validation de la transaction (tout est sauvegardé)
            return orderId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // En cas d'erreur, on annule toutes les opérations
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restauration du comportement par défaut
                conn.close(); // Fermeture de la connexion manuelle
            }
        }
    }

    /**
     * Récupère la liste de toutes les commandes, triées des plus récentes aux plus anciennes.
     * @return Liste des objets Order.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("table_id"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("created_at")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Met à jour uniquement le statut d'une commande (ex: PLACED -> COOKING -> BILLED -> PAID).
     * @param orderId L'ID de la commande.
     * @param newStatus Le nouveau statut (chaine de caractères).
     * @throws SQLException Si la mise à jour échoue.
     */
    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }
}
