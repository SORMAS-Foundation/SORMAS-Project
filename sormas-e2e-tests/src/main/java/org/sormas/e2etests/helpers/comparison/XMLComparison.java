package org.sormas.e2etests.helpers.comparison;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLComparison {

  public static List<String> extractDiffNodes(List<String> allDiffs, String regex) {
    Pattern pattern = Pattern.compile(regex);
    List<String> nodesList = new ArrayList<>();

    for (int i = 0; i < allDiffs.size(); i++) {
      Matcher matcher = pattern.matcher(allDiffs.get(i));
      while (matcher.find()) {
        if (!nodesList.contains(matcher.group())) {
          nodesList.add(matcher.group());
        }
      }
    }
    return nodesList;
  }

  @SneakyThrows
  public static List<String> compareXMLFiles(String controlFilePath, String testedFilePath) {

    String strControlXml =
        FileUtils.readFileToString(new File(controlFilePath), StandardCharsets.UTF_8);
    String strTestedXml =
        FileUtils.readFileToString(new File(testedFilePath), StandardCharsets.UTF_8);

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);

    Diff myDiff = null;
    DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();

    try {
      myDiff = new Diff(strControlXml, strTestedXml);
      myDiff.overrideDifferenceListener(myDifferenceListener);
    } catch (SAXException | IOException e) {
      e.printStackTrace();
    }

    DetailedDiff myDiffDetailed = new DetailedDiff(myDiff);
    List allDifferences = myDiffDetailed.getAllDifferences();

    List<String> allDiffsArray = new ArrayList<>();
    for (int i = 0; i < allDifferences.size(); i++) {
      allDiffsArray.add(allDifferences.get(i).toString());
    }
    return allDiffsArray;
  }
}
