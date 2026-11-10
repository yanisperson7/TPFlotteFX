package FXController;

import DAO.ReservationDAOImpl;
import DAO.SalarieDAOImpl;
import DAO.VehiculeDAOImpl;
import Exceptions.RegleGestionException;
import FXApplication.AuthApplication;
import Modele.FichierExport;
import Modele.Flotte;
import Modele.Vehicule;
import Service.MailService;
import Service.PdfService;
import Service.VehiculeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import sendinblue.ApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class ExportController {

    @FXML private TableView<FichierExport> listeFichiers;
    @FXML private TableColumn<FichierExport, String> nom;
    @FXML private TableColumn<FichierExport, Long> taille;
    @FXML private TableColumn<FichierExport, String> etat;

    @FXML private TextField txtNom;
    @FXML private TextField txtTaille;
    @FXML private TextField txtEtat;

    private final ObservableList<FichierExport> fichiers = FXCollections.observableArrayList();
    private PdfService pdfService;
    private MailService mailService;
    private VehiculeService vehiculeService;
    private String path;

//    public ExportController(Window w) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/export-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        Stage stage = (Stage) w;
//        stage.setTitle("Authentification");
//        stage.setScene(scene);
//        stage.sizeToScene();
//        stage.centerOnScreen();
//        stage.show();
//    }


    private void chargerFichiersExport(String nomDossier) {
        Path dossier = Paths.get(nomDossier);
        if(!Files.exists(dossier)){
            throw new IllegalArgumentException("Le nom dossier n'existe pas");
        }

        try(Stream<Path> stream = Files.list(dossier)) {
            stream.filter(Files::isRegularFile)
                    // conversion en File pour FichierExport
                    .map(Path::toFile)
                    .map(FichierExport::new)
                    // ajout direct dans la liste observable
                    .forEach(fichiers::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        this.path = "export/" + LocalDateTime.now().format(formatter) + "_etat-flotte.pdf";
        this.pdfService = new PdfService(path);
        this.mailService = new MailService();
        this.vehiculeService = new VehiculeService(new Flotte(), new ReservationDAOImpl(), new VehiculeDAOImpl());

        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        taille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        etat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        listeFichiers.setItems(fichiers);
        chargerFichiersExport("export");

        // Remplit ou vide les champs selon la ligne sélectionnée
        listeFichiers.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP == null) {
                txtNom.setText("");
                txtTaille.setText("");
                txtEtat.setText("");
            } else {
                txtNom.setText(newP.getNom() == null ? "" : newP.getNom());
                txtTaille.setText(String.valueOf(newP.getTaille()));
                txtEtat.setText(newP.getEtat() == null ? "" : newP.getEtat());
            }
        });
    }

    @FXML
    protected void onGenererButtonClick() {
        try {

            pdfService.exporterFlottePdf(afficherFlotte());
            mailService.envoyer(this.path);
        } catch (RegleGestionException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected String afficherFlotte(){
        String res = "";

        try {
            List<Vehicule> flotte = vehiculeService.getFlotte();

            res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
            res += String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", "Immat.", "Marque", "Modèle", "Mise circ", "Km", "Etat");
            for (Vehicule v : flotte){
                Boolean enDeplacement = vehiculeService.estEnDeplacement(v.getId());
                res+=String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", v.getImmatriculation(), v.getMarque(), v.getModele(), v.getDate_mise_circulation(), v.getKilometrage(), enDeplacement?"En déplacement":"Disponible");;
            }
            res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
        }catch (RegleGestionException e){
            return e.getMessage();
        }

        return res;

    }

}
