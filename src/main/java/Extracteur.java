import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Extracteur {
    public static Map< HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float> confs =
            new HashMap<HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float>();

    /**
     * read data from the output of apriori as a csv
     * @param nbElements
     * @return
     * @throws IOException
     */
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


        Set<Set<Integer>> result = Sets.powerSet(Sets.newHashSet(data));
        for(Set<Integer> item : result){
            combinations.add(Lists.newArrayList(item));
        }
        //we don't want the last value because it equals data
        combinations.remove(combinations.size()-1);
        //we don't want the null value the Sets.powerSet gives us
        combinations.remove(0);

        return combinations;
    }

    /**
     *
     * @param motifs
     * @return
     */
    private static Map< HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float> conf(Map<ArrayList<Integer>,Float> motifs) {

        Map< HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float> confs = new HashMap<HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float>();

        ArrayList<Integer> biggestKey = new ArrayList<Integer>();
        for (Map.Entry <ArrayList<Integer>, Float> entry : motifs.entrySet()) {
            if(entry.getKey().size() > biggestKey.size()) {
                biggestKey = entry.getKey();
            }
        }

        for (Map.Entry <ArrayList<Integer>, Float> entry : motifs.entrySet()) {
            float conf = motifs.get(biggestKey) / entry.getValue();

            HashMap<ArrayList<Integer>, ArrayList<Integer>> key = new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
            key.put(entry.getKey(), biggestKey);
            confs.put(key, conf);
        }

        // AFFICHAGE
        for (Map.Entry <HashMap<ArrayList<Integer>, ArrayList<Integer>>, Float> entry : confs.entrySet()) {
            HashMap<ArrayList<Integer>, ArrayList<Integer>> key = entry.getKey();
            for (Map.Entry <ArrayList<Integer>, ArrayList<Integer>> assos: key.entrySet()) {
                System.out.print(assos.getKey());
                System.out.print(" -> ");
                System.out.print(assos.getValue());
            }
            System.out.print(" = ");
            System.out.println(entry.getValue());
        }

        return confs;
    }


}

