package org.example.gestionresto.models;

import java.sql.Timestamp;

/**
 * Modèle représentant une commande passée par un client.
 */
public class Order {
    private int id; // Identifiant unique de la commande
    private int tableId; // Identifiant de la table ayant passé la commande
    private String status; // Statut actuel ("PLACED", "COOKING", "SERVED", "BILLED", "PAID")
    private double totalAmount; // Montant total de la commande
    private Timestamp createdAt; // Date et heure de création

    /**
     * Constructeur par défaut.
     */
    public Order() {}

    /**
     * Constructeur avec paramètres.
     */
    public Order(int id, int tableId, String status, double totalAmount, Timestamp createdAt) {
        this.id = id;
        this.tableId = tableId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    // --- Getters et Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
