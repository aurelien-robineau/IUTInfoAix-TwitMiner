import com.opencsv.CSVWriter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class main {

    public static void main(String[] args) {
        // The factory instance is re-useable and thread safe.
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("wRSxjdLMBYOsdl17krB3CKyPM")
                .setOAuthConsumerSecret("nhjAP9FpMuN3avvnnXoLdC6ROLF6neJvgY8o6Xh6AKcxogNoiN")
                .setOAuthAccessToken("1101101558769557504-TjLuUINadQmtneGa8LDeqNT19VBKGu")
                .setOAuthAccessTokenSecret("hbarwoRCYsmRKVmzWmv5hTjaoBdyemF7BGsAyxdnTlAQm");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {


            // The factory instance is re-useable and thread safe.
            Query query = new Query("#datamining");
            QueryResult result = twitter.search(query);
            System.out.println();
            StringBuilder stringBuilder = new StringBuilder();
           // List<String> resultat = new
            for (Status status : result.getTweets()) { //we print his content next
                
                stringBuilder.append(status.getCreatedAt())
                        .append(";")
                        .append(status.getUser().getScreenName())
                        .append(";")
                        .append(status.getFavoriteCount())
                        .append(";")
                        .append(status.isRetweet())
                        .append(";")
                        .append(status.getText())
                        .append("\n");
                        
            }
            printStringToCsv(stringBuilder.toString());
        } catch (twitter4j.TwitterException exc) {
            exc.printStackTrace();
        }

    }

    private static void printStringToCsv(String data){
        try {
        CSVWriter writer = new CSVWriter(new FileWriter("/file.csv"));

        writer.writeAll();
        }catch(IOException exc){
            exc.printStackTrace();
        }

    }
}
