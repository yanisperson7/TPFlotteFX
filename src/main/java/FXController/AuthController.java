package FXController;

import Service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.synedra.validatorfx.Validator;

import javax.crypto.ExemptionMechanismException;
import java.io.IOException;

public class AuthController {
    AuthService authService;

    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private TextFlow error;

    public AuthController() {
        try {
            this.authService = new AuthService("user.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClearClick() {
        login.clear();
        password.clear();
        error.getChildren().clear();
    }

    @FXML
    protected void onFermerButtonClick() {
        Platform.exit();
    }

    @FXML
    protected void onValiderButtonClick() {
        try {
            if(authService.getTentativeRestante() <= 0)
                throw new ExemptionMechanismException("Trop de tentatives invalides !");
            if(!checkError())
                return;
            if(!authService.login(login.getText(), password.getText())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur dans la saisie");
                alert.show();
                return;
            }
//            AccueilController accueilController = new AccueilController();
//            accueilController.accueilScene(login.getScene().getWindow());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur lors de l'authentification");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.show();
        } catch (Exceptions.RegleGestionException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Accès refusé");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.show();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur NullPointerException");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.getMessage());
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.show();

        } catch (ExemptionMechanismException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.getMessage());
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


    public boolean checkError() {
        error.getChildren().clear();
        Validator validator= new Validator();
        validator.createCheck().dependsOn("login",login.textProperty())
                .withMethod(context->{
                    String l= context.get("login");
                    if(l==null || l.isEmpty()){
                        context.error("Le login doit être saisi ! ");
                        addText(error,"Le login  doit être saisi ! ",Color.RED,true);

                    }
                })
                .decorates(login);
        validator.createCheck().dependsOn("password",password.textProperty())
                .withMethod(context->{
                    String p= context.get("password");
                    if(p==null || p.isEmpty()){
                        context.error("Le mot de passe doit être saisi ! ");
                        addText(error,"Le mot de passe doit être saisi ! ",Color.RED,true);
                    }
                })
                .decorates(password);
        return validator.validate();
    }

    /**
     * 🔧 Méthode utilitaire pour ajouter un texte formaté dans un TextFlow
     */
    private void addText(TextFlow flow, String contenu, Color couleur, boolean gras) {
        Text t = new Text(contenu);
        t.setFill(couleur);
        t.setFont(Font.font("Arial", gras ? FontWeight.BOLD : FontWeight.NORMAL, 14));
        flow.getChildren().add(t);
    }

}
