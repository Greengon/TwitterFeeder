package il.ac.colman.cs;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import il.ac.colman.cs.util.AWSutil;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterListener {
  public static void main(String[] args) {
    // Create our twitter configuration
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
        .setOAuthConsumerKey(System.getProperty("config.twitter.consumer.key"))
        .setOAuthConsumerSecret(System.getProperty("config.twitter.consumer.secret"))
        .setOAuthAccessToken(System.getProperty("config.twitter.access.token"))
        .setOAuthAccessTokenSecret(System.getProperty("config.twitter.access.secret"));


    // Create our Twitter stream
    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
    TwitterStream twitterStream = tf.getInstance();
    System.out.println("TwitterListenr is up");


    /*
      This is where we should start fetching the tweets using the Streaming API
      See Example 9 on this page: http://twitter4j.org/en/code-examples.html#streaming
    */
    StatusListener listener = new StatusListener() {
      AmazonSQS listenerSQSClient = AWSutil.getSQSClient();
      AmazonCloudWatch cloudWatch = AWSutil.getCloudWatchClient();

      public void onStatus(Status status) {
        if (status.getText().contains("http")){
            // Testing
            System.out.println(status.getUser().getName() + " : " + status.getText());  // For testing
            //testing

          URLEntity[] urlArray = status.getURLEntities();
          for (URLEntity url : urlArray) {
              try {
                  JSONObject json = new JSONObject();
                  json.put("url",url.getURL());
                  json.put("track",System.getProperty("config.twitter.track"));

              SendMessageRequest singleTweetMessage = new SendMessageRequest(
                      System.getProperty("config.sqs.url"),
                      json.toString()
              );
              listenerSQSClient.sendMessage(singleTweetMessage);
              AWSutil.takeCloudWatchData(cloudWatch,"Stream",System.getProperty("config.twitter.track"),"TweeterStream",1.0);
               } catch (JSONException e){e.getStackTrace();}
          }

        }
      }

      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {} // Unneeded to implement

      public void onTrackLimitationNotice(int i) {} // Unneeded to implement

      public void onScrubGeo(long l, long l1) {}  // Unneeded to implement

      public void onStallWarning(StallWarning stallWarning) {}  // Unneeded to implement

      public void onException(Exception e) {e.printStackTrace();}
    };


    twitterStream.addListener(listener);
    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
    twitterStream.filter(System.getProperty("config.twitter.track"));

  }
}
