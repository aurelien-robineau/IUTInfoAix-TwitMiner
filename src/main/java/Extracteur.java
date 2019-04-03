import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        //Pour phase
        for(int iterationNb =0;iterationNb<data.size(); ++iterationNb){

            // pour chaque item
            for( int itemNb =0; itemNb<data.size()-iterationNb; ++itemNb) {
                ArrayList<Integer>  sousCombinaison = new  ArrayList<Integer>();

                //préfixe
                for(int i =itemNb; i<iterationNb+1; ++i) {
                    sousCombinaison.add(data.get(i));

                }
                System.out.println(sousCombinaison);
                combinations.add(sousCombinaison);


                /*

                // jusqu'à nb item par combinaison
                for( int i =0; i< iterationNb+1; ++i){



                    ArrayList<Integer>  sousCombinaison = new  ArrayList<Integer>();
                    sousCombinaison.add(data.get(itemNb));
                    //pour chaque item suivant
                    for(int j= itemNb+1; j<data.size(); ++j){
                        sousCombinaison.add(data.get(j));
                        System.out.println(sousCombinaison);

                    }
                    combinations.add(sousCombinaison);



                }
*/
            }

        }






        return combinations;
    }



}

