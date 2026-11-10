package fr.vannes.FXApplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReservationApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/reservation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1430, 700);
        stage.setTitle("Réservations (dev)");
        stage.setScene(scene);
        stage.show();
    }
}
