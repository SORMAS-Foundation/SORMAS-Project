package org.sormas.e2etests.helpers.parsers;

import java.io.File;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

public class XMLParser {

  @SneakyThrows
  public static Document getDocument(String filePath) {
    File inputFile = new File(filePath);
    SAXBuilder saxBuilder = new SAXBuilder();
    return saxBuilder.build(inputFile);
  }

  @SneakyThrows
  public static Document findDocumentByName(
      String folderLocation, String nameToBeContained, String fileExtension) {
    File file =
        FileUtils.listFiles(new File(folderLocation), new PrefixFileFilter(nameToBeContained), null)
            .stream()
            .filter(
                filez ->
                    filez.getName().contains(nameToBeContained)
                        && filez.getName().endsWith(fileExtension))
            .findFirst()
            .get();
    SAXBuilder saxBuilder = new SAXBuilder();
    return saxBuilder.build(file);
  }

  @SneakyThrows
  public static void printDocumentContent(Document document) {
    System.out.println(document.getDocument().getContent().get(0).getValue());
  }
}
