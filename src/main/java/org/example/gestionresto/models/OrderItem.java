package org.example.gestionresto.models;

/**
 * Modèle représentant la ligne d'une commande (relation N:M entre Order et MenuItem).
 * Stocke l'article commandé, sa quantité, et son prix au moment de l'achat.
 */
public class OrderItem {
    private int id; // Identifiant unique de la ligne de commande
    private int orderId; // Clé étrangère vers l'entité Order
    private int menuItemId; // Clé étrangère vers l'entité MenuItem
    private int quantity; // Quantité commandée
    private double priceAtTime; // Prix unitaire figé au moment de l'achat

    /**
     * Constructeur par défaut.
     */
    public OrderItem() {}

    /**
     * Constructeur avec paramètres.
     */
    public OrderItem(int id, int orderId, int menuItemId, int quantity, double priceAtTime) {
        this.id = id;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    // --- Getters et Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(double priceAtTime) { this.priceAtTime = priceAtTime; }
}
