package DataProcessing;

import FileManagement.OutToCSVConverter;
import Main.Pair;
import Main.main;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Extracteur {
    // properties
    private Float minConf =0F;
    private Float minfreq =0F;
    private int minLift =0;
    // eg : key{first : X , second : Y-X}, value( first: confiance, second: lift)
    private Map<Pair, Pair> patternConfLift = new HashMap<Pair, Pair>();
    private Map<ArrayList<Integer>,Float> freqPattern  = new HashMap<ArrayList<Integer>, Float>();
    private static Extracteur instance ;


    // getters an setters
    public void setMinConf(Float minConf) {
        this.minConf = minConf;
    }


    public void setMinLift(int minLift) {
        this.minLift = minLift;
    }

    public void setMinfreq(Float minfreq) {
        this.minfreq = minfreq;
    }

    // constructor
    private Extracteur() {}

    // singleton getter
    public static Extracteur getInstance(){
        if(instance == null){
            instance = new Extracteur();
        }
        return instance;
    }

    /**
     * read data from the output of apriori as a csv and analyze it with frequency, "confiance", and lift
     * @param nbElements
     * @return
     * @throws IOException
     */
    public void analyzePatterns(int nbElements, String file) throws IOException {

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
            if (entry.getKey().size() < 2 || entry.getValue() < minfreq ) continue;

            //we search patterns for interresting rules
            for (ArrayList<Integer> combination : combinationFinder(entry.getKey())) {
                try{

                    Float conf = entry.getValue()/freqPattern.get(combination);
                    if(conf >=minConf ){
                        Float lift = conf / entry.getValue();
                        if(lift >= minLift){
                            //pattern Y
                            ArrayList<String> y = new ArrayList<String>();
                            for(int i = 0; i<entry.getKey().size(); ++i){
                                y.add(i, main.numberToWords.get(entry.getKey().get(i)) );
                            }

                            //pattern x
                            ArrayList<String> x = new ArrayList<String>();
                            for(int i = 0; i< combination.size(); ++i){
                                System.out.println(combination.get(i));

                                x.add(i,main.numberToWords.get(combination.get(i)));
                            }

                            patternConfLift.put(new Pair(y, x), new Pair(conf,lift));
                        }

                    }
                }catch (NullPointerException e){
                    System.out.println("number does not match with anything");
                    e.printStackTrace();
                }
            }
        }
        //vérification du lift


    } // analyzePatterns ()

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
    } // combinationFinder

    /**
     * turn freqMotif into a HashMap<String[], Float> with the help of numberToWords wich is a variable telling
     * us what number correspond with wath word
     * @return freqMotif avec pour clé les mots à la place des int
     */
    public  HashMap<String[], Float> freqStringMotif(){

        HashMap<String[], Float> result = new HashMap<String[], Float>();
        //go over all the keys of
        for(Map.Entry<ArrayList<Integer>,Float> entry : freqPattern.entrySet()) {
            String[] key = new String[entry.getKey().size()];
            for(int i = 0 ; i<entry.getKey().size(); ++i){
                key[i] = main.numberToWords.get(entry.getKey().get(i));
            }
            result.put(key,entry.getValue());
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for(Map.Entry<Pair, Pair> entry : patternConfLift.entrySet() ) {





            try{

                ArrayList<String> x;
                x = (ArrayList<String>) entry.getKey().getSecond();

                s.append(x.toString() );
                s.append("-->");

                String yMinusX = entry.getKey().getFirst().toString();

                for( String unit : x){
                    yMinusX= yMinusX.replace(unit,"");
                }
                s.append(entry.getKey().getFirst().toString() );
                s.append(" confiance : ");
                s.append(entry.getValue().getFirst());
                s.append(" Lift : ");
                s.append(entry.getValue().getSecond());
                s.append('\n');

            }catch (ClassCastException e){
                System.out.println("cast exception");
                e.printStackTrace();
            }



        }

        return s.toString();
    } // toString ()

    public static void printToFile(String filename){
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(instance.toString());
        } catch(Exception e ){
            e.printStackTrace();
        }
    }
}

