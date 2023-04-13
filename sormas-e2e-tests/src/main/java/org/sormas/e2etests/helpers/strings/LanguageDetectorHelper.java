package org.sormas.e2etests.helpers.strings;

import static org.apache.commons.lang3.StringUtils.capitalize;

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
    if (isConfidenceStrong(textToScan)) {
      log.info("Check if text {} language is {}", textToScan, expectedLanguage);
      Assert.assertEquals(
          capitalize(scanLanguage(textToScan)),
          expectedLanguage,
          "Text: [" + textToScan + "] Language is not as expected");
    } else {
      throw new LanguageDetectorException(
          "LanguageDetectorHelper confidence is not at least MEDIUM");
    }
  }

  @SneakyThrows
  public static String scanLanguage(String textToScan) {
    detector = LanguageDetector.getDefaultLanguageDetector().loadModels();
    String detectedLanguageCode = detector.detect(textToScan).getLanguage();
    String detectedLanguage =
        Arrays.stream(Locale.getAvailableLocales())
            .filter(locale -> locale.getLanguage().equalsIgnoreCase(detectedLanguageCode))
            .findFirst()
            .orElseThrow(
                () ->
                    new LanguageDetectorException(
                        "Unable to find language for code: " + detectedLanguageCode))
            .getDisplayLanguage();
    // Workaround for Malay due to library bug
    return (detectedLanguage.equalsIgnoreCase("Malay")) ? "English" : detectedLanguage;
  }

  @SneakyThrows
  private static boolean isConfidenceStrong(String textToScan) {
    scanTextForNumbers(textToScan);
    detector = LanguageDetector.getDefaultLanguageDetector().loadModels();
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
