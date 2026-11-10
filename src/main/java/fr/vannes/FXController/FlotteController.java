package fr.vannes.FXController;

import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.Modele.Vehicule;
import fr.vannes.Service.VehiculeService;
import Data.DataFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Thread.sleep;

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
    private TextField rechercheImmat;
    @FXML
    private ChoiceBox<String> choixTri;
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
    private void initialize() throws IOException {
        vehiculeService = DataFactory.getInstance().getVehiculeService();
        vehicules.setAll(vehiculeService.getFlotte());

        immatColumn.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modeleColumn.setCellValueFactory(new PropertyValueFactory<>("modele"));
        miseEnCirculationColumn.setCellValueFactory(new PropertyValueFactory<>("date_mise_circulation"));
        kmColumn.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));

        FilteredList<Vehicule> vehiculesFiltres = new FilteredList<>(vehicules, v -> true);
        list.setItems(vehiculesFiltres);

        rechercheImmat.textProperty().addListener((obs, ancien, nouveau) -> {
            String saisie = nouveau == null ? "" : nouveau.trim().toUpperCase();
            vehiculesFiltres.setPredicate(v ->
                    saisie.isEmpty() || v.getImmatriculation().toUpperCase().contains(saisie));
        });

        Platform.runLater(() -> {
            loadTableAsync();

        });

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            immat.setText(newV == null ? "" : newV.getImmatriculation());
            marque.setText(newV == null ? "" : newV.getMarque());
            modele.setText(newV == null ? "" : newV.getModele());
            dateMiseCirculation.setValue(newV == null ? null : newV.getDate_mise_circulation());
            km.setText(newV == null ? "" : String.valueOf(newV.getKilometrage()));
        });

    }

    @FXML
    private void loadTableAsync() {

        Task<List<Vehicule>> task = new Task<>() {
            @Override
            protected List<Vehicule> call() throws Exception {
                //je simule le temps de chargement d'une bdd distante
                List <Vehicule> listevehicules= new ArrayList<>();

                return vehiculeService.getFlotte();
                // ou return personneService.getALl();
            }
        };

        task.setOnSucceeded(e -> {

            List<Integer> listV = vehiculeService.estEnDeplacement();

            etatColumn.setCellValueFactory(c -> new SimpleStringProperty((listV.contains (c.getValue().getId())) ? "En déplacement" : "Disponible"));
            vehicules.setAll(task.getValue());

            list.setPlaceholder(new Label("Aucun média"));

        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            list.setPlaceholder(new Label("Erreur de chargement : "
                    + task.getException().getMessage()));
        });

        vehicules.clear();               //intialise la tableView en la vidant
        list.setPlaceholder(new ProgressIndicator());//et affiche le sablier

        Thread thread = new Thread(task, "load-media");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void onAjouterVehicule() {
        String immatSaisie = immat.getText().trim().toUpperCase();
        if (immatSaisie.isEmpty()) {
            message.setStyle("-fx-text-fill: black;");
            message.setText("Veuillez saisir une immatriculation");
            return;
        }
        String marqueSaisie = marque.getText().trim();
        String modeleSaisie = modele.getText().trim();
        if (marqueSaisie.isEmpty() || modeleSaisie.isEmpty()) {
            message.setStyle("-fx-text-fill: black;");
            message.setText("Veuillez saisir une marque et un modèle");
            return;
        }
        LocalDate dateSaisie = dateMiseCirculation.getValue();
        if (dateSaisie == null) {
            message.setStyle("-fx-text-fill: black;");
            message.setText("Choisissez une date de mise en circulation");
            return;
        }
        int kmSaisi;
        try {
            kmSaisi = Integer.parseInt(km.getText().trim());
        } catch (NumberFormatException e) {
            message.setStyle("-fx-text-fill: black;");
            message.setText("Le kilométrage doit être un nombre");
            return;
        }
        try {
            Vehicule vehicule = new Vehicule(0, immatSaisie, marqueSaisie, modeleSaisie, dateSaisie, kmSaisi);
            vehiculeService.ajouterVehicule(vehicule);
            vehicules.setAll(vehiculeService.getFlotte());
            list.getSelectionModel().select(vehicule);
            list.scrollTo(vehicule);
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
            message.setStyle("-fx-text-fill: black;");
            message.setText("Choisissez un véhicule à supprimer");
            return;
        }
        try {
            vehiculeService.retirerVehicule(selection.getImmatriculation());
            vehicules.setAll(vehiculeService.getFlotte());
            message.setStyle("-fx-text-fill: green;");
            message.setText("Véhicule " + selection.getImmatriculation() + " supprimé !");
        } catch (RegleGestionException e) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("Erreur : Véhicule en déplacement : " + e.getMessage());

        }
    }

    @FXML
    public void onTrier() {
        String choix = choixTri.getValue();
        System.out.println("Choix : " + choix);
        switch (choix) {
            case "Marque, puis modèle" -> FXCollections.sort(vehicules, Comparator.comparing(Vehicule::getMarque)
                    .thenComparing(Vehicule::getModele));
            case "Date de mise en circulation" -> FXCollections.sort(vehicules, Comparator.comparing(Vehicule::getDate_mise_circulation));
            case "Kilométrage décroissant" -> FXCollections.sort(vehicules, Comparator.comparing(Vehicule::getKilometrage));
        }
    }
}


