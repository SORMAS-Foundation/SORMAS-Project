package customreport.reportbuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomReportBuilder {

  public static void generateReport(String rowsData) {
    try {
      String reportIn =
          new String(
              Files.readAllBytes(
                  Paths.get("./src/main/java/customreport/template/customReport.txt")));

      String reportPath = "customReports/customReport.html";
      Files.write(
          Paths.get(reportPath),
          reportIn.replace("$table_data_placeholder", rowsData).getBytes(),
          StandardOpenOption.CREATE);

    } catch (Exception e) {
      log.info("Error when writing Custom report file: " + e.getMessage());
    }
  }
}
