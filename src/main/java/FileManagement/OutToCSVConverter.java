package FileManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OutToCSVConverter {

    /**
     * Convert a .out file into a .csv file.
     * @return .csv file
     */
    public static String convertToCSV(String filePath) {
        ArrayList<String> lines = new ArrayList<String>();
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {

                line = line.replace(" ", ";");
                line = line.replace("(","");
                line = line.replace(")","");

                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesToString(lines);
    } // convertToCSV ()

    private static String linesToString(ArrayList<String> lines) {
        StringBuilder fileStr = new StringBuilder();
        for (String line: lines) {
            fileStr.append(line);
            fileStr.append('\n');
        }

        return fileStr.toString();
    } // linesToString ()


}
