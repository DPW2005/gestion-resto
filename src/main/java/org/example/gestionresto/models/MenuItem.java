package org.example.gestionresto.models;

/**
 * Modèle représentant un article du menu (plat, boisson, etc.).
 */
public class MenuItem {
    private int id; // Identifiant unique généré par la base de données
    private String name; // Nom de l'article
    private String description; // Description de l'article
    private double price; // Prix de l'article en euros
    private int availableStock; // Quantité disponible en stock

    /**
     * Constructeur par défaut.
     */
    public MenuItem() {}

    /**
     * Constructeur avec paramètres pour initialiser tous les attributs.
     */
    public MenuItem(int id, String name, String description, double price, int availableStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableStock = availableStock;
    }

    // --- Getters et Setters pour chaque attribut ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    /**
     * Redéfinition de toString pour l'affichage propre dans les listes (JavaFX ListView).
     * @return Nom et prix de l'article.
     */
    @Override
    public String toString() {
        return name + " - " + price + "€";
    }
}
