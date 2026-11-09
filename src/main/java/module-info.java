module fr.vannes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires bcrypt;
    requires tools.jackson.databind;
    requires net.synedra.validatorfx;
    requires java.desktop;

    opens fr.vannes.FXApplication to javafx.fxml, javafx.graphics, javafx.base;
    exports fr.vannes.FXApplication;
    exports fr.vannes.FXController;
    opens fr.vannes.Modele to javafx.base;
    opens fr.vannes.FXController to javafx.fxml, javafx.graphics, javafx.base;
}