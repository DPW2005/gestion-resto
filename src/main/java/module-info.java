module org.example.gestionresto {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gestionresto to javafx.fxml;
    exports org.example.gestionresto;
}