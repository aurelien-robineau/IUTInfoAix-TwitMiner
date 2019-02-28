import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

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
            Query query = new Query("@random"); // we search for the user "random"
            QueryResult result = twitter.search(query);
            System.out.println();
            for (Status status : result.getTweets()) { //we print his content next
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
            }
        } catch (twitter4j.TwitterException exc) {
            exc.printStackTrace();
        }
    }
}
