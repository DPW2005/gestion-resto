module org.example.gestionresto {
    requires javafx.controls;
    requires javafx.fxml;    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens org.example.gestionresto to javafx.fxml;
    opens org.example.gestionresto.controllers to javafx.fxml;
    exports org.example.gestionresto;
    exports org.example.gestionresto.controllers;
    exports org.example.gestionresto.models;
    exports org.example.gestionresto.utils;
}