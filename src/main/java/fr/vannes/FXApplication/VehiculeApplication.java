package fr.vannes.FXApplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VehiculeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VehiculeApplication.class.getResource("/FXML/vehicule-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Page véhicule");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

}
