package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController extends VBox {
    // **************************************************
    // Fields
    // **************************************************

    @FXML
    /**
     * Field that contains everything in the page
     */
    private VBox vBox;

    @FXML
    /**
     * Field where to write identifier
     */
    private TextField identifiant;

    @FXML
    /**
     * Field where to write password
     */
    private PasswordField mdp;

    @FXML
    /**
     * Logo checked
     */
    private ImageView logo;

    @FXML
    /**
     * Button to connect
     */
    private Button connexion;

    @FXML
    /**
     * Button to create account
     */
    private Button creerCompte;

    @FXML
    /**
     * Label to click on if you forgot your password
     */
    private Label mdpOublie;

    @FXML
    /**
     * Labal to click on to skip identification (to use the app without an account)
     */
    private Label skip;

    /**
     * TRUE if connection has been done successfully
     */
    private boolean connectionSuccessful = false;

    // **************************************************
    // Constructors
    // **************************************************

    /**
     * Default constructor
     */
    public HomeController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Home.fxml"));
        //fxmlLoader.setRoot(this);
        //fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        /*
        identifiant.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstTime.get()) {
                vBox.requestFocus();
                firstTime.setValue(false);
            }
        });*/
    }
}