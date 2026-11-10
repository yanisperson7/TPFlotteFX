package Data;

import fr.vannes.DAO.DAOFactory;
import fr.vannes.DAO.ReservationDAO;
import fr.vannes.DAO.SalarieDAO;
import fr.vannes.DAO.VehiculeDAO;
import fr.vannes.Modele.Flotte;
import fr.vannes.Service.AuthService;
import fr.vannes.Service.ExportService;
import fr.vannes.Service.ReservationService;
import fr.vannes.Service.VehiculeService;

import java.io.IOException;

public final class DataFactory {

    private static DataFactory instance;

    private final VehiculeService vehiculeService;
    private final ReservationService reservationService;
    private final ExportService exportService;
    private final AuthService authService;


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


    public static synchronized void fermer() throws InterruptedException {
        if (instance != null) {
            instance.vehiculeService.fermer();
        }
    }
}
