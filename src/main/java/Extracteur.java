import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Extracteur {
    private Map<ArrayList<Integer>,Float> frequencies;



    public static Map<ArrayList<Integer>,Float> readData() throws IOException {

        String file = "./src/main/java/csv/test.txt";
        Map<ArrayList<Integer>,Float> content = new HashMap<ArrayList<Integer>,Float>();


        String[] stringContent;
        try{

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            Integer nbLignes = 0;
            while ((line = br.readLine()) != null) {
                ++nbLignes;
                ArrayList<Integer> temp = new ArrayList<Integer>();
                stringContent = line.split(";");
                for(int i =0; i<stringContent.length-1; ++i){
                    temp.add( Integer.parseInt(stringContent[i]) );
                }
                content.put(temp,Float.parseFloat(stringContent[stringContent.length-1]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return content;
    }

}

