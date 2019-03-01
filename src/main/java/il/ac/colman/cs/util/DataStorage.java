package il.ac.colman.cs.util;

import com.amazonaws.services.s3.AmazonS3;
import il.ac.colman.cs.ExtractedLink;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
  public Connection conn;

  public DataStorage() throws SQLException {
    this.conn = getRemoteConnection();
  }

  /**
   * Add link to the database
   */
  public void addLink(ExtractedLink link) {
    /*
    This is where we'll add our link
     */
      if (link != null) {
          checkDB();
          try {
              Statement stmt = conn.createStatement();
              String sqlLinkInsert = "INSERT INTO tweetsDB(link,track,date,content,title,description,screenshotURL)"
                      + " values (" + "'" + link.getUrl() + "'" + "," + "'" + link.getTrack() + "'" + "," + "'" + link.getDate() + "'" + "," + "'" + link.getContent() + "'" + "," + "'" + link.getTitle() + "'" + "," + "'" + link.getDescription() + "'" + "," + "'" + link.getScreenshotURL() + "'" + ")";
              stmt.executeUpdate(sqlLinkInsert);
          } catch (SQLException e) {
              e.printStackTrace();
          }
      }
  }

  /**
   * Search for a link
   * @param query The query to search
   */
  public List<ExtractedLink> search(String query) {
    /*
    Search for query in the database and return the results
     */
    List<ExtractedLink> result = new LinkedList<ExtractedLink>();
    try{
      String sqlMainQuery = "SELECT * FROM tweetsDB";
      ResultSet queryResult;
      Statement statement = this.conn.createStatement();
      if (query != null) {
        queryResult = statement.executeQuery(sqlMainQuery + " WHERE track =" + "'" + query + "'");
      }else{
        queryResult = statement.executeQuery(sqlMainQuery);
      }
      while(queryResult.next()){
        ExtractedLink extractedLink = new ExtractedLink(
                queryResult.getString("link"),
                queryResult.getString("track"),
                queryResult.getString("date"),
                queryResult.getString("content"),
                queryResult.getString("title"),
                queryResult.getString("description"),
                queryResult.getString("screenshotURL")
        );
        result.add(extractedLink);
      }
      return result;
    }
    catch (SQLException error){
      error.printStackTrace();
    }

    return null;
  }

  /*
  GetRemoteConnection class creates the connection with all needed variables for reaching our
  mysql RDS in amazon.
  As explained in https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-RDS.html
   */
  private static Connection getRemoteConnection(){
    if (true){
        try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          String dbName = System.getProperty("RDS_DB_NAME");
          String userName = System.getProperty("RDS_USERNAME");
          String password = System.getProperty("RDS_PASSWORD");
          String hostname = System.getProperty("RDS_HOSTNAME");
          String port = System.getProperty("RDS_PORT");
          String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/"+dbName+"?user=" + userName + "&password=" + password;
          Connection con = DriverManager.getConnection(jdbcUrl);
          createTableInDB(con);
          return con;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e){
          e.printStackTrace();
        }
    }
    return null;
  }

  private static void createTableInDB(Connection con) throws SQLException {
      if (con != null){
        Statement statement = con.createStatement();
        String sqlCreate= "CREATE TABLE IF NOT EXISTS tweetsDB("
                + "id int NOT NULL AUTO_INCREMENT,"
                + "link TEXT,"
                + "track TEXT,"
                + "date TEXT,"
                + "content TEXT,"
                + "title TEXT,"
                + "description TEXT,"
                + "screenshotURL TEXT,"
                + " PRIMARY KEY (id));";

        statement.execute(sqlCreate);
      }
  }

    /*
     * Check if the database contain more than 1000 rows
     */
  private void checkDB(){
      ResultSet queryResults;
      int counter = 0;
      try{
          Statement statement = conn.createStatement();
          String query = "SELECT count(*) FROM tweetsDB";
          queryResults = statement.executeQuery(query);
          while (queryResults.next()){
              counter = queryResults.getInt("count(*)");
          }
          if (counter > 999){
              String oneScreenshotUrl = "";
              query = "SELECT * FROM tweetsDB ORDER BY id LIMIT 1";
              queryResults = statement.executeQuery(query);
              while (queryResults.next()){
                  oneScreenshotUrl = queryResults.getString("screenshotURL");
              }

              String[] onlyNameOfScreenShot = oneScreenshotUrl.split("https://s3.amazonaws.com/gon-and-israel-bucket/");
              AmazonS3 S3Client = AWSutil.getS3Client();
              S3Client.deleteObject("gon-and-israel-bucket",onlyNameOfScreenShot[1]);
              query = "DELETE FROM tweetsDB ORDER BY id LIMIT 1";
              statement.executeQuery(query);
          }
      }catch (SQLException e){
          e.printStackTrace();
      }

  }
}
