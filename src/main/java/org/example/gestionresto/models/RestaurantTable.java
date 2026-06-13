package org.example.gestionresto.models;

/**
 * Modèle représentant une table physique dans le restaurant.
 */
public class RestaurantTable {
    private int id; // Identifiant unique (base de données)
    private int tableNumber; // Numéro affiché de la table (ex: Table 1)
    private String status; // Statut actuel de la table: "AVAILABLE" (Libre) ou "OCCUPIED" (Occupée)

    /**
     * Constructeur par défaut.
     */
    public RestaurantTable() {}

    /**
     * Constructeur avec paramètres.
     */
    public RestaurantTable(int id, int tableNumber, String status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.status = status;
    }

    // --- Getters et Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Redéfinition de toString pour l'affichage propre dans les ComboBox (liste déroulante).
     * @return Exemple : "Table 1 (AVAILABLE)"
     */
    @Override
    public String toString() {
        return "Table " + tableNumber + " (" + status + ")";
    }
}
