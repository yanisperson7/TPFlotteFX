package fr.vannes.FXApplication;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {

        Application.launch(AuthApplication.class, args);
        Application.launch(ReservationApplication.class, args);
        Application.launch(VehiculeApplication.class, args);
        Application.launch(AccueilApplication.class, args);
        Application.launch(ExportApplication.class, args);
    }
}
