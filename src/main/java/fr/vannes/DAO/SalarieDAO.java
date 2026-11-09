package fr.vannes.DAO;

import fr.vannes.Modele.Salarie;

import java.util.List;

public interface SalarieDAO {
    void creer(Salarie s);
    Salarie trouverParMatricule(String matricule);
    List<Salarie> lister();
}
