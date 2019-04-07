package Main;

import DataCleaning.DataCleaner;
import DataProcessing.Apriori;
import DataProcessing.Extracteur;
import FileManagement.CSVReader;
import FileManagement.CSVToTransConverter;
import FileManagement.OutToCSVConverter;
import GUI.HomeController;
import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class main extends Application {

    public static HashMap<Integer, String> numberToWords = new HashMap<Integer, String>();

    public static final String transFilePath    = "./src/main/resources/trans/";
    public static final String csvFilePath      = "./src/main/resources/CSV/";
    public static final String aprioriFilePath  = "src/main/resources/aprioriOut/";
    public static final String outCSV           = "src/main/resources/outCSV/";
    public static final String patternFile      = "src/main/resources/patterns/";


    private static int   numberOfTweets =0;
    private static int   MAX_QUERIES;
    private static int   TWEETS_PER_QUERY;
    public static String search_Term;

    private static CSVWriter writer ;

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
    } // main ()

    public static void mine(String query, int nbTweets) {
        System.out.println("/!\\ Initializing parameters ...");
        main.initializeParams(query, nbTweets);

        System.out.println("/!\\ Getting data ...");
        Collection<String[]> tweets = main.getData();

        main.printStringToCsv(tweets);

        System.out.println("/!\\ Cleaning data ...");
        main.cleanData("./src/main/resources/dictionary.csv");

        main.createTransFile();

        System.out.println("/!\\ Run apriori ...");
        main.runApriori();

        System.out.println("/!\\ out -> csv ...");
        try{
            FileWriter writer = new FileWriter(outCSV+ search_Term);
            writer.append( OutToCSVConverter.convertToCSV("src/main/resources/aprioriOut/"+ search_Term +".out") );
            writer.close();
        }catch (IOException e ){
            e.printStackTrace();
        }


    } // mine ()




    public static void processData(int minfLift, Float minFreq, Float minConf, String searchTerm){
        int numberOfTweets = 0;
        try {
            numberOfTweets = LineCounter.countLines(transFilePath + searchTerm + ".trans");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(numberToWords == null){
            unserializeNumberToWords();
        }
        Extracteur extracteur = Extracteur.getInstance();
        extracteur.setMinLift(minfLift);
        extracteur.setMinfreq(minFreq);
        extracteur.setMinConf(minConf);
        try{
            extracteur.analyzePatterns(numberOfTweets,outCSV + searchTerm);

        }catch (IOException e){
            e.printStackTrace();
        }
        Extracteur.printToFile(patternFile+ search_Term +".txt");

    }//processData ()

    private static void initializeParams(String searchTerm, int numberOfTweetsMax) {
        search_Term = searchTerm;

        if(numberOfTweetsMax >= 100) {
            MAX_QUERIES = numberOfTweetsMax / 100;
            TWEETS_PER_QUERY = 100;
        } else {
            MAX_QUERIES = 1;
            TWEETS_PER_QUERY = numberOfTweetsMax;
        }
    } // initializeParams ()

    private static Collection<String[]> getData(){
        String fileToSaveIn = csvFilePath + search_Term + ".csv";

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

                Query query = new Query(search_Term);
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
    private static void cleanData(String dictionaryFilePath) {
        String csvFile = csvFilePath + search_Term + ".csv";

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

    private static void createTransFile() {
        CSVToTransConverter csvToTransConverter = new CSVToTransConverter();
        numberToWords = csvToTransConverter.convertToTrans(csvFilePath + search_Term + ".csv",
                transFilePath + search_Term +".trans");
        try{
            FileOutputStream fos =
                    new FileOutputStream("src/main/resources/serialized/" + search_Term + "_numberToWords.ser");
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
            FileInputStream fis = new FileInputStream("src/main/resources/serialized/" + search_Term + "_numberToWords.ser");
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

    private static void runApriori() {
        try {
            Apriori.run(main.transFilePath + search_Term + ".trans",0.3F,  main.aprioriFilePath + search_Term + ".out");
        } catch(Exception e){
            e.printStackTrace();
        }
    } // runApriori ()


}