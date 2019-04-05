import FileManagement.CSVToTransConverter;
import com.opencsv.CSVWriter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class main {
    public static CSVWriter writer ;


    public static void main(String[] args) {
        
        getData("./src/main/java/csv/tweets.txt");
        CSVToTransConverter.convertToTrans("./src/main/java/csv/tweetsTrans.txt");

        try{
            Extracteur e =Extracteur.getInstance();
            e.readData(8);
        }catch (Exception e){
            e.printStackTrace();
        }







    }

    private static void getData(String fileToSaveIn){
        // The factory instance is re-useable and thread safe.
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("wRSxjdLMBYOsdl17krB3CKyPM")
                .setOAuthConsumerSecret("nhjAP9FpMuN3avvnnXoLdC6ROLF6neJvgY8o6Xh6AKcxogNoiN")
                .setOAuthAccessToken("1101101558769557504-TjLuUINadQmtneGa8LDeqNT19VBKGu")
                .setOAuthAccessTokenSecret("hbarwoRCYsmRKVmzWmv5hTjaoBdyemF7BGsAyxdnTlAQm");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        //initiliazing the writer
        try{
            writer = new CSVWriter(new FileWriter(fileToSaveIn),';');
        }catch(IOException exc){
            exc.printStackTrace();
        }

        try {


            // The factory instance is re-useable and thread safe.
            Query query = new Query("#datamining");
            QueryResult result = twitter.search(query);
            System.out.println();
            StringBuilder stringBuilder = new StringBuilder();
            Collection<String[]> resultat = new ArrayList<String[]>();
            //we print all the results in a file
            for (Status status : result.getTweets()) {
                stringBuilder.append(status.getCreatedAt().toString())
                        .append(";")
                        .append(status.getUser().getScreenName())
                        .append(";")
                        .append(status.getFavoriteCount())
                        .append(";")
                        .append(status.isRetweet())
                        .append(";")
                        .append(status.getText());
                resultat.add(stringBuilder.toString().split("[; ]"));

            }
            printStringToCsv(resultat);

        } catch (twitter4j.TwitterException exc) {
            exc.printStackTrace();
        }
    }


    private static void printStringToCsv(Collection<String[]> tweetCollection){

        try {
            for(String[] tweet : tweetCollection){
                System.out.println("print to csv");
                writer.writeNext(tweet);
            }
            writer.close();

        }catch(IOException exc){
            exc.printStackTrace();
        }
    }

}
