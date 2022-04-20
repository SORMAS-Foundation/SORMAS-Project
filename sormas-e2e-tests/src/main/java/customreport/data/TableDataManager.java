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

  private static List<TableRowObject> tableRowsDataList = new ArrayList<>();
  private static final String resultsTextPath = "customReports/pagesMeasurements/data/results.txt";

  @SneakyThrows
  public static void addRowEntity(String testName, String elapsedTime) {
    File file = new File(resultsTextPath);
    FileWriter fr = new FileWriter(file, true);
    BufferedWriter br = new BufferedWriter(fr);
    PrintWriter pr = new PrintWriter(br);
    pr.println(testName + "=" + elapsedTime + "/");
    pr.close();
    br.close();
    fr.close();
  }

  public static void convertData() {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      File myObj = new File(resultsTextPath);
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
      tableRowsDataList.add(
          TableRowObject.builder()
              .testName(result.substring(0, indexOfSeparation))
              .currentTime(result.substring(indexOfSeparation + 1))
              .build());
    }
  }

  public static List<TableRowObject> getTableRowsDataList() {
    return tableRowsDataList;
  }

  public static String getTableRowsAsHtml() {
    StringBuilder htmlCode = new StringBuilder();
    String tableRowHtml =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td id=\"loadingTime\"> time-placeholder </td> </tr>";
    String tableRowHtmlWithWarning =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td> <font color=\"red\"> time-placeholder </font> </td> </tr>";
    for (TableRowObject tableRowObject : tableRowsDataList) {
      try {
        Double time = Double.parseDouble(tableRowObject.getCurrentTime());
        if (time < 10) {
          htmlCode.append(
              tableRowHtml
                  .replace("test-name-placeholder", tableRowObject.getTestName())
                  .replace("time-placeholder", tableRowObject.getCurrentTime()));
        } else {
          htmlCode.append(
              tableRowHtmlWithWarning
                  .replace("test-name-placeholder", tableRowObject.getTestName())
                  .replace("time-placeholder", tableRowObject.getCurrentTime()));
        }
      } catch (NumberFormatException e) {
        htmlCode.append(
            tableRowHtmlWithWarning
                .replace("test-name-placeholder", tableRowObject.getTestName())
                .replace("time-placeholder", tableRowObject.getCurrentTime()));
      }
    }
    return htmlCode.toString();
  }
}
