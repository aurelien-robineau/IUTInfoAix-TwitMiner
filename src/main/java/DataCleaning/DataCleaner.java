package DataCleaning;

import java.awt.peer.SystemTrayPeer;
import java.util.ArrayList;
import java.util.Arrays;

public class DataCleaner {

    public ArrayList<String[]> cleanCSV(ArrayList<String[]> data, ArrayList<String[]> dictionary) {
        // Put all words from dictionnary in one array
        ArrayList<String> unsignificantsWords = new ArrayList<String>();
        for(String[] line: dictionary) {
            for(String word: line) {
                unsignificantsWords.add(word);
            }
        }

        for(int i = 0; i < data.size(); i++) {
            ArrayList<String> arrayLine = new ArrayList<String>(Arrays.asList(data.get(i)));
            for(int j = 0; j < arrayLine.size(); j++) {

                // Remove double quotes from the word
                String word = arrayLine.get(j).replace("\"", "");
                arrayLine.set(j, word);

                if(unsignificantsWords.contains(word)) {
                    arrayLine.remove(j);
                    // Going back because one element has been deleted
                    --j;
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
