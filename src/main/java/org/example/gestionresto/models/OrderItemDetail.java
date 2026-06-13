package org.example.gestionresto.models;

/**
 * Modèle DTO (Data Transfer Object) utilisé pour l'affichage détaillé d'une ligne de commande.
 * Il regroupe les informations de la table `order_items` et le `name` de la table `menu_items`.
 */
public class OrderItemDetail {
    private String itemName; // Le nom du plat (provenant de menu_items)
    private int quantity; // La quantité commandée
    private double priceAtTime; // Le prix unitaire lors de l'achat

    /**
     * Constructeur pour initialiser les détails.
     */
    public OrderItemDetail(String itemName, int quantity, double priceAtTime) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    // --- Getters ---

    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPriceAtTime() { return priceAtTime; }

    /**
     * Redéfinition de toString pour un affichage lisible dans la ListView du personnel.
     * Exemple : "2x Burger Maison (12.5€)"
     */
    @Override
    public String toString() {
        return quantity + "x " + itemName + " (" + priceAtTime + "€)";
    }
}
