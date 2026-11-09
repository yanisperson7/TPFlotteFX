package fr.vannes.FXController;

import fr.vannes.FXApplication.AuthApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ExportController {

    public ExportController(Window w) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/export-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) w;
        stage.setTitle("Authentification");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();

    }
}
