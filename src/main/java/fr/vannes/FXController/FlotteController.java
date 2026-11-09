package fr.vannes.FXController;

<<<<<<< Updated upstream:src/main/java/fr/vannes/FXController/FlotteController.java
import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.Modele.Vehicule;
import fr.vannes.Service.VehiculeService;
=======
import Exceptions.RegleGestionException;
import Modele.Flotte;
import Modele.Vehicule;
import Data.DataFactory;
import Service.VehiculeService;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
>>>>>>> Stashed changes:src/main/java/FXController/FlotteController.java
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    private TableView<Vehicule> list;
    @FXML
    private TableColumn<Vehicule, String> immatColumn;
    @FXML
    private TableColumn<Vehicule, String> marqueColumn;
    @FXML
    private TableColumn<Vehicule, String> modeleColumn;
    @FXML
    private TableColumn<Vehicule, LocalDate> miseEnCirculationColumn;
    @FXML
    private TableColumn<Vehicule, Integer> kmColumn;
    @FXML
    private TableColumn<Vehicule, String> etatColumn;

    private VehiculeService vehiculeService;

    private final ObservableList<Vehicule> vehicules = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        vehiculeService = DataFactory.getInstance().getVehiculeService();



        immatColumn.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modeleColumn.setCellValueFactory(new PropertyValueFactory<>("modele"));
        miseEnCirculationColumn.setCellValueFactory(new PropertyValueFactory<>("date_mise_circulation"));
        kmColumn.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));
        etatColumn.setCellValueFactory(c -> new SimpleStringProperty(vehiculeService.estEnDeplacement(c.getValue().getId()) ? "En déplacement" : "Disponible"));
        list.setItems(vehicules);

        list.setItems(FXCollections.observableArrayList(vehiculeService.getFlotte()));

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            immat.setText(newV == null ? "" : newV.getImmatriculation());
            marque.setText(newV == null ? "" : newV.getMarque());
            modele.setText(newV == null ? "" : newV.getModele());
            dateMiseCirculation.setValue(newV == null ? null : newV.getDate_mise_circulation());
            km.setText(newV == null ? "" : String.valueOf(newV.getKilometrage()));
        });

    }

    @FXML
    public void onAjouterVehicule() {

        String immatSaisie = immat.getText().trim().toUpperCase();
        if (immatSaisie.isEmpty()) {
            message.setText("Veuillez saisir une immatriculation");
            return;
        }
        String marqueSaisie = marque.getText().trim();
        String modeleSaisie = modele.getText().trim();
        if (marqueSaisie.isEmpty() || modeleSaisie.isEmpty()) {
            message.setText("Veuillez saisir une marque et un modèle");
            return;
        }
        LocalDate dateSaisie = dateMiseCirculation.getValue();
        if (dateSaisie == null) {
            message.setText("Choisissez une date de mise en circulation");
            return;
        }
        int kmSaisi;
        try {
            kmSaisi = Integer.parseInt(km.getText().trim());
        } catch (NumberFormatException e) {
            message.setText("Le kilométrage doit être un nombre");
            return;
        }
        try {
            Vehicule vehicule = new Vehicule(0, immatSaisie, marqueSaisie, modeleSaisie, dateSaisie, kmSaisi);
            vehiculeService.ajouterVehicule(vehicule);

            message.setStyle("-fx-text-fill: green;");
            message.setText("Véhicule " + immatSaisie + " ajouté !");

        } catch (IllegalArgumentException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Saisie invalide : " + e.getMessage());

        } catch (RegleGestionException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Refusé : " + e.getMessage());
        }
    }

    @FXML
    public void onSupprimerVehicule() {
        Vehicule selection = list.getSelectionModel().getSelectedItem();
        if (selection == null) {
            message.setText("Choisissez un véhicule à supprimer");
            return;
        }

        try {
            vehiculeService.retirerVehicule(selection.getImmatriculation());

            message.setStyle("-fx-text-fill: green;");
            message.setText("Véhicule " + selection.getImmatriculation() + " supprimé !");
        } catch (RegleGestionException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Saisie invalide : " + e.getMessage());

        }
    }
}


