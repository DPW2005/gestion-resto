package org.example.gestionresto.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.example.gestionresto.dao.MenuItemDAO;
import org.example.gestionresto.models.MenuItem;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur de l'interface Gérant (ManagerView.fxml).
 * C'est l'interface d'administration qui permet de créer, modifier, 
 * ou supprimer les plats du menu (Opérations CRUD).
 */
public class ManagerController {

    // Composants FXML (Tableau et champs de saisie)
    @FXML private TableView<MenuItem> menuTable;
    @FXML private TextField nameField;
    @FXML private TextField descField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;

    // DAO
    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    /**
     * Initialise le tableau avec toutes les données de la table menu_items.
     */
    @FXML
    public void initialize() {
        loadMenu();
    }

    /**
     * Récupère tous les items (même ceux en stock 0) et les injecte dans le TableView.
     */
    private void loadMenu() {
        List<MenuItem> items = menuItemDAO.getAllMenuItems();
        menuTable.setItems(FXCollections.observableArrayList(items));
    }

    /**
     * Lorsqu'on clique sur une ligne du tableau, on pré-remplit les champs de saisie
     * en bas de la fenêtre avec les valeurs de la ligne cliquée.
     * Utile pour la modification rapide.
     */
    @FXML
    protected void handleTableClick(MouseEvent event) {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            descField.setText(selected.getDescription());
            priceField.setText(String.valueOf(selected.getPrice()));
            stockField.setText(String.valueOf(selected.getAvailableStock()));
        }
    }

    /**
     * Action pour le bouton "Ajouter".
     * Crée un nouveau plat dans la base de données.
     */
    @FXML
    protected void handleAdd(ActionEvent event) {
        try {
            // Lecture et conversion (Parsing) des valeurs entrées par l'utilisateur
            String name = nameField.getText();
            String desc = descField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            // Construction de l'objet et insertion DB
            MenuItem newItem = new MenuItem(0, name, desc, price, stock);
            menuItemDAO.createMenuItem(newItem);
            
            // Rechargement visuel et vidage des champs
            loadMenu();
            clearFields();
            showAlert("Succès", "Article ajouté au menu.");
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs numériques valides pour le prix et le stock.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter l'article.");
        }
    }

    /**
     * Action pour le bouton "Modifier".
     * Met à jour le plat actuellement sélectionné avec les valeurs des champs texte.
     */
    @FXML
    protected void handleUpdate(ActionEvent event) {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Modification de l'objet avec les nouvelles entrées
                selected.setName(nameField.getText());
                selected.setDescription(descField.getText());
                selected.setPrice(Double.parseDouble(priceField.getText()));
                selected.setAvailableStock(Integer.parseInt(stockField.getText()));

                // Envoi à la base de données
                menuItemDAO.updateMenuItem(selected);
                
                // Actualisation
                loadMenu();
                clearFields();
                showAlert("Succès", "Article modifié.");
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Valeurs numériques invalides.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de modifier l'article.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un article à modifier.");
        }
    }

    /**
     * Action pour le bouton "Supprimer".
     * Efface définitivement l'article sélectionné (sauf si verrouillé par une contrainte de clé étrangère).
     */
    @FXML
    protected void handleDelete(ActionEvent event) {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                menuItemDAO.deleteMenuItem(selected.getId());
                loadMenu();
                clearFields();
                showAlert("Succès", "Article supprimé.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer. Il se peut qu'une commande soit déjà liée à cet article.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un article à supprimer.");
        }
    }

    /**
     * Vide simplement le contenu de tous les TextFields.
     */
    private void clearFields() {
        nameField.clear();
        descField.clear();
        priceField.clear();
        stockField.clear();
        menuTable.getSelectionModel().clearSelection();
    }

    /**
     * Boîte de dialogue simple.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
