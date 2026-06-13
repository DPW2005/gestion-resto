package org.example.gestionresto.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.gestionresto.dao.MenuItemDAO;
import org.example.gestionresto.dao.OrderDAO;
import org.example.gestionresto.dao.PaymentDAO;
import org.example.gestionresto.dao.RestaurantTableDAO;
import org.example.gestionresto.models.MenuItem;
import org.example.gestionresto.models.Order;
import org.example.gestionresto.models.OrderItem;
import org.example.gestionresto.models.RestaurantTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur de l'interface Client (ClientView.fxml).
 * Permet au client de consulter le menu, passer sa commande, et attendre/payer sa facture.
 */
public class ClientController {

    // Composants injectés depuis FXML
    @FXML private ComboBox<RestaurantTable> tableComboBox;
    @FXML private ListView<MenuItem> menuListView;
    @FXML private ListView<String> cartListView;
    @FXML private Label totalLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button payButton;

    // Objets d'accès aux données (DAO)
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private RestaurantTableDAO tableDAO = new RestaurantTableDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    // Variables d'état interne
    private ObservableList<MenuItem> cartItems = FXCollections.observableArrayList();
    private double currentTotal = 0.0;
    private int currentOrderId = -1; // -1 signifie aucune commande en cours
    
    // Timeline utilisée pour interroger la base de données régulièrement (polling)
    private Timeline pollingTimeline;

    /**
     * Méthode appelée automatiquement par JavaFX lors du chargement de la vue.
     */
    @FXML
    public void initialize() {
        loadTables();
        loadMenuItems();
    }

    /**
     * Charge les tables dans le ComboBox.
     */
    private void loadTables() {
        List<RestaurantTable> tables = tableDAO.getAllTables();
        tableComboBox.setItems(FXCollections.observableArrayList(tables));
    }

    /**
     * Charge les plats disponibles (>0 stock) dans la ListView.
     */
    private void loadMenuItems() {
        List<MenuItem> items = menuItemDAO.getAvailableMenuItems();
        menuListView.setItems(FXCollections.observableArrayList(items));
    }

    /**
     * Ajoute un plat sélectionné à la liste de commande courante (Panier).
     */
    @FXML
    protected void handleAddToCart(ActionEvent event) {
        MenuItem selectedItem = menuListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.add(selectedItem);
            updateCartView(); // Met à jour l'affichage
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un article du menu.");
        }
    }

    /**
     * Rafraîchit l'affichage du panier (ListView de droite) et calcule le total.
     */
    private void updateCartView() {
        cartListView.getItems().clear();
        currentTotal = 0.0;
        for (MenuItem item : cartItems) {
            cartListView.getItems().add(item.getName() + " - " + item.getPrice() + "€");
            currentTotal += item.getPrice();
        }
        totalLabel.setText(String.format("%.2f €", currentTotal));
    }

    /**
     * Gère l'envoi de la commande finale vers la cuisine/personnel.
     */
    @FXML
    protected void handlePlaceOrder(ActionEvent event) {
        // Vérification anti-erreur
        if (cartItems.isEmpty()) {
            showAlert("Erreur", "Votre commande est vide.");
            return;
        }
        
        RestaurantTable table = tableComboBox.getValue();
        if (table == null) {
            showAlert("Erreur", "Veuillez choisir votre table.");
            return;
        }

        try {
            // Création des OrderItems avec le stock qui diminue
            List<OrderItem> orderItems = new ArrayList<>();
            for (MenuItem mi : cartItems) {
                orderItems.add(new OrderItem(0, 0, mi.getId(), 1, mi.getPrice()));
                menuItemDAO.updateStock(mi.getId(), 1); // Décrémente le stock de 1 en base
            }

            // Création de la commande complète en transaction
            currentOrderId = orderDAO.createOrder(table.getId(), orderItems, currentTotal);
            
            // On marque la table comme occupée
            tableDAO.updateTableStatus(table.getId(), "OCCUPIED");
            
            showAlert("Succès", "Votre commande a été envoyée en cuisine ! Veuillez patienter...");
            
            // L'utilisateur ne peut plus commander ou payer tant que la facture n'est pas générée
            placeOrderButton.setDisable(true);
            payButton.setDisable(true);
            payButton.setText("En attente de la facture...");
            
            // On lance la boucle de vérification (Polling) pour attendre le statut "BILLED"
            startPollingForInvoice();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création de la commande.");
        }
    }
    
    /**
     * Boucle d'arrière-plan JavaFX (Timeline) qui lit la base toutes les 3 secondes.
     * L'objectif est d'activer le bouton de paiement dès que le personnel a cliqué sur "Générer Facture".
     */
    private void startPollingForInvoice() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            if (currentOrderId != -1) {
                // On vérifie le statut de notre commande dans la base de données
                List<Order> allOrders = orderDAO.getAllOrders();
                for (Order o : allOrders) {
                    if (o.getId() == currentOrderId) {
                        // Si le serveur a facturé la commande
                        if ("BILLED".equals(o.getStatus())) {
                            pollingTimeline.stop(); // On arrête de faire des requêtes pour rien
                            payButton.setDisable(false); // Le bouton "Payer" devient cliquable
                            payButton.setText("Payer la facture");
                            showAlert("Facture prête", "Le serveur a généré votre facture. Vous pouvez procéder au paiement.");
                        }
                        break;
                    }
                }
            }
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE); // Tourne en boucle infinie
        pollingTimeline.play();
    }

    /**
     * Gère l'action de paiement simulé.
     */
    @FXML
    protected void handlePayment(ActionEvent event) {
        if (currentOrderId != -1) {
            try {
                // 1. Crée la trace dans la table des paiements
                paymentDAO.createPayment(currentOrderId, currentTotal);
                // 2. Marque la commande comme payée
                orderDAO.updateOrderStatus(currentOrderId, "PAID");
                
                // 3. Libère la table pour un prochain client
                RestaurantTable table = tableComboBox.getValue();
                tableDAO.updateTableStatus(table.getId(), "AVAILABLE");
                
                showAlert("Paiement réussi", "Merci pour votre paiement ! La table a été libérée.");
                
                // Réinitialisation complète de l'interface pour le prochain client
                cartItems.clear();
                updateCartView();
                currentOrderId = -1;
                placeOrderButton.setDisable(false);
                payButton.setDisable(true);
                payButton.setText("Payer la facture");
                tableComboBox.getSelectionModel().clearSelection();
                loadMenuItems(); // Met à jour les stocks affichés
                loadTables(); // Met à jour les tables disponibles
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du paiement.");
            }
        }
    }

    /**
     * Utilitaire pour afficher une boite de dialogue modale d'information.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
