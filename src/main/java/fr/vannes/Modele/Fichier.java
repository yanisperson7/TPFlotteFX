package Modele;

import java.io.File;
import java.io.IOException;

public interface Fichier {
    /**
     * Méthode de lecture de fichier
     */
    public void readFile() throws IOException;

    /**
     * Méthode d'écriture de fichier
     */
    public void writeFile() throws IOException;

    String getPath();

    public File getFile();

}
