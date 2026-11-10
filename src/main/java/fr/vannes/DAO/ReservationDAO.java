package fr.vannes.DAO;

import fr.vannes.Modele.Reservation;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ReservationDAO {
    void creer(Reservation r);
    void cloturer(int idVehicule, Timestamp dateRetour, int kmRetour);
    boolean estEnDeplacement(int idVehicule);
    boolean salarieEnDeplacement(String matricule);
    List<Reservation> listerEnCours();
    List<Reservation> listerParSalarie(String matricule);
    List<Reservation> lister();
    Reservation trouverDerniereParVehicule(int vehiculeId);
    List<Integer> vehiculeNd();
}