package il.ac.colman.cs;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.AWSutil;
import il.ac.colman.cs.util.DataStorage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SearchResultsServer extends AbstractHandler {
  public static void main(String[] args) throws Exception {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();

    // Start the http server on port 8080
    Server server = new Server(8080);
    ContextHandler context = new ContextHandler();
    context.setContextPath( "/results" );
    context.setHandler( new SearchResultsServer() );


    server.setHandler(context);
    server.start();
    server.join();
  }

  private DataStorage storage;

  SearchResultsServer() throws SQLException {
    storage = new DataStorage();
  }

  public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
    // Set the content type to JSON
    httpServletResponse.setContentType("application/json;charset=UTF-8");

    // Set the status to 200 OK
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);

    System.out.println("SearchResultsServer is up");

    AmazonCloudWatch cloudWatch = AWSutil.getCloudWatchClient();
    long startTime = System.nanoTime();
    // Build data from request
    List<ExtractedLink> results = storage.search(httpServletRequest.getParameter("query"));
    long endTime = (System.nanoTime() - startTime) / 1000000;
    AWSutil.takeCloudWatchData(cloudWatch,"APISearchTime","Time to search for query in DB","searchTime",endTime);


    // Notify that this request was handled
    request.setHandled(true);

    //  httpServletResponse.getWriter().println("<h1>Hello World</h1>");
    HashMap<String, List<ExtractedLink>> hm = new HashMap<String, List<ExtractedLink>>();
    hm.put("results", results);
    // Convert data to JSON string and write to output
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(httpServletResponse.getWriter(), hm);
  }

}
