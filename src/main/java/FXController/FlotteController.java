package FXController;

import Exceptions.RegleGestionException;
import Modele.Vehicule;
import Service.VehiculeService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.time.LocalDate;

public class FlotteController {
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
    private Label message;

    private VehiculeService vehiculeService;

    public void setVehiculeService(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @FXML
    public void onAjouterVehicule() {

        String immatSaisie = immat.getText().trim().toUpperCase();
        String marqueSaisie = marque.getText().trim();
        String modeleSaisie = modele.getText().trim();
        LocalDate dateSaisie = dateMiseCirculation.getValue();
        if (dateSaisie == null) {
            message.setText("Choisissez une date de mise en circulation.");
            return;
        }
        int kmSaisi;
        try {
            kmSaisi = Integer.parseInt(km.getText().trim());
        } catch (NumberFormatException e) {
            message.setText("Le kilométrage doit être un nombre.");
            return;
        }
        try {
            Vehicule vehicule = new Vehicule(0, immatSaisie, marqueSaisie, modeleSaisie, dateSaisie, kmSaisi);
            vehiculeService.ajouterVehicule(vehicule);

            message.setStyle("-fx-text-fill: green;");
            message.setText("Véhicule " + immatSaisie + " ajouté. Enregistrement en base en cours…");

        } catch (IllegalArgumentException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Saisie invalide : " + e.getMessage());

        } catch (RegleGestionException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Refusé : " + e.getMessage());
        }


    }
}


