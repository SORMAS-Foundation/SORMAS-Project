package org.sormas.e2etests.helpers.files;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.testng.Assert;

public abstract class FilesHelper {

  private static AssertHelpers assertHelpers = new AssertHelpers();
  private static File file;
  private static Path path;

  private static final String DOWNLOADS_FOLDER = System.getProperty("user.dir") + "//downloads//";

  public static void waitForFileToDownload(String fileName, int waitingTime) {
    path = Paths.get(DOWNLOADS_FOLDER + fileName);
    assertHelpers.assertWithPoll(
        () ->
            Assert.assertTrue(
                Files.exists(path), fileName + " wasn't downloaded: " + path.toAbsolutePath()),
        waitingTime);
  }

  @SneakyThrows
  public static void deleteFile(String fileName) {
    file = new File(DOWNLOADS_FOLDER + fileName);
    try {
      file.deleteOnExit();
    } catch (Exception any) {
      throw new Exception(
          String.format("Unable to delete file: [ %s ] due to: [ %s]", fileName, any.getMessage()));
    }
  }

  public static void validateFileIsNotEmpty(String fileName) {
    Assert.assertTrue(
        FileUtils.sizeOf(new File(DOWNLOADS_FOLDER + fileName)) > 5, fileName + " is empty");
  }

  @SneakyThrows
  public static Workbook getExcelFile(String fileName) {
    waitForFileToDownload(fileName, 30);
    FileInputStream excelFile = new FileInputStream(DOWNLOADS_FOLDER + fileName);
    deleteFile(fileName);
    return new XSSFWorkbook(excelFile);
  }
}
