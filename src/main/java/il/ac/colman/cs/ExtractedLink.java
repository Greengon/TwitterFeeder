package il.ac.colman.cs;

public class ExtractedLink {
  private final String url;
  private final String track;
  private final String date;
  private final String content;
  private final String title;
  private final String description;
  private final String screenshotURL;

  public ExtractedLink(String url,String track,String date ,String content, String title, String description, String screenshotURL) {
    this.url = url;
    this.track = track;
    this.date = date;
    this.content = content;
    this.title = title;
    this.description = description;
    this.screenshotURL = screenshotURL;
  }

  public String getUrl() {
    return url;
  }

  public String getTrack(){
    return track;
  }

  public String getDate(){
    return date;
  }

  public String getContent() {
    return content;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getScreenshotURL() {
    return screenshotURL;
  }
}
