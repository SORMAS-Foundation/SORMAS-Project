package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum SourceTypeValues {
  NOT_APPLICABLE("NOT_APPLICABLE", "Not applicable", "Nicht erhoben"),
  MEDIA_NEWS("MEDIA_NEWS", "Media/News", "Medien/ Nachrichten"),
  HOTLINE_PERSON("HOTLINE_PERSON", "Hotline/Person", "Hotline/ Person"),
  MATHEMATICAL_MODEL("MATHEMATICAL_MODEL", "Mathematical model", "Mathematisches Modell"),
  INSTITUTIONAL_PARTNER(
      "INSTITUTIONAL_PARTNER", "Institutional partner", "Institutioneller Partner");

  private final String sourceTypeName;
  private final String sourceTypeCaption;
  private final String sourceTypeCaptionDE;
  private static Random random = new Random();

  SourceTypeValues(String sourceTypeName, String sourceTypeCaption, String sourceTypeCaptionDE) {
    this.sourceTypeName = sourceTypeName;
    this.sourceTypeCaption = sourceTypeCaption;
    this.sourceTypeCaptionDE = sourceTypeCaptionDE;
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    SourceTypeValues[] sourceTypeOptions = SourceTypeValues.values();
    for (SourceTypeValues value : sourceTypeOptions) {
      if (value.getSourceTypeName().equalsIgnoreCase(option)) return value.getSourceTypeCaption();
    }
    throw new Exception("Unable to find " + option + " value in SourceTypeValues Enum");
  }

  @SneakyThrows
  public static String getCaptionForNameDE(String option) {
    SourceTypeValues[] sourceTypeOptions = SourceTypeValues.values();
    for (SourceTypeValues value : sourceTypeOptions) {
      if (value.getSourceTypeName().equalsIgnoreCase(option)) return value.getSourceTypeCaptionDE();
    }
    throw new Exception("Unable to find " + option + " value in SourceTypeValues Enum");
  }

  @SneakyThrows
  public static String getRandomSourceTypeDifferentThan(String excludedOption) {
    SourceTypeValues[] sourceTypeValuesOptions = SourceTypeValues.values();
    for (SourceTypeValues value : sourceTypeValuesOptions) {
      if (!value.getSourceTypeCaption().equalsIgnoreCase(excludedOption)
          && !value.getSourceTypeName().equalsIgnoreCase(excludedOption))
        return value.getSourceTypeCaption();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  @SneakyThrows
  public static String getRandomSourceTypeDifferentThanDE(String excludedOption) {
    SourceTypeValues[] sourceTypeValuesOptions = SourceTypeValues.values();
    for (SourceTypeValues value : sourceTypeValuesOptions) {
      if (!value.getSourceTypeCaptionDE().equalsIgnoreCase(excludedOption)
          && !value.getSourceTypeName().equalsIgnoreCase(excludedOption))
        return value.getSourceTypeCaptionDE();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }
  /** Returns values used for API tests */
  public static String getRandomSourceTypeName() {
    return String.valueOf(
        SourceTypeValues.values()[random.nextInt(values().length)].sourceTypeName);
  }
  /** Returns values used for UI tests */
  public static String getRandomSourceTypeCaption() {
    return String.valueOf(
        SourceTypeValues.values()[random.nextInt(values().length)].sourceTypeCaption);
  }
}
