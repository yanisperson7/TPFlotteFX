package Data;

import DAO.DAOFactory;
import DAO.ReservationDAO;
import DAO.SalarieDAO;
import DAO.VehiculeDAO;
import Modele.Flotte;
import Service.AuthService;
import Service.ExportService;
import Service.ReservationService;
import Service.VehiculeService;

public final class DataFactory {

    private static DataFactory instance;

    private final VehiculeService vehiculeService;
    private final ReservationService reservationService;
    private final ExportService exportService;
    private final AuthService authService;


    private DataFactory() {
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


    public static synchronized DataFactory getInstance() {
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
