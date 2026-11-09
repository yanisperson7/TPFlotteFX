module org.example.tpflottefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires bcrypt;
    requires tools.jackson.databind;
    requires net.synedra.validatorfx;
    requires java.desktop;

    opens FXApplication to javafx.fxml, javafx.graphics;
    exports FXApplication;
    exports FXController;
    opens FXController to javafx.fxml, javafx.graphics;
}