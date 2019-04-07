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
import java.util.Collection;

public class HomeController extends VBox {

    private StringProperty STEP_MESSAGE = new SimpleStringProperty("");

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

    @FXML
    private Label stepMessage;

    @FXML
    private ProgressIndicator stepIndicator;

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

        stepMessage.setVisible(false);
        stepIndicator.setVisible(false);

        stepMessage.textProperty().bind(STEP_MESSAGE);

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

            stepMessage.setVisible(true);
            stepIndicator.setVisible(true);

            System.out.println("/!\\ Initializing parameters ...");
            STEP_MESSAGE.setValue(" Initialisation des paramètres");
            main.initializeParams(query, nbTweets);

            STEP_MESSAGE.setValue(" Récupération des tweets");
            Collection<String[]> tweets = main.getData();

            STEP_MESSAGE.setValue(" Création du fichier .csv");
            main.printStringToCsv(tweets);

            System.out.println("/!\\ Cleaning data ...");
            STEP_MESSAGE.setValue(" Nettoyage des données");
            main.cleanData("./src/main/resources/dictionary.csv");

            System.out.println("/!\\ Creating .trans file ...");
            STEP_MESSAGE.setValue(" Création du fichier .trans");
            main.createTransFile();

            System.out.println("/!\\ Run apriori ...");
            STEP_MESSAGE.setValue(" Calcul des motifs fréquents");
            main.runApriori();

            //stepMessage.setText("Extraction des règles d'association");
            //Extracteur.getInstance().readData(???, main.aprioriFilePath + query + ".out");

            updateExistingData();

            //STEP_MESSAGE.setValue("");
            //stepMessage.setVisible(false);
            //stepIndicator.setVisible(false);

            displayResults();
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

    private void displayResults() {
        String fileName = main.transFilePath + main.SEARCH_TERM + ".trans";

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
}