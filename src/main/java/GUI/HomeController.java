package GUI;

import Main.LineCounter;
import Main.main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;

public class HomeController extends VBox {

    @FXML
    private CheckBox useOldData;

    @FXML
    private TextField newQuery;

    @FXML
    private Button btnMineNewData;

    @FXML
    private TextField nbOfTweetsField;

    @FXML
    private TextField nbOfExistingTweetsField;

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
        nbOfExistingTweetsField.setDisable(true);

        // Force the field to be numeric only
        nbOfTweetsField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    nbOfTweetsField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                // Avoid empty text
                if (newValue.equals("")) {
                    nbOfTweetsField.setText("0");
                }
                // Delete first 0
                if (newValue.length() > 1 && newValue.substring(0,1).equals("0")) {
                    nbOfTweetsField.setText(newValue.substring(1,newValue.length()));
                }
            }
        });

        updateExistingData();
    } // constructor

    @FXML
    private void mine() {
        if(!useOldData.isSelected()) {
            int nbTweets = Integer.parseInt(nbOfTweetsField.getText());
            String query = newQuery.getText();

            main.getFreshData(query, nbTweets);

            //Extracteur.getInstance().readData(???, main.aprioriFilePath + query + ".out");

            updateExistingData();
        }
        else {
            String query = existingData.getValue().toString();
            System.out.println(query);
            //Extracteur.getInstance().readData(???, main.aprioriFilePath + query + ".out");
        }
    } // mine ()

    @FXML
    private void changeDataSelection() {
        newQuery.setDisable(!newQuery.isDisabled());
        btnMineNewData.setDisable(!btnMineNewData.isDisabled());
        nbOfTweetsField.setDisable(!nbOfTweetsField.isDisabled());

        existingData.setDisable(!existingData.isDisabled());
        btnMineExistingData.setDisable(!btnMineExistingData.isDisabled());
        nbOfExistingTweetsField.setDisable(!nbOfExistingTweetsField.isDisabled());
    } // changeDataSelection ()

    @FXML
    private void updateNumberOfExistingTweets() {
        String file = "./src/main/resources/CSV/" + existingData.getValue() + ".csv";
        try {
            Integer nbOfLines = LineCounter.countLines(file);
            nbOfExistingTweetsField.setText(nbOfLines.toString());
        } catch (Exception e) {
            nbOfExistingTweetsField.setText("Erreur");
            System.err.println(e.getMessage());
        }
    }

    private void updateExistingData() {
        File dataFolder = new File("./src/main/resources/trans");
        File[] dataFiles = dataFolder.listFiles();

        ObservableList<String> options = FXCollections.observableArrayList();
        for(File file: dataFiles) {
            // File name without extension
            options.add(file.getName().split("\\.")[0]);
        }

        existingData.setItems(options);
    }
}