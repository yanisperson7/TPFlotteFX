package fr.vannes.Controleur;

import fr.vannes.Exceptions.*;
import fr.vannes.Modele.*;
import fr.vannes.Service.*;
import javafx.scene.Scene;
import sendinblue.ApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlotteController {
    VehiculeService vehiculeService;
    private Service.PdfService pdfService;
    private Service.MailService mailService;
    private String path;

    public FlotteController(VehiculeService vehiculeService) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        this.vehiculeService = vehiculeService;
        this.path = "export/" + LocalDateTime.now().format(formatter) + "_etat-flotte.pdf";
        this.pdfService = new Service.PdfService(path);
        this.mailService = new Service.MailService();
    }


    public String afficherFlotte(){
        String res = "";

        try {
            List<Vehicule> flotte = vehiculeService.getFlotte();

            res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
            res += String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", "Immat.", "Marque", "Modèle", "Mise circ", "Km", "Etat");
            for (Vehicule v : flotte){
                Boolean enDeplacement = false;
                res+=String.format("| %-10.10s| %-9.9s| %-10.10s| %-11.11s| %-10.10s| %-15.15s|\n", v.getImmatriculation(), v.getMarque(), v.getModele(), v.getDate_mise_circulation(), v.getKilometrage(), enDeplacement?"En déplacement":"Disponible");;
            }
            res += "+-----------+----------+-----------+------------+-----------+----------------+\n";
        }catch (RegleGestionException e){
            return e.getMessage();
        }

        return res;

    }

    public String ajouterVehicule(VehiculeRecord vehiculeRecord){

        try {
            Vehicule vehicule = new Vehicule(
                    1,
                    vehiculeRecord.immat(),
                    vehiculeRecord.marque(),
                    vehiculeRecord.modele(),
                    vehiculeRecord.dateMiseCirculation(),
                    vehiculeRecord.kilometrage()

            );
            vehiculeService.ajouterVehicule(vehicule);
        } catch (RegleGestionException e) {
            return e.getMessage();
        }

        return "Le véhicule a été ajouté";
    }

    public String retirerVehicule(String immat){
        try {
            vehiculeService.retirerVehicule(immat);
            return "Le véhicule a été retiré";
        }catch (RegleGestionException e){
            System.out.println(e.getMessage());
        }
        return "Echec lors du retrait du véhicule";
    }

    public  String rechercherVehicule(String immat){
        try {
            String res = "";
            String etat = "";
            Vehicule vehicule = vehiculeService.rechercherParImmatriculation(immat);
            if(false){
                Reservation reservation     = vehiculeService.getReservationEncours(vehicule.getId());
                Salarie salarie             = reservation.getSalarie();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String depart               = reservation.getDateDepart().toLocalDateTime().format(formatter);
                String prenom               = salarie.getPrenom();
                String nom                  = salarie.getNom();
                String matricule            = salarie.getMatricule();

                etat = "Etat : en déplacement depuis le " + depart + " avec " + prenom +
                        " " + nom + " " + "(" + matricule + ")";
            }
            else etat="Etat : Disponible";
            res += vehicule.getImmatriculation() + " " + vehicule.getMarque() + " " + vehicule.getModele() + " " +
                    " Mise en circulation depuis le " + vehicule.getDate_mise_circulation() + ", " + vehicule.getKilometrage() + " km\n";
            res += etat;
            return res;
        }catch (RegleGestionException e){
            return e.getMessage();
        }

    }

    public String trierFlotte(int tri){
        List<Vehicule> vehicules = new ArrayList<>();
        String res = "";
        switch(tri){
            case 1:
                vehicules = vehiculeService.trierParMarqueEtModele();
                break;
            case 2:
                vehicules = vehiculeService.trierParDateMiseCirculation();
                break;
            case 3:
                vehicules = vehiculeService.trierParKmDecroissant();
                break;
        }
        for (Vehicule vehicule: vehicules){
            res += vehicule.getImmatriculation() + " " + vehicule.getMarque() + " " + vehicule.getModele() + " " + vehicule.getKilometrage() +" km\n";
        }

        return res;
    }

    public void etatFlottePdf(){
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
}
