package DataCleaning;

import java.util.ArrayList;
import java.util.Arrays;

public class DataCleaner {

    public ArrayList<String[]> cleanCSV(ArrayList<String[]> data, ArrayList<String[]> dictionary) {
        ArrayList<String> unsignificantsWords = new ArrayList<String>();
        for(String[] line: dictionary) {
            for(String word: line) {
                unsignificantsWords.add(word);
            }
        }

        for(int i = 0; i < data.size(); i++) {
            ArrayList<String> arrayLine = new ArrayList<String>(Arrays.asList(data.get(i)));
            for(String word: data.get(i)) {
                if(unsignificantsWords.contains(word)) {
                    arrayLine.remove(word);
                }
            }
            data.set(i, arrayLine.toArray(new String[0]));
        }

        return data;
    }

    public void selectBestRules(String rulesFilePath) {
        // a;r;b;freq;conf


    }
}
