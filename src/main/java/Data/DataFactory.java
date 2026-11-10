package Data;

import Service.MailService;
import Service.PdfService;
import fr.vannes.DAO.DAOFactory;
import fr.vannes.DAO.ReservationDAO;
import fr.vannes.DAO.SalarieDAO;
import fr.vannes.DAO.VehiculeDAO;
import fr.vannes.Modele.Flotte;
import fr.vannes.Service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DataFactory {

    private static DataFactory instance;

    private final VehiculeService vehiculeService;
    private final ReservationService reservationService;
    private final ExportService exportService;
    private final AuthService authService;
    private final String exportPath;
    private final Service.MailService mailService;
    private final Service.PdfService pdfService;


    private DataFactory() throws IOException {
        DAOFactory daoFactory;
        try {
            daoFactory = DAOFactory.getInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver MySQL introuvable", e);
        }


        VehiculeDAO vehiculeDAO = daoFactory.getVehiculeDAO();
        ReservationDAO reservationDAO = daoFactory.getReservationDAO();
        SalarieDAO salarieDAO = daoFactory.getSalarieDAO();


        Flotte flotte = new Flotte();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        this.exportPath = "export/" + LocalDateTime.now().format(formatter) + "_etat-flotte.pdf";
        this.pdfService = new Service.PdfService(exportPath);
        this.mailService = new Service.MailService();
        vehiculeService = new VehiculeService(flotte, reservationDAO, vehiculeDAO);   // charge la flotte depuis la base
        reservationService = new ReservationService(vehiculeDAO, reservationDAO, salarieDAO);
        exportService = new ExportService(salarieDAO, reservationDAO);
        authService = new AuthService("user.json");
    }


    public static synchronized DataFactory getInstance() throws IOException {
        if (instance == null) {
            instance = new DataFactory();
        }
        return instance;
    }


    public VehiculeService getVehiculeService() {
        return vehiculeService;
    }


    public ReservationService getReservationService() {
        return reservationService;
    }


    public ExportService getExportService() {
        return exportService;
    }


    public AuthService getAuthService() {
        return authService;
    }

    public static void setInstance(DataFactory instance) {
        DataFactory.instance = instance;
    }

    public String getExportPath() {
        return exportPath;
    }

    public MailService getMailService() {
        return mailService;
    }

    public PdfService getPdfService() {
        return pdfService;
    }

    public static synchronized void fermer() throws InterruptedException {
        if (instance != null) {
            instance.vehiculeService.fermer();
        }
    }
}
