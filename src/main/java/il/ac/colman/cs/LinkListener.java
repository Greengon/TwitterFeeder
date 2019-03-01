package il.ac.colman.cs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import il.ac.colman.cs.util.AWSutil;
import il.ac.colman.cs.util.DataStorage;
import il.ac.colman.cs.util.LinkExtractor;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LinkListener {
  public static void main(String[] args) throws IOException,SQLException, InterruptedException, JSONException {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();

    // Initiate our link extractor
    LinkExtractor linkExtractor = new LinkExtractor();

    // Listen to SQS for arriving links
    AmazonSQS listenerSQSClient = AWSutil.getSQSClient();

    System.out.println("LinkListener is up");

    ReceiveMessageRequest request = new ReceiveMessageRequest(System.getProperty("config.sqs.url"));

    while (true) {
      // Extract the link content
      ReceiveMessageResult result = listenerSQSClient.receiveMessage(request);
      if (result.getMessages().size() == 0)
        Thread.sleep(10000);
      else {
          for(Message message: result.getMessages()){
            final JSONObject json = new JSONObject(message.getBody());
            dataStorage.addLink(linkExtractor.extractContent(json.getString("url"),json.getString("track")));
            listenerSQSClient.deleteMessage(System.getProperty("config.sqs.url"),message.getReceiptHandle());
          }
      }
    }


  }
}
