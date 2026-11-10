package fr.vannes.FXController;

import Data.DataFactory;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import fr.vannes.Modele.HistoriqueReservations;
import fr.vannes.Modele.Reservation;
import fr.vannes.Modele.Salarie;
import fr.vannes.Modele.Vehicule;
import fr.vannes.Service.ReservationService;
import fr.vannes.Service.VehiculeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ReservationController {

    // Liste Véhicules
    @FXML private TableColumn<Vehicule, String> Immatriculation;
    @FXML private TableColumn<Vehicule, String> Marque;
    @FXML private TableColumn<Vehicule, String> Modele;
    @FXML private TableColumn<Vehicule, LocalDate> MiseCirculation;
    @FXML private TableColumn<Vehicule, Integer> Km;
    @FXML private TableColumn<Vehicule, String> Etat;
    @FXML private TableView<Vehicule> TableauVehicule;

    //Liste salariés
    @FXML private TableColumn<Salarie, String> Matricule;
    @FXML private TableColumn<Salarie, String> Nom;
    @FXML private TableColumn<Salarie, String> Prenom;
    @FXML private TableView<Salarie> TableauSalarie;

    // Historique des salariés
    @FXML private TableColumn<Reservation, String> DateDepart;
    @FXML private TableColumn<Reservation, String> DateRetour;
    @FXML private TableColumn<Reservation, String> ImmatriculationHistorique;
    @FXML private TableColumn<Reservation, String> Véhicule;
    @FXML private TableView<Reservation> TableauHistorique;

    private ReservationService reservationService;
    private VehiculeService vehiculeService;

    private final ObservableList<Vehicule> vehicules = FXCollections.observableArrayList();
    private final ObservableList<Salarie> salaries = FXCollections.observableArrayList();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();


    @FXML
    private void initialize() throws IOException {

        reservationService = DataFactory.getInstance().getReservationService();
        vehiculeService = DataFactory.getInstance().getVehiculeService();

        // Tableau de véhicules
        Immatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        Marque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        Modele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        MiseCirculation.setCellValueFactory(new PropertyValueFactory<>("date_mise_circulation"));
        Km.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));
        Etat.setCellValueFactory(new PropertyValueFactory<>(""));

        //TableauVehicule.setItems(vehicules);
        TableauVehicule.setItems(FXCollections.observableArrayList(vehiculeService.getFlotte()));

        // Tableau des salaries
        Matricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        Nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        Prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableauSalarie.setItems(FXCollections.observableArrayList(reservationService.listerSalaries()));

        // Historique des salariés
        DateDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        DateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        ImmatriculationHistorique.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        Véhicule.setCellValueFactory(new PropertyValueFactory<>("vehiculeComplet"));
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
}
