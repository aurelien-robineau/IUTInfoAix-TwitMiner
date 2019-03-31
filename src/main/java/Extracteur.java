import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Extracteur {
    private Map<ArrayList<Integer>,Float> frequencies;



    public static Map<ArrayList<Integer>,Float> readData() throws IOException {

        int count = 0;
        String file = "./src/main/java/csv/test.txt";
        Map<ArrayList<Integer>,Float> content = new HashMap<ArrayList<Integer>,Float>();
        ArrayList<ArrayList<Integer>> pattern = new ArrayList<ArrayList<Integer>>();
        ArrayList<Float> frequencies = new ArrayList<Float>();


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
                frequencies.add( Float.parseFloat(stringContent[stringContent.length-1]) );
                pattern.add(temp);

            }
            for( int i=0; i<frequencies.size() ; ++i){
                content.put(pattern.get(i),frequencies.get(i)/nbLignes);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }









        return content;
    }

}

