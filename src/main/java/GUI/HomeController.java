package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController extends VBox {

    @FXML
    private Label labeltest;

    /**
     * Default constructor
     */
    public HomeController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/Home.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    } // constructor

    @FXML
    private void mine () {
        if (labeltest.getText().equals("")) {
            labeltest.setText("Mine !");
        } else {
            labeltest.setText("");
        }
    }
}