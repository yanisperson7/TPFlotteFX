package fr.vannes.Controleur;

import fr.vannes.Exceptions.RegleGestionException;
import fr.vannes.Service.ExportService;

public class EtatController {
    ExportService exportService;

    public EtatController(ExportService exportService) {
        this.exportService = exportService;
    }

    public String exporter() {
        try {
            exportService.exporterSalaries();
            exportService.exporterReservations();
            return "Exports terminés.";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String lireFichier(String nomFichier) {
        try {
            return "Contenu déchiffré de " + nomFichier + " :\n" + exportService.lire(nomFichier);
        } catch (RegleGestionException e) {
            return e.getMessage();
        }
    }
}
