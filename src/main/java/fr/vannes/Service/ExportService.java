package fr.vannes.Service;

import fr.vannes.DAO.ReservationDAO;
import fr.vannes.DAO.SalarieDAO;
import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.Modele.Reservation;
import fr.vannes.Modele.Salarie;
import fr.vannes.Util.ChiffrementAES;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {
    private final SalarieDAO salarieDAO;
    private final ReservationDAO reservationDAO;
    private SecretKey cle;

    public ExportService(SalarieDAO salarieDAO, ReservationDAO reservationDAO) {
        this.salarieDAO = salarieDAO;
        this.reservationDAO = reservationDAO;

        try {
            Path fichierCle = Path.of("aes.key");
            if (Files.exists(fichierCle)) {
                cle = ChiffrementAES.chargerCle(fichierCle);      // lancements suivants
            } else {
                cle = ChiffrementAES.genererCle();                // premier lancement
                ChiffrementAES.sauvegarderCle(cle, fichierCle);
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la clé AES", e);
        }
    }

    public void exporterSalaries() {
        List<Salarie> salaries = salarieDAO.lister();

        String texte = "matricule;nom;prenom\n";
        for (Salarie s : salaries) {
            texte = texte + s.getMatricule() + ";" + s.getNom() + ";" + s.getPrenom() + "\n";
        }

        try {
        ChiffrementAES.chiffrer(texte, cle, Path.of("export/salaries.enc"));
            System.out.println("[Export] salaries.enc : " + salaries.size() + " salariés exportés et chiffrés.");
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Échec de l'export des salariés", e);
        }
    }

    public void exporterReservations() {
        List<Reservation> reservations = reservationDAO.lister();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String texte = "matricule;immatriculation;date_depart;date_retour;km_retour\n";
        for (Reservation r : reservations) {
            String dateDepart = r.getDateDepart().toLocalDateTime().format(f);
            String dateRetour = "";
            String kmRetour = "";

            if (!r.estEnCours()) {
                dateRetour = r.getDateRetour().toLocalDateTime().format(f);
                kmRetour = String.valueOf(r.getKmRetour());
            }

            texte = texte + r.getSalarie().getMatricule() + ";"
                    + r.getVehicule().getImmatriculation() + ";"
                    + dateDepart + ";" + dateRetour + ";" + kmRetour + "\n";
        }

        try {
            ChiffrementAES.chiffrer(texte, cle, Path.of("export/reservations.enc"));
            System.out.println("[Export] reservations.enc : " + reservations.size() + " réservations exportées et chiffrées.");
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Échec de l'export des réservations", e);
        }
    }

    public String lire(String nomFichier) {
        try {
            return ChiffrementAES.dechiffrer(cle, Path.of(nomFichier));
        } catch (GeneralSecurityException | IOException e) {
            throw new RegleGestionException("impossible de lire le fichier " + nomFichier);
        }
    }
}
