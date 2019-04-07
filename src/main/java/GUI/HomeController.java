package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

public class HomeController extends VBox {

    @FXML
    private Label labeltest;

    @FXML
    private CheckBox selectMode;

    @FXML
    private TextField newDataName;

    @FXML
    private Button btnMineNewData;

    @FXML
    private ComboBox existingData;

    @FXML
    private Button btnMineExistingData;

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

        existingData.setDisable(true);
        btnMineExistingData.setDisable(true);

        File dataFolder = new File("./src/main/resources/trans");
        File[] dataFiles = dataFolder.listFiles();

        ObservableList<String> options = FXCollections.observableArrayList();
        for(File file: dataFiles) {
            // File name without extension
            options.add(file.getName().split("\\.")[0]);
        }

        existingData.setItems(options);
    } // constructor

    @FXML
    private void mine() {
        if (labeltest.getText().equals("")) {
            labeltest.setText("Mine !");
        } else {
            labeltest.setText("");
        }
    } // mine ()

    @FXML
    private void changeDataSelection() {
        newDataName.setDisable(!newDataName.isDisabled());
        btnMineNewData.setDisable(!btnMineNewData.isDisabled());
        existingData.setDisable(!existingData.isDisabled());
        btnMineExistingData.setDisable(!btnMineExistingData.isDisabled());
    } // changeDataSelection ()
}