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
    private static final int MAX_QUERIES			= 100;
    private static final int TWEETS_PER_QUERY		= 100;
    private static final String SEARCH_TERM			= "canard";
    private static CSVWriter writer ;


    public static void main(String[] args) {
        String storingFile ="./src/main/java/csv/tweets.txt";
        getData(storingFile);
        //System.out.println(CSVToTransConverter.convertToTrans(storingFile));
        //call apriori here


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


        long maxID = -1;
        int totalTweets =0;
        Collection<String[]> resultat = new ArrayList<String[]>();
        try {
            //initiliazing the writer
            writer = new CSVWriter(new FileWriter(fileToSaveIn),';');

            //	This returns all the various rate limits in effect for us with the Twitter API
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

            //	This finds the rate limit specifically for doing the search API call we use in this program
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");



            // The factory instance is re-useable and thread safe.


            for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {
                System.out.printf("\n\n!!! Starting loop %d\n\n", queryNumber);


                if (searchTweetsRateLimit.getRemaining() == 0) {
                    //	Yes we do, unfortunately ...
                    System.out.printf("!!! Sleeping for %d seconds due to rate limits\n", searchTweetsRateLimit.getSecondsUntilReset());

                    Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000l);
                }

                Query query = new Query(SEARCH_TERM);
                query.setCount(TWEETS_PER_QUERY);
                query.setLang("fr");

                if (maxID != -1){
                    query.setMaxId(maxID - 1);
                }


                /////// recherche et traitement résultat ///////////
                QueryResult result = twitter.search(query);

                StringBuilder stringBuilder = new StringBuilder();


                //we print all the results in a file
                for (Status status : result.getTweets()) {
                    ++totalTweets;
                    if (maxID == -1 || status.getId() < maxID)
                    {
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
                            .append(status.getText());
                    resultat.add(stringBuilder.toString().split("[; ]"));


                    searchTweetsRateLimit = result.getRateLimitStatus();
                }



                /////// fin recherche et traitement résultat ///////////
            }
            System.out.println("j'ai lu "+ totalTweets +" tweets");
            printStringToCsv(resultat);
        } catch(Exception exc){
            exc.printStackTrace();
        }

    }



    private static void printStringToCsv(Collection<String[]> tweetCollection){

        try {
            for(String[] tweet : tweetCollection){
                writer.writeNext(tweet);
            }
            writer.close();
        }catch(IOException exc){
            exc.printStackTrace();
        }
    }

}
