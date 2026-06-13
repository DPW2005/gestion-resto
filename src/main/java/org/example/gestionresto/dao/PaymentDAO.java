package org.example.gestionresto.dao;

import org.example.gestionresto.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) pour gérer l'historique des paiements dans la table `payments`.
 */
public class PaymentDAO {

    /**
     * Enregistre le paiement d'une facture par le client.
     * La date de paiement (payment_time) est générée automatiquement par PostgreSQL via "DEFAULT CURRENT_TIMESTAMP".
     * 
     * @param orderId L'ID de la commande payée.
     * @param amount Le montant total payé.
     * @throws SQLException Si l'insertion échoue.
     */
    public void createPayment(int orderId, double amount) throws SQLException {
        String query = "INSERT INTO payments (order_id, amount) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        }
    }
}
