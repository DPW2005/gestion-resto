package org.example.gestionresto.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.example.gestionresto.dao.OrderDAO;
import org.example.gestionresto.dao.OrderItemDAO;
import org.example.gestionresto.models.Order;
import org.example.gestionresto.models.OrderItemDetail;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur de l'interface Personnel (StaffView.fxml).
 * Gère le suivi des commandes (cuisine -> salle) et la facturation.
 */
public class StaffController {

    // Composants FXML
    @FXML private TableView<Order> ordersTable;
    @FXML private ListView<OrderItemDetail> orderDetailsListView;

    // DAOs
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();
    
    // Timeline pour rafraîchir le tableau automatiquement
    private Timeline pollingTimeline;

    /**
     * Initialisation appelée au chargement de la vue.
     */
    @FXML
    public void initialize() {
        loadOrders();
        startPolling(); // Démarre le rafraichissement auto
    }

    /**
     * Lance une tâche en arrière plan qui recharge la liste des commandes toutes les 3 secondes.
     * C'est essentiel pour voir apparaitre les nouvelles commandes des clients instantanément
     * sans demander aux serveurs de cliquer sur "Actualiser".
     */
    private void startPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            // Sauvegarde de l'élément actuellement sélectionné pour ne pas perdre le focus
            Order selected = ordersTable.getSelectionModel().getSelectedItem();
            int selectedId = selected != null ? selected.getId() : -1;

            loadOrders();

            // Restauration de la sélection après le rechargement
            if (selectedId != -1) {
                for (Order o : ordersTable.getItems()) {
                    if (o.getId() == selectedId) {
                        ordersTable.getSelectionModel().select(o);
                        break;
                    }
                }
            }
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    /**
     * Gère le clic de la souris sur une ligne du tableau.
     * Récupère l'ID de la commande cliquée pour afficher les détails (plats) dans la liste de droite.
     */
    @FXML
    protected void handleTableClick(MouseEvent event) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Demande au DAO tous les articles associés à cette commande
            List<OrderItemDetail> details = orderItemDAO.getOrderDetails(selected.getId());
            orderDetailsListView.setItems(FXCollections.observableArrayList(details));
        }
    }

    /**
     * Bouton "Actualiser" manuel (au cas où le polling serait désactivé).
     */
    @FXML
    protected void handleRefresh(ActionEvent event) {
        loadOrders();
    }

    /**
     * Charge toutes les commandes de la base vers le TableView.
     */
    private void loadOrders() {
        List<Order> orders = orderDAO.getAllOrders();
        ordersTable.setItems(FXCollections.observableArrayList(orders));
    }

    /**
     * Passe la commande sélectionnée en "CUISSON".
     */
    @FXML
    protected void handleStatusCooking(ActionEvent event) {
        updateSelectedOrderStatus("COOKING");
    }

    /**
     * Passe la commande sélectionnée en "SERVIE".
     */
    @FXML
    protected void handleStatusServed(ActionEvent event) {
        updateSelectedOrderStatus("SERVED");
    }

    /**
     * Méthode générique pour mettre à jour le statut dans la DB.
     * @param newStatus Le statut à insérer.
     */
    private void updateSelectedOrderStatus(String newStatus) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                orderDAO.updateOrderStatus(selected.getId(), newStatus);
                loadOrders(); // Met à jour l'affichage
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de mettre à jour le statut.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une commande.");
        }
    }

    /**
     * Bouton "Générer Facture".
     * Le fait de cliquer dessus passe le statut de la commande à "BILLED".
     * C'est ce mot-clé (BILLED) qui, côté client, débloquera le bouton "Payer".
     */
    @FXML
    protected void handleGenerateInvoice(ActionEvent event) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Change le statut pour notifier le client qu'il peut payer
                orderDAO.updateOrderStatus(selected.getId(), "BILLED");
                loadOrders();
                
                // Création visuelle du ticket de caisse
                String invoiceContent = "FACTURE\n" +
                                        "-------\n" +
                                        "Commande #" + selected.getId() + "\n" +
                                        "Table: " + selected.getTableId() + "\n" +
                                        "Total: " + selected.getTotalAmount() + " €\n" +
                                        "Statut: BILLED (En attente de paiement)\n" +
                                        "Date: " + selected.getCreatedAt() + "\n" +
                                        "-------\n" +
                                        "Détails :\n";
                
                // Boucle sur chaque plat pour l'ajouter au texte du ticket
                List<OrderItemDetail> details = orderItemDAO.getOrderDetails(selected.getId());
                for (OrderItemDetail d : details) {
                    invoiceContent += "- " + d.toString() + "\n";
                }
                invoiceContent += "\nMerci de votre visite!";
                                        
                // Affichage du ticket au personnel
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Facture générée");
                alert.setHeaderText("Facture envoyée au client - Commande " + selected.getId());
                alert.setContentText(invoiceContent);
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la génération de la facture.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une commande pour générer la facture.");
        }
    }

    /**
     * Utilitaire d'alertes visuelles.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
