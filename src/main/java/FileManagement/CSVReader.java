package FileManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReader {

    private String splitBy;

    public CSVReader (String splitBy) {
        this.splitBy = splitBy;
    }

    public ArrayList<String[]> splitCSV(String filePath) {
        String line = "";
        ArrayList splittedCSV = new ArrayList<String[]>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] words = line.split(this.splitBy);

                splittedCSV.add(words);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return splittedCSV;
    } // splitCSV ()

}
