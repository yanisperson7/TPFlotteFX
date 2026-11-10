package fr.vannes.Modele;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.crypto.ExemptionMechanismException;
import java.io.File;

public class FichierExport {
    private final SimpleStringProperty nom;
    private final SimpleLongProperty taille;
    private final SimpleStringProperty etat;

    public FichierExport(File fichier) {
        this.nom = new SimpleStringProperty(fichier.getName());
        this.taille = new SimpleLongProperty(fichier.length());
        this.etat = new SimpleStringProperty();
        String extension = "";
        int pointIndex = fichier.toString().lastIndexOf('.');
        if (pointIndex != -1) {
            extension = fichier.toString().substring(pointIndex + 1);
        }
        if (extension.isEmpty()) {
            throw new IllegalArgumentException("Erreur, fichier sans extension");
        }
        if (extension.equalsIgnoreCase("enc")) {
            this.etat.setValue("Chiffré");
        }
        this.etat.setValue("Lisible");
    }

    public String getNom() {
        return nom.get();
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public long getTaille() {
        return taille.get();
    }

    public SimpleLongProperty tailleProperty() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille.set(taille);
    }

    public String getEtat() {
        return etat.get();
    }

    public SimpleStringProperty etatProperty() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat.set(etat);
    }
}
