package fr.vannes.FXController;

import fr.vannes.Modele.Salarie;
import fr.vannes.Modele.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private final ObservableList<Vehicule> vehicules = FXCollections.observableArrayList(
            new Vehicule(10, "DD-555-DD", "BMW", "325TDS", LocalDate.now(), 50000),
            new Vehicule(11, "DD-444-DD", "BMW", "M3", LocalDate.now(), 50000),
            new Vehicule(12, "DD-333-DD", "Citroen", "C3", LocalDate.now(), 50000),
            new Vehicule(13, "DD-222-DD", "Renault", "Clio", LocalDate.now(), 50000));

    private final ObservableList<Salarie> salaries = FXCollections.observableArrayList(
            new Salarie("111112", "Person", "Yanis"),
            new Salarie("111113", "Dupont", "Jean"),
            new Salarie("111114", "Dupont", "Géraldine"),
            new Salarie("111115", "Le Goff", "Morgane"));


    @FXML
    private void initialize() {

        // Tableau de véhicules
        Immatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        Marque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        Modele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        MiseCirculation.setCellValueFactory(new PropertyValueFactory<>("date_mise_circulation"));
        Km.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));
        Etat.setCellValueFactory(new PropertyValueFactory<>(""));

        TableauVehicule.setItems(vehicules);

        // Tableau des salaries
        Matricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        Nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        Prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableauSalarie.setItems(salaries);

        // Remplit ou vide les champs selon la ligne sélectionnée
        /*TableauVehicule.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            name.setText(newP == null ? "" : newP.getName());
            surname.setText(newP == null ? "" : newP.getSurname());
        });*/
    }
}
