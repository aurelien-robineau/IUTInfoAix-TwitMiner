package FileManagement;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVToTransConverter {

    /**
     * Convert a .csv file into a .trans file.
     * @return .trans file
     */
    public static String convertToTrans(String csvFile) {
        CSVReader reader = new CSVReader(";");
        ArrayList<String[]> splitCSV = reader.splitCSV(csvFile);

        HashMap<String, Integer> knownWords = new HashMap<String, Integer>();
        int lastValue = 0;
        ArrayList<ArrayList<Integer>> splitTrans = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < splitCSV.size(); i++) {
            String[] line = splitCSV.get(i);
            splitTrans.add(new ArrayList<Integer>());

            for (String word: line) {
                if(knownWords.containsKey(word)) {
                    splitTrans.get(i).add(knownWords.get(word));
                } else {
                    ++lastValue;
                    knownWords.put(word, lastValue);
                    splitTrans.get(i).add( lastValue );
                }

            }
        }
        return splitTransToString(splitTrans);
    }

    private static void makeTransFile(String transFile, String destFile) {
        try {
            FileWriter writer = new FileWriter(destFile);
            writer.write(transFile);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String splitTransToString(ArrayList<ArrayList<Integer>> splitTrans) {
        StringBuilder fileStr = new StringBuilder();
        for (ArrayList<Integer> line: splitTrans) {
            for (Integer word: line) {
                fileStr.append(word + " ");
            }
            fileStr.append('\n');
        }

        return fileStr.toString();
    }
}
