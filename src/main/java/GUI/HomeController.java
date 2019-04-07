package GUI;

import Main.LineCounter;
import Main.main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.util.ArrayList;

public class HomeController extends VBox {

    private StringProperty STEP_MESSAGE = new SimpleStringProperty("");

    @FXML
    private CheckBox useOldData;

    @FXML
    private TextField minFreq;

    @FXML
    private TextField minConf;

    @FXML
    private TextField minLift;

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

    @FXML
    private TextArea outputFile;

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

        outputFile.setVisible(false);

        existingData.setDisable(true);
        btnMineExistingData.setDisable(true);
        nbOfExistingTweetsField.setDisable(true);

        btnMineNewData.setDisable(true);

        forceNumerics();

        // Cannot mine if field is empty
        newQuery.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 0) {
                    btnMineNewData.setDisable(false);
                } else {
                    btnMineNewData.setDisable(true);
                }
            }
        });

        existingData.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 0) {
                    btnMineExistingData.setDisable(false);
                } else {
                    btnMineExistingData.setDisable(true);
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

            if(query.equals(""))
                query = "untitled";

            main.mine(query, nbTweets);

            minLift.setText(minLift.getText().equals("") ? "1" : minLift.getText());
            minConf.setText(minConf.getText().equals("") ? "0.5" : minConf.getText());
            minFreq.setText(minFreq.getText().equals("") ? "0.1" : minFreq.getText());
            main.processData(Integer.parseInt(minLift.getText()), Float.parseFloat(minFreq.getText()), Float.parseFloat(minConf.getText()), query);

            updateExistingData();
            displayResults(query);
        }
        else {
            String query = existingData.getValue().toString();

            minLift.setText(minLift.getText().equals("") ? "1" : minLift.getText());
            minConf.setText(minConf.getText().equals("") ? "0.5" : minConf.getText());
            minFreq.setText(minFreq.getText().equals("") ? "0.1" : minFreq.getText());
            main.processData(Integer.parseInt(minLift.getText()), Float.parseFloat(minFreq.getText()), Float.parseFloat(minConf.getText()), query);

            displayResults(query);
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

    private void displayResults(String query) {
        String fileName = main.patternFile + query + ".txt";

        outputFile.setVisible(true);

        String line = "";
        StringBuilder fileText = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                fileText.append(line);
                fileText.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputFile.setText(fileText.toString());
    } // displayResults ()

    private void forceNumerics() {
        ArrayList<TextField> textFields = new ArrayList<>();
        textFields.add(minFreq);
        textFields.add(minConf);
        textFields.add(minLift);
        textFields.add(nbOfTweetsField);

        for(TextField textField: textFields) {
            // Force the field to be numeric only
            textField.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {

                    if (!newValue.matches("(?<=^| )\\d+(\\.\\d+)?(?=$| )")) {
                        textField.setText(newValue.replaceAll("[^0-9.]", ""));
                    }
                    // Avoid empty text
                    if (newValue.equals("")) {
                        textField.setText("0");
                    }
                    // Delete first 0
                    if (newValue.length() > 1 && newValue.substring(0,1).equals("0") && !newValue.substring(1,2).equals(".")) {
                        textField.setText(newValue.substring(1,newValue.length()));
                    }
                }
            });
        }
    } // forceNumerics ()
}