package fr.vannes.FXController;

import Data.DataFactory;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.Modele.*;
import fr.vannes.Service.ReservationService;
import fr.vannes.Service.VehiculeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ReservationController {
    // Tableaux
    // Liste Véhicules
    @FXML private TableColumn<Vehicule, String> Immatriculation;
    @FXML private TableColumn<Vehicule, String> Marque;
    @FXML private TableColumn<Vehicule, String> Modele;
    @FXML private TableColumn<Vehicule, LocalDate> MiseCirculation;
    @FXML private TableColumn<Vehicule, Integer> Km;
    @FXML private TableColumn<Vehicule, String> Etat;
    @FXML private TableView<Vehicule> TableauVehicule;

    // Liste salariés
    @FXML private TableColumn<Salarie, String> Matricule;
    @FXML private TableColumn<Salarie, String> Nom;
    @FXML private TableColumn<Salarie, String> Prenom;
    @FXML private TableView<Salarie> TableauSalarie;

    // Historique des salariés
    @FXML private TableColumn<Reservation, String> DateDepart;
    @FXML private TableColumn<Reservation, String> DateRetour;
    @FXML private TableColumn<Reservation, String> ImmatriculationHistorique;
    @FXML private TableColumn<Reservation, String> Vehicule;
    @FXML private TableView<Reservation> TableauHistorique;

    //Formulaires
    // Créer un salarié
    @FXML private TextField TextMatriculeSalarie;
    @FXML private TextField TextNom;
    @FXML private TextField TextPrenom;
    @FXML private Label MessageSalarie;

    // Enregistrer un départ
    @FXML private TextField TextMatriculeDepart;
    @FXML private TextField TextImmatriculationDepart;
    @FXML private Label MessageDepart;

    private ReservationService reservationService;
    private VehiculeService vehiculeService;

    private final ObservableList<Vehicule> vehicules = FXCollections.observableArrayList();
    private final ObservableList<Salarie> salaries = FXCollections.observableArrayList();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();


    @FXML
    private void initialize() throws IOException {

        reservationService = DataFactory.getInstance().getReservationService();
        vehiculeService = DataFactory.getInstance().getVehiculeService();
        List<Integer> listeVehiculeNonDispo = vehiculeService.estEnDeplacement();
        // Tableau de véhicules
        Immatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        Marque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        Modele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        MiseCirculation.setCellValueFactory(new PropertyValueFactory<>("date_mise_circulation"));
        Km.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));


        //TableauVehicule.setItems(vehicules);
        //TableauVehicule.setItems(FXCollections.observableArrayList(vehiculeService.getFlotte()));
        loadTableAsync();
        Etat.setCellValueFactory(c -> new SimpleStringProperty((listeVehiculeNonDispo.contains (c.getValue().getId())) ? "En déplacement" : "Disponible"));

        // Tableau des salaries
        Matricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        Nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        Prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableauSalarie.setItems(FXCollections.observableArrayList(reservationService.listerSalaries()));

        // Historique des salariés
        DateDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        DateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        ImmatriculationHistorique.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        Vehicule.setCellValueFactory(new PropertyValueFactory<>("vehiculeComplet"));
    }

    private void loadTableAsync() {

        Task<List<Vehicule>> task = new Task<>() {
            @Override
            protected List<Vehicule> call() throws Exception {
                //je simule le temps de chargement d'une bdd distante
                return vehiculeService.getFlotte();
            }
        };

        task.setOnSucceeded(e -> {
            TableauVehicule.getItems().setAll(task.getValue());
            //le placeholder est une propriété de TableView et ListView
            //il s'affiche automatiquement à la place de lignes quand la liste
            // d'élements est vide
            TableauVehicule.setPlaceholder(new Label("Aucun média"));
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            TableauVehicule.setPlaceholder(new Label("Erreur de chargement : "
                    + task.getException().getMessage()));
        });

        TableauVehicule.getItems().clear();               //intialise la tableView en la vidant
        TableauVehicule.setPlaceholder(new ProgressIndicator());//et affiche le sablier

        Thread thread = new Thread(task, "load-media");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onSalarieClick() {
        Salarie salarie = TableauSalarie.getSelectionModel().getSelectedItem();
        if (salarie != null) {
            String matricule = salarie.getMatricule();
            HistoriqueReservations<Salarie, Timestamp, Reservation> historique = reservationService.historiqueSalarie(matricule);

            TableauHistorique.getItems().setAll(historique.getReservations().values());
        }
    }

    @FXML
    private void onValiderSalarieClick() throws IOException {
        String matricule = TextMatriculeSalarie.getText().trim();
        String nom = TextNom.getText().trim();
        String prenom = TextPrenom.getText().trim();

        if (matricule.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            MessageSalarie.setStyle("-fx-text-fill: red;");
            MessageSalarie.setText("Veuillez remplir tous les champs");
            return;
        }

        if (matricule.length() != 6) {
            MessageSalarie.setStyle("-fx-text-fill: red;");
            MessageSalarie.setText("Le matricule doit contenir 6 chiffres");
            return;
        }

        try {
            Integer.parseInt(matricule);
            Salarie salarie = new Salarie(matricule, nom, prenom);
            reservationService.creerSalarie(salarie);

            TableauSalarie.getItems().add(salarie);

            TextMatriculeSalarie.clear();
            TextNom.clear();
            TextPrenom.clear();

            MessageSalarie.setStyle("-fx-text-fill: green;");
            MessageSalarie.setText("Le salarié " + nom + " " + prenom + " a été ajouté");

        } catch (NumberFormatException e) {
            MessageSalarie.setStyle("-fx-text-fill: red;");
            MessageSalarie.setText("Le matricule ne doit contenir que des chiffres");

        } catch (IllegalArgumentException e) {
            MessageSalarie.setStyle("-fx-text-fill: red;");
            MessageSalarie.setText("Saisie invalide : " + e.getMessage());

        } catch (RegleGestionException e) {
            MessageSalarie.setStyle("-fx-text-fill: red;");
            MessageSalarie.setText("Refusé : " + e.getMessage());
        }
    }

    @FXML
    private void onValiderDepartClick() {
        String matricule = TextMatriculeDepart.getText().trim();
        String immatriculation = TextImmatriculationDepart.getText().trim();

        if (matricule.isEmpty() || immatriculation.isEmpty()) {
            MessageDepart.setStyle("-fx-text-fill: red;");
            MessageDepart.setText("Veuillez remplir tous les champs");
            return;
        }

        if (matricule.length() != 6) {
            MessageDepart.setStyle("-fx-text-fill: red;");
            MessageDepart.setText("Le matricule doit contenir 6 chiffres");
            return;
        }

        try {
            Integer.parseInt(matricule);
            reservationService.enregistrerDepart(matricule, immatriculation);

            TextMatriculeDepart.clear();
            TextImmatriculationDepart.clear();

            MessageDepart.setStyle("-fx-text-fill: green;");
            MessageDepart.setText("Le Départ de " + matricule + " avec " + immatriculation + "a été enregistré");

        } catch (NumberFormatException e) {
            MessageDepart.setStyle("-fx-text-fill: red;");
            MessageDepart.setText("Le matricule ne doit contenir que des chiffres");

        } catch (RegleGestionException e) {
            MessageDepart.setStyle("-fx-text-fill: red;");
            MessageDepart.setText("Refusé : " + e.getMessage());
        }
    }

    @FXML
    private void onValiderRetourClick() {

    }
}
