import com.google.common.collect.Sets;

import java.io.*;
import java.util.*;

public class Extracteur {



    public static Map<ArrayList<Integer>,Float> readData(int nbElements) throws IOException {

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
                    if(!stringContent[i].equals("")){
                        temp.add( Integer.parseInt(stringContent[i]) );
                    }else{
                        temp.add( 0);
                    }

                }
                content.put(temp,Float.parseFloat(stringContent[stringContent.length-1]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //for each element in the content map
        for(Map.Entry<ArrayList<Integer>,Float> entry : content.entrySet()) {
            content.put(entry.getKey(),entry.getValue()/nbElements);

        }

        return content;
    }

    public void patternFinder(Map<ArrayList<Integer>,Float> data){

        for(Map.Entry<ArrayList<Integer>,Float>  row: data.entrySet()) {
            row.getKey();
            row.getValue();


        }



    }

    /**
     * find all combinations with the given numbers
     * @param data
     * @return the combinations
     */
    public static ArrayList<ArrayList<Integer>> combinationFinder(ArrayList<Integer> data){

        ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
        Set<Set<Integer>> result = Sets.powerSet(Sets.newHashSet(1,2,3));
        for(Set<Integer> token : result){
                ArrayList<Integer> mainList = new ArrayList<Integer>();
                mainList.addAll(token);
                combinations.add(mainList);
        }
        combinations.remove(0);






        return combinations;
    }



}

