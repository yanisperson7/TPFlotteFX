package FXController;

import Service.VehiculeService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class FlotteController {
    VehiculeService vehiculeService;
    @FXML
    private TextField immat;
    @FXML
    private TextField marque;
    @FXML
    private TextField modele;
    @FXML
    private DatePicker dateMiseCirculation;
    @FXML
    private TextField km;

    @FXML
    public void onAjouterVehicule() {
        String immatSaisie = immat.getText().trim().toUpperCase();
        String marqueSaisie;
    }
}


