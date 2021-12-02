package customreport.reportbuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomReportBuilder {

  public static final String pathToHtmlTemplate =
      "./src/main/java/customreport/template/customReport.txt";
  public static final String exportPath = "customReports/customReport.html";
  public static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd-MMM-yyy hh:mm a");

  public static void generateReport(String rowsData) {
    try {
      String reportIn = new String(Files.readAllBytes(Paths.get(pathToHtmlTemplate)));
      Files.write(
          Paths.get(exportPath),
          reportIn
              .replace("$table_data_placeholder", rowsData)
              .replace("$Date_text", "Created on: " + LocalDateTime.now().format(formatter))
              .getBytes(),
          StandardOpenOption.CREATE);

    } catch (Exception e) {
      log.info("Error when writing Custom report file: " + e.getStackTrace());
    }
  }
}
