package il.ac.colman.cs.util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import il.ac.colman.cs.ExtractedLink;
import org.joda.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Extract content from links
 */
public class LinkExtractor {
  Document doc;
  ScreenshotGenerator screenshotGenerator;

  public LinkExtractor(){
    this.doc = null;
    this.screenshotGenerator = new ScreenshotGenerator();
  }

  public ExtractedLink extractContent(String url,String track) throws IOException {
    /*
    Use JSoup to extract the text, title and description from the URL.

    Extract the page's content, without the HTML tags.
    Extract the title from title tag or meta tags, prefer the meta title tags.
    Extract the description the same as you would the title.

    For title and description tags, if there are multiple (which is usually the case)
    take the first.
     */

    try {
      AmazonCloudWatch cloudWatch = AWSutil.getCloudWatchClient();
      long startTime = System.nanoTime();
      System.out.println(url);
      this.doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
      if (this.doc != null){
        System.out.println("Fetching url");
        String fullUrL= this.doc.select("a").first().absUrl("abs:href");
        System.out.println("Url:" + fullUrL);
        fullUrL =  (fullUrL == "" || fullUrL == null) ? "No url found" : fullUrL;
        System.out.println("Fetching title");
        String title = getTag("title");
        title =  (title == null) ? "No title found": title.replaceAll("\\'","");
        System.out.println("title:" + title);
        System.out.println("Fetching description");
        String description = getTag("description");
        description =  (description == null) ? "No description found" : description.replaceAll("\\'","");
        System.out.println("description:" + description);
        String date = LocalDateTime.now().toString();
        System.out.println("date:" + date);
        System.out.println("Fetching content");
        String content = this.getContent();
        System.out.println("Content:" + content);
        long endTime = (System.nanoTime() - startTime) / 1000000;
        AWSutil.takeCloudWatchData(cloudWatch,"LinkExtractor","Time to extract link","LinkExtractor",endTime);
        startTime = System.nanoTime();
        System.out.println("Fetching screenshot");
        String screenShotUrl = this.screenshotGenerator.takeScreenshot(fullUrL);
        System.out.println("ScreenshotURL:" + screenShotUrl);
        endTime = (System.nanoTime() - startTime) / 1000000;
        AWSutil.takeCloudWatchData(cloudWatch,"screenshotGenerator","Time To Take ScreenShot","screenshotGenerator",endTime);

        // Testing
        // testing

        ExtractedLink newData = new ExtractedLink(fullUrL,track,date,content,title,description,screenShotUrl);

        return newData;
      }
      System.out.println("Failed to connect to website");
    } catch (IOException e){
      System.out.println("404 return error on given url");
    } catch (NullPointerException e){
      System.out.println("Jsoup failed to connect to url, NullPointerException");
    }
    return null;
  }

  String getContent(){
    String textToReturn = null;
   Elements bodyTags = this.doc.getElementsByTag("body");
   if (bodyTags != null){
     for (Element element: bodyTags)
       textToReturn = element.wholeText();
   }
   if (textToReturn != null){
      textToReturn = textToReturn
             .replaceAll("\\r", "")
             .replaceAll("\\n", "")
             .replaceAll("\\r\\n", "")
              .replaceAll("\\'", "")
              .replaceAll("\\t", "");
      if (textToReturn.length() > 100)
        textToReturn.substring(0,100);
      return textToReturn;
   }
    return "No content could be retrieve";
  }

  // http://zetcode.com/java/jsoup/
  // https://moz.com/blog/meta-data-templates-123
  // https://developer.twitter.com/en/docs/tweets/optimize-with-cards/guides/getting-started.html
  // https://developers.facebook.com/docs/sharing/webmasters/

  String getTag(String tag) {
    Elements elements = doc.select("meta[name=" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    elements = doc.select("meta[property=" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    elements = doc.select("meta[name=twitter:" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    elements = doc.select("meta[property=og:" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    elements = doc.select("meta[property=article:" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    elements = doc.select("meta[itemprop=" + tag + "]");
    for (Element element : elements) {
      if (element.attr("content")!= null) return element.attr("content");
    }
    return null;
  }

}

