package il.ac.colman.cs.util;

import com.amazonaws.services.s3.AmazonS3;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ScreenshotGenerator {
  public static String takeScreenshot(String url) {
    UUID uuid = UUID.randomUUID();

    /* Windows soultion
    String directory = "C:\\Users\\green\\IdeaProjects\\TwitterFeeder\\src\\main\\resources\\NightmareScreenshot\\";
    String screenshotFilePath = "C:\\Users\\green\\IdeaProjects\\TwitterFeeder\\" + uuid.toString() + ".png";
    */
    String screenshotFilePath = uuid.toString() + ".png";
    AmazonS3 s3 = AWSutil.getS3Client();
    /*
    Run our screenshot generator program
     */
    try {
      String command = "xvfb-run --server-args=\"-screen 0 1024x768x24\" wkhtmltoimage --format png --crop-w 1024 --crop-h 768 --quiet --quality 60 " + url + " " + uuid.toString() + ".png";
      Process myProcess = Runtime.getRuntime().exec(new String[] {"bash", "-c", command});
      myProcess.waitFor();

      /*
      Nightmere solution
      Process myProcess = Runtime.getRuntime().exec(
              "node " +
              directory +
              "screenshot.js " +
              url +
              " " +
              uuid.toString() +
              ".png"
      );

      myProcess.waitFor();

      */
      File file = new File(screenshotFilePath);
      if (file.exists()){
        s3.putObject("gon-and-israel-bucket",uuid.toString() + ".png",file);
        file.delete();
        return "https://s3.amazonaws.com/gon-and-israel-bucket/" + uuid.toString() + ".png";
      }
      file.delete();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("screenshot attempt timed out");
    return "screenshot attempt timed out";
  }
}

