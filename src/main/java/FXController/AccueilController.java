package FXController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class AccueilController {
    @FXML
    public void accueilScene(Window w) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accueil.fxml"));
        fxmlLoader.setLocation(AuthController.class.getResource("accueil-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage) w;
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.show();


    }
}
