package customreport.reportbuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomReportBuilder {

  public static final String pathToPagesReportHtmlTemplate =
      "./src/main/java/customreport/template/customReportPages.txt";
  public static final String pathToApiReportHtmlTemplate =
      "./src/main/java/customreport/template/customReportApi.txt";
  public static final String pathToExportPagesReport =
      "customReports/pagesMeasurements/customReport.html";
  public static final String pathToExportApiReport =
      "customReports/apiMeasurements/customReport.html";
  public static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd-MMM-yyy hh:mm a");

  public static void generatePagesMeasurementsReport(String rowsData) {
    try {
      String reportIn = new String(Files.readAllBytes(Paths.get(pathToPagesReportHtmlTemplate)));
      Files.write(
          Paths.get(pathToExportPagesReport),
          reportIn
              .replace("$table_data_placeholder", rowsData)
              .replace("$Date_text", "Created on: " + LocalDateTime.now().format(formatter))
              .getBytes(),
          StandardOpenOption.CREATE);

    } catch (Exception e) {
      log.info("Error when writing Custom report file: {}", e.getStackTrace());
    }
  }

  public static void generateApiMeasurementsReport(String rowsData) {
    try {
      String reportIn = new String(Files.readAllBytes(Paths.get(pathToApiReportHtmlTemplate)));
      Files.write(
          Paths.get(pathToExportApiReport),
          reportIn
              .replace("$table_data_placeholder", rowsData)
              .replace("$Date_text", "Created on: " + LocalDateTime.now().format(formatter))
              .getBytes(),
          StandardOpenOption.CREATE);

    } catch (Exception e) {
      log.info("Error when writing Custom report file: {}", e.getStackTrace());
    }
  }
}
