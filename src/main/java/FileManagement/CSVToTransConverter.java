package FileManagement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSVToTransConverter {

    /**
     * Convert a .csv file into a .trans file.
     * @param csvFilePath Path of the csv file to convert
     * @param transFilePath Path of the trans file to print
     * @return Association of numbers and there word
     */
    public  HashMap<Integer, String> convertToTrans(String csvFilePath, String transFilePath) {
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


        printTrans(splitTransToString(splitTrans), transFilePath);

        HashMap<Integer, String> numbersToWords = new HashMap<Integer, String>();
        for(Map.Entry<String, Integer> entry : knownWords.entrySet()){
            numbersToWords.put(entry.getValue(), entry.getKey());
        }

        return numbersToWords;
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
