package org.sormas.e2etests.helpers.strings;

import com.hpe.caf.languagedetection.LanguageDetectorException;
import java.util.Arrays;
import java.util.Locale;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.language.detect.LanguageDetector;
import org.testng.Assert;

@Slf4j
public abstract class LanguageDetectorHelper {

  private static LanguageDetector detector;

  @SneakyThrows
  public static void checkLanguage(String textToScan, String expectedLanguage) {
    String langCode = getLanguageCode(expectedLanguage);
    detector = LanguageDetector.getDefaultLanguageDetector().loadModels();
    if (isConfidenceStrong(textToScan)) {
      log.info("Check if text {} language is {}", textToScan, expectedLanguage);
      Assert.assertEquals(
          detector.detect(textToScan).getLanguage(), langCode, "Language is not as expected");
    } else {
      throw new LanguageDetectorException(
          "LanguageDetectorHelper confidence is not at least MEDIUM");
    }
  }

  @SneakyThrows
  public static String scanLanguage(String textToScan) {
    detector = LanguageDetector.getDefaultLanguageDetector().loadModels();
    return detector.detect(textToScan).getLanguage();
  }

  private static String getLanguageCode(String expectedLanguage) {
    String lang =
        Arrays.stream(Locale.getAvailableLocales())
            .filter(locale -> locale.getDisplayLanguage().equalsIgnoreCase(expectedLanguage))
            .findFirst()
            .get()
            .toString();
    return lang.substring(0, lang.indexOf("_"));
  }

  private static boolean isConfidenceStrong(String textToScan) {
    scanTextForNumbers(textToScan);
    String confidence = detector.detect(sanitizeCharacters(textToScan)).getConfidence().toString();
    return confidence.equalsIgnoreCase("MEDIUM") || confidence.equalsIgnoreCase("HIGH");
  }

  @SneakyThrows
  private static void scanTextForNumbers(String textToScan) {
    if (textToScan.matches(".*[0-9].*")) {
      throw new LanguageDetectorException(
          "Provided text contains numbers. Please remove them before scanning it.");
    }
  }

  private static String sanitizeCharacters(String textToClean) {
    return textToClean.replaceAll("[!@#$%^&*()-_=+{[}];:'\"<>,.?/~`|]", "");
  }
}
