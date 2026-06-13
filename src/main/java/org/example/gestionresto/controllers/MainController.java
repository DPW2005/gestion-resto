package org.example.gestionresto.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur principal gérant la fenêtre d'accueil (MainView).
 * Il s'occupe de la navigation vers les différentes interfaces de l'application.
 */
public class MainController {

    /**
     * Méthode appelée lors du clic sur le bouton "Espace Client".
     */
    @FXML
    protected void openClientView(ActionEvent event) {
        openWindow("/org/example/gestionresto/views/ClientView.fxml", "Espace Client");
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Espace Personnel (Commandes)".
     */
    @FXML
    protected void openStaffView(ActionEvent event) {
        openWindow("/org/example/gestionresto/views/StaffView.fxml", "Espace Personnel");
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Espace Gérant (Stocks)".
     */
    @FXML
    protected void openManagerView(ActionEvent event) {
        openWindow("/org/example/gestionresto/views/ManagerView.fxml", "Espace Gérant");
    }

    /**
     * Méthode utilitaire privée pour ouvrir une nouvelle fenêtre FXML indépendante.
     * 
     * @param fxmlPath Le chemin absolu du fichier FXML dans les ressources.
     * @param title Le titre de la nouvelle fenêtre.
     */
    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
