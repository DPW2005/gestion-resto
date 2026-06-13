# GestionResto - Application de Gestion de Restaurant

GestionResto est une application JavaFX conçue pour gérer un restaurant, en facilitant la prise de commande par les clients, la gestion des plats et des stocks par les gérants, et la gestion du service par le personnel. 

L'application communique avec une base de données PostgreSQL pour la persistance des données.

---

## 🛠 Prérequis et Dépendances

Pour faire tourner le projet, vous aurez besoin de :
- **Java JDK 21+** (Testé sur la version 21/22).
- **Maven** (le projet intègre un Wrapper Maven `mvnw.cmd` sous Windows pour vous faciliter la tâche).
- **PostgreSQL** installé et en cours d'exécution.

### Rôle des Dépendances principales (dans `pom.xml`)
- **`javafx-controls` / `javafx-fxml`** : Le framework graphique pour créer l'interface utilisateur.
- **`postgresql`** (JDBC Driver) : Permet à Java de se connecter et de dialoguer avec la base de données PostgreSQL.
- **`dotenv-java`** : Permet de lire le fichier `.env` pour charger les variables d'environnement (identifiants de base de données) de manière sécurisée sans les écrire en dur dans le code.

---

## ⚙️ Installation et Configuration

### 1. Base de données
1. Ouvrez votre outil d'administration PostgreSQL (pgAdmin ou psql).
2. Créez une nouvelle base de données (ex: `gestion_resto`).
3. Exécutez le script SQL fourni dans le projet : `src/main/resources/init.sql`. Ce script va :
   - Créer toutes les tables nécessaires (`menu_items`, `restaurant_tables`, `orders`, `order_items`, `payments`).
   - Insérer un jeu de données initial (tables et quelques plats).

### 2. Configuration de l'environnement (`.env`)
1. À la racine du projet, vous trouverez un fichier nommé `.env.example`.
2. Faites une copie de ce fichier et nommez-la `.env` (à la racine du projet).
3. Ouvrez le fichier `.env` et modifiez les variables pour qu'elles correspondent à votre base de données locale :
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/gestion_resto
   DB_USER=postgres
   DB_PASSWORD=votre_mot_de_passe
   ```

### 3. Lancement du projet
Ouvrez un terminal à la racine du projet et utilisez le wrapper Maven pour compiler et lancer l'application :
```bash
# Nettoie, compile et lance l'application JavaFX
.\mvnw.cmd clean javafx:run
```

---

## 📂 Rôle des Fichiers Clés

### Architecture MVC
Le projet suit une architecture Modèle-Vue-Contrôleur (MVC) simplifiée avec un pattern DAO.

- **`/models`** : Contient les classes représentant les entités de la base de données (`MenuItem`, `Order`, `RestaurantTable`, etc.).
- **`/dao` (Data Access Object)** : Contient la logique d'accès à la base de données. C'est ici qu'on trouve toutes les requêtes SQL (ex: `MenuItemDAO`, `OrderDAO`).
- **`/utils/DatabaseConnection.java`** : Gère la connexion unique à PostgreSQL en lisant le fichier `.env`.
- **`/controllers`** : Contient les contrôleurs JavaFX (`ClientController`, `StaffController`, `ManagerController`) qui gèrent la logique des interfaces (clics sur les boutons, remplissage des listes).
- **`src/main/resources/.../views`** : Contient les fichiers FXML qui définissent le design visuel des interfaces.
- **`HelloApplication.java`** : Le point d'entrée de l'application qui charge la vue principale (`MainView`).

---

## 📖 Guide d'Utilisation

L'application propose 3 espaces distincts accessibles depuis le menu principal :

### 1. Espace Client
- Permet aux clients de sélectionner leur table.
- Affiche la liste des menus (les plats en rupture de stock n'apparaissent pas).
- Le client ajoute les plats à sa commande et clique sur **"Passer la commande"**.
- Une fois la commande envoyée en cuisine, le bouton de paiement est grisé dans l'attente de la facture. Dès que le serveur la génère, le client est alerté et peut payer.

### 2. Espace Personnel (Service)
- Permet aux serveurs de voir la liste de toutes les commandes passées.
- Le tableau s'actualise tout seul.
- En sélectionnant une commande, le serveur peut voir le détail des plats à servir à droite de l'écran.
- Il peut changer le statut (`CUISSON`, `SERVI`).
- Le clic sur **"Générer Facture"** permet de valider le ticket, ce qui débloque immédiatement le paiement côté client.

### 3. Espace Gérant (Stocks)
- Interface de gestion (CRUD) pour les plats du restaurant.
- Le gérant voit un tableau complet avec l'état des stocks.
- Il peut ajouter un nouveau plat, modifier un prix ou renflouer le stock d'un plat existant. Les modifications impactent instantanément l'Espace Client.
