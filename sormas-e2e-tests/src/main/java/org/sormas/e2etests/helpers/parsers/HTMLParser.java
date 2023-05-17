package org.sormas.e2etests.helpers.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser {

  public static String getTextFromHtml(String htmlContent, String cssQuery) {
    return Jsoup.parse(htmlContent).select(cssQuery).get(0).text();
  }

  public static Element findElement(String htmlContent, String cssQuery) {
    return Jsoup.parse(htmlContent).select(cssQuery).get(0);
  }

  public static Elements findElements(String htmlContent, String cssQuery) {
    return Jsoup.parse(htmlContent).select(cssQuery);
  }
}
