package fr.vannes.FXController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class AccueilController {

    @FXML
    private Button ButVehicule;

    @FXML
    public void accueilScene(Window w) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/accueil-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) w;
        stage.setTitle("Accueil");
        stage.setScene(scene);

        stage.sizeToScene();
        stage.centerOnScreen();

        stage.show();
    }

    @FXML
    public void onVehiculeClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/vehicule-view.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ButVehicule.getScene().getWindow();
        stage.setTitle("Véhicules");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    @FXML
    public void onReservationClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/reservation-view.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ButVehicule.getScene().getWindow();
        stage.setTitle("Véhicules");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    @FXML
    public void onExportClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/export-view.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ButVehicule.getScene().getWindow();
        stage.setTitle("Véhicules");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

}
