package fr.vannes.FXController;

import Data.DataFactory;
import fr.vannes.DAO.ReservationDAOImpl;
import fr.vannes.DAO.VehiculeDAOImpl;
import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.FXApplication.AuthApplication;
import fr.vannes.Modele.FichierExport;
import fr.vannes.Modele.Flotte;
import fr.vannes.Modele.Vehicule;
import Service.MailService;
import Service.PdfService;
import fr.vannes.Service.ExportService;
import fr.vannes.Service.VehiculeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import sendinblue.ApiException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class ExportController {

    @FXML
    private TableView<FichierExport> listeFichiers;
    @FXML
    private TableColumn<FichierExport, String> nom;
    @FXML
    private TableColumn<FichierExport, Long> taille;
    @FXML
    private TableColumn<FichierExport, String> etat;

    @FXML
    private Button btnGenerer;
    @FXML
    private Button btnExporter;
    @FXML
    private Button btnLireFichier;
    @FXML
    private Button btnSupprimer;


    private final ObservableList<FichierExport> fichiers = FXCollections.observableArrayList();
    private PdfService pdfService;
    private MailService mailService;
    private ExportService exportService;
    private VehiculeService vehiculeService;
    private String exportPath;

    private void chargerFichiersExport(String nomDossier) {
        listeFichiers.getItems().clear();
        Path dossier = Paths.get(nomDossier);
        if (!Files.exists(dossier)) {
            throw new IllegalArgumentException("Le nom dossier n'existe pas");
        }

        try (Stream<Path> stream = Files.list(dossier)) {
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
    private void initialize() throws IOException {
        mailService = DataFactory.getInstance().getMailService();
        exportService = DataFactory.getInstance().getExportService();
        pdfService = DataFactory.getInstance().getPdfService();
        vehiculeService = DataFactory.getInstance().getVehiculeService();
        exportPath = DataFactory.getInstance().getExportPath();

        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        taille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        etat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        listeFichiers.setItems(fichiers);
        chargerFichiersExport("export");

        // Remplit ou vide les champs selon la ligne sélectionnée
        listeFichiers.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP == null) {
                btnSupprimer.setDisable(true);
                return;
            }

            btnSupprimer.setDisable(false);

            File fichierPhysique = new File("export", newP.getNom());
            if (!fichierPhysique.exists()) return;

        });
        btnSupprimer.setDisable(true);
    }

    @FXML
    private void onLireFichierButtonClick() {
        FichierExport selection = listeFichiers.getSelectionModel().getSelectedItem();
        if (selection != null) {
            File fichierPhysique = new File("export", selection.getNom());
            if (fichierPhysique.exists()) {
                // Utilise l'API Desktop pour ouvrir le fichier avec l'application par défaut
                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            // début déchiffrement
                            if(selection.getEtat() == "Chiffré") {
                                File fichierTemp = null;
                                String contenuLisible = exportService.lire(selection.getNom());

                                fichierTemp = File.createTempFile("view_", ".html");

                                fichierTemp.deleteOnExit();

                                try (FileWriter fw = new FileWriter(fichierTemp)) {
                                    fw.write(contenuLisible);
                                }

                                Desktop.getDesktop().open(fichierTemp);

                            } else {
                                Desktop.getDesktop().open(fichierPhysique);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }


        @FXML
        protected void onAccueilButtonClick () {
            try {
                // charger le fichier FXML de la page de destination
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/accueil-view.fxml"));
                Parent root = loader.load();

                // récupérer la fenêtre (Stage) actuelle à partir d'un composant existant (votre table 'nom')
                Stage stage = (Stage) listeFichiers.getScene().getWindow();

                // injecter la nouvelle scène dans la fenêtre
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Gérez l'erreur ici (ex: afficher une alerte si le FXML est introuvable)
            }
        }

        @FXML
        protected void onVehiculeButtonClick () {
            try {
                // charger le fichier FXML de la page de destination
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/vehicule-view.fxml"));
                Parent root = loader.load();

                // récupérer la fenêtre (Stage) actuelle à partir d'un composant existant (votre table 'nom')
                Stage stage = (Stage) listeFichiers.getScene().getWindow();

                // injecter la nouvelle scène dans la fenêtre
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Gérez l'erreur ici (ex: afficher une alerte si le FXML est introuvable)
            }
        }

        @FXML
        protected void onReservationButtonClick () {
            try {
                // charger le fichier FXML de la page de destination
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/reservation-view.fxml"));
                Parent root = loader.load();

                // récupérer la fenêtre (Stage) actuelle à partir d'un composant existant (votre table 'nom')
                Stage stage = (Stage) listeFichiers.getScene().getWindow();

                // injecter la nouvelle scène dans la fenêtre
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Gérez l'erreur ici (ex: afficher une alerte si le FXML est introuvable)
            }
        }

        @FXML
        protected void onGenererButtonClick () {
            try {
                btnGenerer.setDisable(true);
                listeFichiers.setPlaceholder(new ProgressIndicator());
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000); // Simulation du traitement
                        pdfService.exporterFlottePdf(donneesFlotte());
                        mailService.envoyer(exportPath);
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    btnGenerer.setDisable(false);
                    chargerFichiersExport("export");
                });

                task.setOnFailed(e -> {
                    btnGenerer.setDisable(false);
                    task.getException().printStackTrace();
                    listeFichiers.setPlaceholder(new Label("Erreur de chargement : "
                            + task.getException().getMessage()));
                });

                Thread thread = new Thread(task, "load-media");
                thread.setDaemon(true);
                thread.start();
            } catch (RegleGestionException e) {
                System.err.println(e.getMessage());
            }
        }

        @FXML
        protected void onExporterButtonClick () {
            try {
                btnExporter.setDisable(true);
                listeFichiers.setPlaceholder(new ProgressIndicator());
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000); // Simulation du traitement
                        exportService.exporterSalaries();
                        exportService.exporterReservations();
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    btnExporter.setDisable(false);
                    chargerFichiersExport("export");
                });

                task.setOnFailed(e -> {
                    btnExporter.setDisable(false);
                    task.getException().printStackTrace();
                    listeFichiers.setPlaceholder(new Label("Erreur de chargement : "
                            + task.getException().getMessage()));
                });

                Thread thread = new Thread(task, "load-media");
                thread.setDaemon(true);
                thread.start();
            } catch (RuntimeException e) {
                btnExporter.setDisable(false);
                System.err.println(e.getMessage());
            }
        }

        @FXML
        protected void onSupprimerButtonClick () {
            FichierExport fichierSelectionne = listeFichiers.getSelectionModel().getSelectedItem();

            if (fichierSelectionne == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un fichier à supprimer", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce fichier ?", ButtonType.YES, ButtonType.NO);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {

                        btnSupprimer.setDisable(true);
                        listeFichiers.setPlaceholder(new ProgressIndicator());

                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                Thread.sleep(3000); // Simulation du traitement
                                // début de la suppression
                                Path cheminFichier = Paths.get("export", fichierSelectionne.getNom());
                                Files.deleteIfExists(cheminFichier);


                                return null;
                            }
                        };

                        task.setOnSucceeded(e -> {
                            btnSupprimer.setDisable(false);
                            // supprimer l'élément de la liste ObservableList maj de la tableview
                            fichiers.remove(fichierSelectionne);
                            chargerFichiersExport("export");
                        });

                        task.setOnFailed(e -> {
                            btnSupprimer.setDisable(false);
                            task.getException().printStackTrace();
                            listeFichiers.setPlaceholder(new Label("Erreure de chargement : "
                                    + task.getException().getMessage()));
                        });

                        Thread thread = new Thread(task, "delete-file");
                        thread.setDaemon(true);
                        thread.start();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        protected String donneesFlotte () {
            String res = "";

            try {
                List<Vehicule> flotte = vehiculeService.getFlotte();

                res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
                res += String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", "Immat.", "Marque", "Modèle", "Mise circ", "Km", "Etat");
                for (Vehicule v : flotte) {
                    Boolean enDeplacement = vehiculeService.estEnDeplacement(v.getId());
                    res += String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", v.getImmatriculation(), v.getMarque(), v.getModele(), v.getDate_mise_circulation(), v.getKilometrage(), enDeplacement ? "En déplacement" : "Disponible");
                    ;
                }
                res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
            } catch (RegleGestionException e) {
                return e.getMessage();
            }
            return res;
        }

    }
