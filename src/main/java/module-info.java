module org.example.tpflottefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires bcrypt;
    requires tools.jackson.databind;
    requires net.synedra.validatorfx;

    opens FXController to javafx.fxml;

    opens FXApplication to javafx.fxml, javafx.graphics;
    exports FXApplication;
}