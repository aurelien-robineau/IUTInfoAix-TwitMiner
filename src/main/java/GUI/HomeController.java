package GUI;

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
    private Label labeltest;

    @FXML
    private CheckBox selectMode;

    @FXML
    private TextField newDataName;

    @FXML
    private Button btnMineNewData;

    @FXML
    private TextField nbOfTweets;

    @FXML
    private TextField nbOfExistingTweets;

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
        nbOfExistingTweets.setDisable(true);

        // Force the field to be numeric only
        nbOfTweets.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    nbOfTweets.setText(newValue.replaceAll("[^\\d]", ""));
                }
                // Avoid empty text
                if (newValue.equals("")) {
                    nbOfTweets.setText("0");
                }
                // Delete first 0
                if (newValue.length() > 1 && newValue.substring(0,1).equals("0")) {
                    nbOfTweets.setText(newValue.substring(1,newValue.length()));
                }
            }
        });

        File dataFolder = new File("./src/main/resources/trans");
        File[] dataFiles = dataFolder.listFiles();

        ObservableList<String> options = FXCollections.observableArrayList();
        for(File file: dataFiles) {
            // File name without extension
            options.add(file.getName().split("\\.")[0]);
        }

        existingData.setItems(options);
    } // constructor

    // From : https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    private Integer countLinesOld(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    } // countLinesOld ()

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
        nbOfTweets.setDisable(!nbOfTweets.isDisabled());

        existingData.setDisable(!existingData.isDisabled());
        btnMineExistingData.setDisable(!btnMineExistingData.isDisabled());
        nbOfExistingTweets.setDisable(!nbOfExistingTweets.isDisabled());
    } // changeDataSelection ()

    @FXML
    private void updateNumberOfExistingTweets() {
        String file = "./src/main/resources/CSV/" + existingData.getValue() + ".csv";
        try {
            Integer nbOfLines = countLinesOld(file);
            nbOfExistingTweets.setText(nbOfLines.toString());
        } catch (Exception e) {
            nbOfExistingTweets.setText("Erreur");
            System.err.println(e.getMessage());
        }
    }
}