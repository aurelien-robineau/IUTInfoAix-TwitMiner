package Main;

import DataCleaning.DataCleaner;
import DataProcessing.Apriori;
import DataProcessing.Extracteur;
import FileManagement.CSVReader;
import FileManagement.CSVToTransConverter;
import GUI.HomeController;
import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class main extends Application {

    public static HashMap<Integer, String> numberToWords = new HashMap<Integer, String>();

    private static final String transFilePath    = "./src/main/resources/trans/";
    private static final String csvFilePath      = "./src/main/resources/CSV/";
    private static final String aprioriFilePath  = "src/main/resources/aprioriOut/";

    private static int          numberOfTweets =0;
    private static int          MAX_QUERIES;
    private static int          TWEETS_PER_QUERY;
    private static String       SEARCH_TERM;

    private static CSVWriter   writer ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new HomeController());
        scene.getStylesheets().addAll("css/style.css");
        primaryStage.setTitle("TwitMiner");
        primaryStage.setScene(scene);
        primaryStage.show();
    } // start ()

    public static void main(String[] args) {
        //Application.launch(args);
    } // main ()

    public static void initializeParams(String searchTerm, int numberOfTweetsMax) {
        SEARCH_TERM = searchTerm;

        if(numberOfTweetsMax >= 100) {
            MAX_QUERIES = numberOfTweetsMax / 100;
            TWEETS_PER_QUERY = 100;
        } else {
            MAX_QUERIES = 1;
            TWEETS_PER_QUERY = numberOfTweetsMax;
        }
    } // initializeParams ()

    /*
    public static void getFreshData(String searchTerm, int numberOfTweetsMax){
        SEARCH_TERM = searchTerm;

        if(numberOfTweetsMax >= 100) {
            MAX_QUERIES = numberOfTweetsMax / 100;
            TWEETS_PER_QUERY = 100;
        } else {
            MAX_QUERIES = 1;
            TWEETS_PER_QUERY = numberOfTweetsMax;
        }

        Collection<String[]> tweets = getData(csvFilePath + SEARCH_TERM + ".csv");

        printStringToCsv(tweets);

        System.out.println("/!\\ Cleaning data ...");
        cleanData(csvFilePath+SEARCH_TERM +".csv", "./src/main/resources/dictionary.csv");

        System.out.println("/!\\ Creating trans file ...");
        CSVToTransConverter csvToTransConverter = new CSVToTransConverter();
        numberToWords = csvToTransConverter.convertToTrans(csvFilePath + SEARCH_TERM +".csv",
                transFilePath + SEARCH_TERM +".trans");
        try {
            Apriori.run(transFilePath + SEARCH_TERM + ".trans",0.6F,  aprioriFilePath + SEARCH_TERM + ".out");
        } catch(Exception e){
            e.printStackTrace();
        }
    } // getFreshData ()
    */

    public static Collection<String[]> getData(){
        String fileToSaveIn = csvFilePath + SEARCH_TERM + ".csv";

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

        Collection<String[]> resultat = new ArrayList<String[]>();
        try {
            // Initialize csv writer
            writer = new CSVWriter(new FileWriter(fileToSaveIn),';');

            // This returns all the various rate limits in effect for us with the Twitter API
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

            // This finds the rate limit specifically for doing the search API call we use in this program
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

            for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {

                if (searchTweetsRateLimit.getRemaining() == 0) {
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
                    ++numberOfTweets;
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

            System.out.println("/!\\ " + numberOfTweets + " tweets lus.");
        } catch(Exception exc){
            exc.printStackTrace();
        }

        return resultat;
    } // getData ()

    /**
     * Clean the tweets
     * @param dictionaryFilePath CSV containing the words to delete
     */
    public static void cleanData(String dictionaryFilePath) {
        String csvFile = csvFilePath + SEARCH_TERM + ".csv";

        CSVReader reader = new CSVReader(";");
        ArrayList<String[]> csv = reader.splitCSV(csvFile);
        ArrayList<String[]> dictionary = reader.splitCSV(dictionaryFilePath);

        DataCleaner cleaner = new DataCleaner();
        csv = cleaner.cleanCSV(csv, dictionary);

        try {
            writer = new CSVWriter(new FileWriter(csvFile),';');
            printStringToCsv(csv);
        } catch(Exception exc){
            exc.printStackTrace();
        }
    } // cleanData ()

    public static void printStringToCsv(Collection<String[]> tweetCollection){
        try {
            for(String[] tweet : tweetCollection){
                writer.writeNext(tweet);
            }
            writer.close();
        }catch(IOException exc){
            exc.printStackTrace();
        }
    } // printStringToCSV

    public static void createTransFile() {
        CSVToTransConverter csvToTransConverter = new CSVToTransConverter();
        numberToWords = csvToTransConverter.convertToTrans(csvFilePath + SEARCH_TERM + ".csv",
                transFilePath + SEARCH_TERM +".trans");
        try{
            FileOutputStream fos =
                    new FileOutputStream("src/main/resources/serialized/"+SEARCH_TERM+"numberToWords");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(numberToWords);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    } // createTransFile

    public static void unserializeNumberToWords(){
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/serialized/"+SEARCH_TERM+"numberToWords");
            ObjectInputStream ois = new ObjectInputStream(fis);
            numberToWords = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }



    public static void runApriori() {
        try {
            Apriori.run(main.transFilePath + SEARCH_TERM + ".trans",0.6F,  main.aprioriFilePath + SEARCH_TERM + ".out");
        } catch(Exception e){
            e.printStackTrace();
        }
    } // runApriori ()
}