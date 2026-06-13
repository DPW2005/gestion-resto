package org.example.gestionresto.models;

import java.sql.Timestamp;

/**
 * Modèle représentant un paiement effectué pour une commande.
 */
public class Payment {
    private int id; // Identifiant unique du paiement
    private int orderId; // Clé étrangère vers l'entité Order (la commande payée)
    private double amount; // Montant payé
    private Timestamp paymentTime; // Date et heure du paiement

    /**
     * Constructeur par défaut.
     */
    public Payment() {}

    /**
     * Constructeur avec paramètres.
     */
    public Payment(int id, int orderId, double amount, Timestamp paymentTime) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentTime = paymentTime;
    }

    // --- Getters et Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Timestamp getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Timestamp paymentTime) { this.paymentTime = paymentTime; }
}
