package customreport.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TableDataManager {

  private static List<TablePageRowObject> tablePageRowsDataList = new ArrayList<>();
  private static List<TableApiRowObject> tableApiRowsDataList = new ArrayList<>();
  private static final String pagesResultsTextPath =
      "customReports/pagesMeasurements/data/results.txt";
  private static final String apiResultsTextPath = "customReports/apiMeasurements/data/results.txt";

  @SneakyThrows
  public static void addPagesRowEntity(String testName, String elapsedTime) {
    File file = new File(pagesResultsTextPath);
    FileWriter fr = new FileWriter(file, true);
    BufferedWriter br = new BufferedWriter(fr);
    PrintWriter pr = new PrintWriter(br);
    pr.println(testName + "=" + elapsedTime + "/");
    pr.close();
    br.close();
    fr.close();
  }

  @SneakyThrows
  public static void addApiRowEntity(String testName, String elapsedTime, String maxTime) {
    File file = new File(apiResultsTextPath);
    FileWriter fr = new FileWriter(file, true);
    BufferedWriter br = new BufferedWriter(fr);
    PrintWriter pr = new PrintWriter(br);
    pr.println(testName + "=" + elapsedTime + ">" + maxTime + "<");
    pr.close();
    br.close();
    fr.close();
  }

  public static void convertPagesData() {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      File myObj = new File(pagesResultsTextPath);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        stringBuilder.append(data);
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      log.error("Unable to read results text file " + e.getStackTrace());
    }
    List<String> dataList = Arrays.asList(stringBuilder.toString().split("/"));
    for (String result : dataList) {
      int indexOfSeparation = result.indexOf("=");
      tablePageRowsDataList.add(
          TablePageRowObject.builder()
              .testName(result.substring(0, indexOfSeparation))
              .currentTime(result.substring(indexOfSeparation + 1))
              .build());
    }
  }

  public static void convertApiData() {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      File myObj = new File(apiResultsTextPath);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        stringBuilder.append(data);
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      log.error("Unable to read results text file " + e.getStackTrace());
    }
    List<String> dataList = Arrays.asList(stringBuilder.toString().split("<"));
    for (String result : dataList) {
      int indexStopName = result.indexOf("=");
      int indexStopExecutionTime = result.indexOf(">");
      tableApiRowsDataList.add(
          TableApiRowObject.builder()
              .testName(result.substring(0, indexStopName))
              .currentTime(result.substring(indexStopName + 1, indexStopExecutionTime))
              .maxTime(result.substring(indexStopExecutionTime + 1))
              .build());
    }
  }

  public static List<TablePageRowObject> getTablePageRowsDataList() {
    return tablePageRowsDataList;
  }

  public static String getPageRowsAsHtml() {
    StringBuilder htmlCode = new StringBuilder();
    String tableRowHtml =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td id=\"loadingTime\"> time-placeholder </td> </tr>";
    String tableRowHtmlWithWarning =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td> <font color=\"red\"> time-placeholder </font> </td> </tr>";
    for (TablePageRowObject tablePageRowObject : tablePageRowsDataList) {
      try {
        Double time = Double.parseDouble(tablePageRowObject.getCurrentTime());
        if (time < 10) {
          htmlCode.append(
              tableRowHtml
                  .replace("test-name-placeholder", tablePageRowObject.getTestName())
                  .replace("time-placeholder", tablePageRowObject.getCurrentTime()));
        } else {
          htmlCode.append(
              tableRowHtmlWithWarning
                  .replace("test-name-placeholder", tablePageRowObject.getTestName())
                  .replace("time-placeholder", tablePageRowObject.getCurrentTime()));
        }
      } catch (NumberFormatException e) {
        htmlCode.append(
            tableRowHtmlWithWarning
                .replace("test-name-placeholder", tablePageRowObject.getTestName())
                .replace("time-placeholder", tablePageRowObject.getCurrentTime()));
      }
    }
    return htmlCode.toString();
  }

  public static String getApiRowsAsHtml() {
    StringBuilder htmlCode = new StringBuilder();
    String tableRowHtml =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td id=\"loadingTime\"> time-placeholder </td> "
            + "    <td> maxTime-placeholder </td>"
            + "</tr>";
    String tableRowHtmlWithWarning =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td> <font color=\"red\"> time-placeholder </font> </td> "
            + "    <td> maxTime-placeholder </td>"
            + "</tr>";
    for (TableApiRowObject tableApiRowObject : tableApiRowsDataList) {
      try {
        if (tableApiRowObject.getCurrentTime().contains("FAILED")) {
          htmlCode.append(
              tableRowHtmlWithWarning
                  .replace("test-name-placeholder", tableApiRowObject.getTestName())
                  .replace("time-placeholder", tableApiRowObject.getCurrentTime())
                  .replace("maxTime-placeholder", tableApiRowObject.getMaxTime()));
        } else {
          htmlCode.append(
              tableRowHtml
                  .replace("test-name-placeholder", tableApiRowObject.getTestName())
                  .replace("time-placeholder", tableApiRowObject.getCurrentTime())
                  .replace("maxTime-placeholder", tableApiRowObject.getMaxTime()));
        }
      } catch (NumberFormatException e) {
        htmlCode.append(
            tableRowHtml
                .replace("test-name-placeholder", tableApiRowObject.getTestName())
                .replace("time-placeholder", tableApiRowObject.getCurrentTime()));
      }
    }
    return htmlCode.toString();
  }
}
