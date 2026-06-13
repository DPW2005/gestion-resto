package org.example.gestionresto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application JavaFX.
 * Hérite de javafx.application.Application.
 */
public class HelloApplication extends Application {
    
    /**
     * Point d'entrée de l'interface graphique JavaFX.
     * @param stage La fenêtre principale de l'application.
     * @throws IOException Si le fichier FXML n'est pas trouvé.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Chargement de la vue principale depuis les ressources FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/gestionresto/views/MainView.fxml"));
        
        // Création de la scène avec les dimensions 400x300
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        
        // Configuration de la fenêtre principale
        stage.setTitle("Gestion Resto - Accueil");
        stage.setScene(scene);
        stage.show(); // Affichage de la fenêtre
    }

    /**
     * Méthode main standard en Java.
     * Elle lance l'application JavaFX.
     * @param args Arguments en ligne de commande.
     */
    public static void main(String[] args) {
        launch();
    }
}