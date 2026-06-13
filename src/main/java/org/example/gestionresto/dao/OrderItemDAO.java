package org.example.gestionresto.dao;

import org.example.gestionresto.models.OrderItemDetail;
import org.example.gestionresto.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) pour récupérer les lignes d'une commande (order_items).
 */
public class OrderItemDAO {

    /**
     * Récupère les détails d'une commande spécifique, en effectuant une jointure
     * avec la table menu_items pour obtenir le nom des plats (plutôt que juste leur ID).
     * 
     * @param orderId L'ID de la commande.
     * @return Une liste contenant les détails de la commande.
     */
    public List<OrderItemDetail> getOrderDetails(int orderId) {
        List<OrderItemDetail> details = new ArrayList<>();
        // Jointure SQL entre order_items et menu_items pour remplacer l'ID du plat par son vrai nom
        String query = "SELECT m.name, oi.quantity, oi.price_at_time " +
                       "FROM order_items oi " +
                       "JOIN menu_items m ON oi.menu_item_id = m.id " +
                       "WHERE oi.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, orderId); // Paramétrage de la requête pour éviter les injections SQL
            try (ResultSet rs = pstmt.executeQuery()) {
                // Création des objets DTO (Data Transfer Object) à partir du résultat
                while (rs.next()) {
                    OrderItemDetail detail = new OrderItemDetail(
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price_at_time")
                    );
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}
