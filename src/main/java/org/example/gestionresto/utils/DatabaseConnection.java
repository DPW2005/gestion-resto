package org.example.gestionresto.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire gérant la connexion à la base de données PostgreSQL.
 * Utilise le pattern Singleton implicite pour les informations de configuration.
 */
public class DatabaseConnection {
    // Chargement des variables d'environnement depuis le fichier .env
    private static Dotenv dotenv = Dotenv.load();
    
    // Attributs constants récupérés depuis le fichier .env
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    /**
     * Méthode pour obtenir une nouvelle connexion à la base de données.
     * @return Connection Objet de connexion JDBC.
     * @throws SQLException Si la connexion échoue (mauvais identifiants ou DB injoignable).
     */
    public static Connection getConnection() throws SQLException {
        // Utilise le DriverManager pour établir la connexion avec les informations stockées
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
