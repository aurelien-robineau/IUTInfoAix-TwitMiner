import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Extracteur {
    // properties
    private Map< Pair, Float> confs =new HashMap<Pair, Float>();
    private Float minConf =0F;
    private Map<ArrayList<Integer>,Float> freqPattern  = new HashMap<ArrayList<Integer>, Float>();
    private static Extracteur instance ;

    // etters an setters
    public Float getMinConf() {
        return minConf;
    }

    public void setMinConf(Float minConf) {
        this.minConf = minConf;
    }

    // constructor
    private Extracteur() {}

    // singleton getter
    public static   Extracteur getInstance(){
        if(instance == null){
            return new Extracteur();
        }else{
            return instance;
        }
    }

    /**
     * read data from the output of apriori as a csv
     * @param nbElements
     * @return
     * @throws IOException
     */
    public void readData(int nbElements, String file) throws IOException {
        String[] stringContent;

        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                stringContent = line.split(";");
                for(int i =0; i<stringContent.length-1; ++i){
                    if(!stringContent[i].equals("")){
                        temp.add( Integer.parseInt(stringContent[i]) );
                    }else{
                        temp.add( 0);
                    }

                }
                freqPattern.put(temp,Float.parseFloat(stringContent[stringContent.length-1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // for each element in the content map
        for(Map.Entry<ArrayList<Integer>,Float> entry : freqPattern.entrySet()) {
            freqPattern.put(entry.getKey(),entry.getValue()/nbElements);
        }

        // creating rules
        for(Map.Entry<ArrayList<Integer>,Float> entry : freqPattern.entrySet()) {
            //if there can't be any subunit it's not interresting
            if (entry.getKey().size() < 1) continue;

            //we search patterns for interresting rules
            for (ArrayList<Integer> combination : combinationFinder(entry.getKey())) {
                if(freqPattern.get(combination) != null){
                    Float conf = entry.getValue()/freqPattern.get(combination);

                    if(conf >=minConf){
                        confs.put(new Pair(entry.getKey(),combination),conf);
                    }
                }else{
                    System.out.println(combination +" does not exist");
                }
            }
        }
    } // readData ()

    /**
     * find all combinations with the given numbers
     * @param data
     * @return the combinations
     */
    private ArrayList<ArrayList<Integer>> combinationFinder(ArrayList<Integer> data){
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
    } // combinationFinder ()

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("conf :");
        s.append(confs);
        s.append("\n\n freqPatterns :");
        s.append(freqPattern);
        s.append("\n\nmin conf :");
        s.append(minConf);

        return s.toString();
    } // toString ()

    /*
    /**
     * remplie conf
     * @param motifs
     * @return
     */
    /*
    private static Map< Pair<ArrayList<Integer>, ArrayList<Integer>>, Float> conf(Map<ArrayList<Integer>,Float> motifs) {

        Pair<ArrayList<Integer>, ArrayList<Integer>> test ;
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
*/
}

