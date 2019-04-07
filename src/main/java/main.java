import DataCleaning.DataCleaner;
import FileManagement.CSVReader;
import FileManagement.CSVToTransConverter;
import GUI.HomeController;
import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class main extends Application {

    public static HashMap<Integer, String> numberToWords = new HashMap<Integer, String>();
    private static final int    MAX_QUERIES	     = 100;
    private static final int    TWEETS_PER_QUERY = 100;
    private static final String SEARCH_TERM      = "Esport";
    private static CSVWriter    writer ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new HomeController());
        scene.getStylesheets().addAll("css/style.css");
        primaryStage.setTitle("TwitMiner");
        primaryStage.setScene(scene);
        primaryStage.show();
    } // start ()

    public static void main(String[] args) {

        Application.launch(args);

        String csvFilePath   = "./src/main/resources/CSV/" + SEARCH_TERM + ".csv";
        String transFilePath = "./src/main/resources/trans/" + SEARCH_TERM + ".trans";
        String outFilePath   = "./src/main/resources/out/" + SEARCH_TERM + ".out";

        /////// Undo comments if you want fresh data
        /*
        Collection<String[]> tweets = getData(csvFilePath);

        printStringToCsv(tweets);

        System.out.println("/!\\ Cleaning data ...");
        cleanData(csvFilePath, "./src/main/resources/dictionary.csv");

        System.out.println("/!\\ Creating trans file ...");
        CSVToTransConverter csvToTransConverter = new CSVToTransConverter();
        numberToWords = csvToTransConverter.convertToTrans(csvFilePath, transFilePath);
        System.out.println("/!\\ trans file created !");
        */
        /*
        try {
            Process process = new ProcessBuilder("./src/main/resources/apriori.exe", transFilePath, "2", outFilePath).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        */

        // !!! \\ Call apriori on trans here.

        /*
        try {
            Runtime.getRuntime().exec("apriori.exe", null, new File("."));
        } catch IOException e){
            e.printStackTrace();
        }

        try {
            Extracteur e = Extracteur.getInstance();
            //e.readData(8);
        } catch (Exception e){
            e.printStackTrace();
        }
        */
    } // main ()

    private static Collection<String[]> getData(String fileToSaveIn){
        // The factory instance is re-useable and thread safe.
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("wRSxjdLMBYOsdl17krB3CKyPM")
                .setOAuthConsumerSecret("nhjAP9FpMuN3avvnnXoLdC6ROLF6neJvgY8o6Xh6AKcxogNoiN")
                .setOAuthAccessToken("1101101558769557504-TjLuUINadQmtneGa8LDeqNT19VBKGu")
                .setOAuthAccessTokenSecret("hbarwoRCYsmRKVmzWmv5hTjaoBdyemF7BGsAyxdnTlAQm");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        long maxID = -1;
        int totalTweets =0;
        Collection<String[]> resultat = new ArrayList<String[]>();
        try {
            // Initialize csv writer
            writer = new CSVWriter(new FileWriter(fileToSaveIn),';');

            //	This returns all the various rate limits in effect for us with the Twitter API
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

            //	This finds the rate limit specifically for doing the search API call we use in this program
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

            for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {
                System.out.printf("/!\\ Starting loop %d\n", queryNumber);

                if (searchTweetsRateLimit.getRemaining() == 0) {

                    System.out.printf("/!\\ Sleeping for %d seconds due to rate limits\n", searchTweetsRateLimit.getSecondsUntilReset());

                    Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000l);
                }

                Query query = new Query(SEARCH_TERM);
                query.setCount(TWEETS_PER_QUERY);
                query.setLang("fr");

                if (maxID != -1){
                    query.setMaxId(maxID - 1);
                }

                /////// Fetch for tweets. Select wanted fields. ///////////
                QueryResult result = twitter.search(query);

                StringBuilder stringBuilder = new StringBuilder();

                // Print all the results in a file
                for (Status status : result.getTweets()) {
                    ++totalTweets;
                    if (maxID == -1 || status.getId() < maxID) {
                        maxID = status.getId();
                    }
                    stringBuilder.append(status.getCreatedAt().toString())
                            .append(";")
                            .append(status.getUser().getScreenName())
                            .append(";")
                            .append(status.getFavoriteCount())
                            .append(";")
                            .append(status.isRetweet())
                            .append(";")
                            .append(status.getText().replace("\n",""));
                    resultat.add(stringBuilder.toString().split("[; ]"));
                    stringBuilder.setLength(0);

                    searchTweetsRateLimit = result.getRateLimitStatus();
                }
                /////// End fetch and select ///////////
            }

            System.out.println("/!\\ " + totalTweets + " tweets lus.");
        } catch(Exception exc){
            exc.printStackTrace();
        }

        return resultat;
    } // getData ()

    /**
     * Clean the tweets
     * @param csvFilePath CSV containing the tweets
     * @param dictionaryFilePath CSV containing the words to delete
     */
    private static void cleanData(String csvFilePath, String dictionaryFilePath) {
        CSVReader reader = new CSVReader(";");
        ArrayList<String[]> csv = reader.splitCSV(csvFilePath);
        ArrayList<String[]> dictionary = reader.splitCSV(dictionaryFilePath);

        DataCleaner cleaner = new DataCleaner();
        csv = cleaner.cleanCSV(csv, dictionary);

        try {
            writer = new CSVWriter(new FileWriter(csvFilePath),';');
            printStringToCsv(csv);
        } catch(Exception exc){
            exc.printStackTrace();
        }
    } // cleanData ()

    private static void printStringToCsv(Collection<String[]> tweetCollection){
        try {
            for(String[] tweet : tweetCollection){
                writer.writeNext(tweet);
            }
            writer.close();
        }catch(IOException exc){
            exc.printStackTrace();
        }
    } // printStringToCSV

    private static void printTrans(String toPrint, String filename){
        try{
            PrintWriter transOutput = new PrintWriter(filename);
            transOutput.println(toPrint);
            transOutput.close();
        } catch(Exception e ){
            e.printStackTrace();
        }
    } // printTrans ()
}