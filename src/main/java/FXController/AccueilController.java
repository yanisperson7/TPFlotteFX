package FXController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class AccueilController {

    @FXML
    private Button ButVehicules;

    @FXML
    public void accueilScene(Window w) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/accueil-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage) w;
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onVehiculeClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXApplication/vehicule-view.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);

        Stage stage = (Stage) ButVehicules.getScene().getWindow();
        stage.setTitle("Véhicules");
        stage.setScene(scene);
    }
}
