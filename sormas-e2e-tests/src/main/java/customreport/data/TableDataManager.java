package customreport.data;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TableDataManager {

  private static List<TableRowObject> tableRowsDataList = new ArrayList<>();

  public static void addRowEntity(String testName, String elapsedTime) {
    TableRowObject rowObject =
        TableRowObject.builder().testName(testName).currentTime(elapsedTime).build();
    tableRowsDataList.add(rowObject);
  }

  public static List<TableRowObject> getTableRowsDataList() {
    return tableRowsDataList;
  }

  public static String getTableRowsAsHtml() {
    StringBuilder htmlCode = new StringBuilder();
    String tableRowHtml =
        "<tr>\n"
            + "    <td> test-name-placeholder </td>\n"
            + "    <td id=\"loadingTime\"> time-placeholder <img src=\"images/warn.jpg\" id=\"warn\" style=\"width:20;height:20px;visibility:hidden\"> </td>\n"
            + "  </tr>";
    for (TableRowObject tableRowObject : tableRowsDataList) {
      htmlCode.append(
          tableRowHtml
              .replace("test-name-placeholder", tableRowObject.getTestName())
              .replace("time-placeholder", tableRowObject.getCurrentTime()));
    }
    return htmlCode.toString();
  }
}
