package Util;



import Modele.Fichier;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;

/**
 * Objet d'un fichier PDF.
 */
public class PDFfile implements Fichier {
    private final String path;
    private final String content;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public File getFile() {
        return new File(getPath());
    }

    /**
     * Créée un objet PDFfile.
     *
     * @param path    Le chemin de destination
     * @param content Le contenu textuel du fichier
     */
    public PDFfile(String path, String content) {
        this.path = path;
        this.content = content;
    }

    /**
     * Lis les contenus du fichier PDF dans la console.
     */
    @Override
    public void readFile() throws IOException {
        PdfReader reader = new PdfReader(path);
        PdfDocument doc = new PdfDocument(reader);
        int num = doc.getNumberOfPages();

        for (int i = 1; i <= num; i++) {
            String str = PdfTextExtractor.getTextFromPage(doc.getPage(i));
            System.out.println(str);
        }

        doc.close();
    }

    /**
     * Écrit le document PDF.
     */
    @Override
    public void writeFile() throws IOException {
        // Gestionnaire de l'écriture dans le fichier pdf
        PdfWriter writer = new PdfWriter(path);
        // Création d'un document avec plusieurs pages
        PdfDocument pdfDoc = new PdfDocument(writer);
        // Création du contenu de la page du document
        Document doc = new Document(pdfDoc);

        // Définition de la police et de la taille du texte avant l'écriture
        PdfFont myFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        doc.setFont(myFont);
        doc.setFontSize(12);
        doc.setTextAlignment(TextAlignment.CENTER);

        // Sépare les lignes une par une en les scindant à chaque line break grâce à "\\R"
        for (String line : content.split("\\R", -1)) {
            doc.add(new Paragraph(line));
        }

        // Ferme le doc et finalise les modifications
        doc.close();
    }
}
