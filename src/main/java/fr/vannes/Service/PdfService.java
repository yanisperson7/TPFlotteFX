package Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;


public class PdfService {

    private String fichier;
//    private final VehiculeDAO vehiculeDAO = new VehiculeDAOImpl();

    public PdfService(String fichier) {
        this.fichier = fichier;
    }

    public void exporterFlottePdf(String flotte) throws IOException {
        //gestionnaire de l'ecriture dans le fichier pdf
        PdfWriter writer = new PdfWriter(this.fichier);
        //creation d'un document avec plusieurs page
        PdfDocument pdfDoc = new PdfDocument(writer);
        //creation du contenu de la page du document
        Document doc = new Document(pdfDoc);

        PdfFont myFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph p = new Paragraph("Etat de la Flotte \n");
        p.add(flotte);
        doc.add(p);
        doc.close();
    }
}
