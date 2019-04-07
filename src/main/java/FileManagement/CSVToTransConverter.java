package FileManagement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVToTransConverter {

    /**
     * Convert a .csv file into a .trans file.
     * @param csvFilePath Path of the csv file to convert
     * @return Converted file
     */
    public  HashMap<String, Integer> convertToTrans(String csvFilePath, String whereToPrint) {
        CSVReader reader = new CSVReader(";");
        ArrayList<String[]> splitCSV = reader.splitCSV(csvFilePath);

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


        printTrans(splitTransToString(splitTrans), whereToPrint);
        return knownWords;
    } // convertToTrans ()

    /**
     * Make a String from a split trans file
     * @param splitTrans Split trans file
     * @return trans file
     */
    private static String splitTransToString(ArrayList<ArrayList<Integer>> splitTrans) {
        StringBuilder fileStr = new StringBuilder();
        for (ArrayList<Integer> line: splitTrans) {
            for (Integer word: line) {
                fileStr.append(word + " ");
            }
            fileStr.append('\n');
        }

        return fileStr.toString();
    } // splitTransToString ()

    private static void printTrans(String toPrint, String filename){
        try{
            PrintWriter transOutput = new PrintWriter(filename);
            transOutput.println(toPrint);
            transOutput.close();
        } catch(Exception e ){
            e.printStackTrace();
        }
    } // printTrans ()


}
