package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum SourceTypeValues {
  NOT_APPLICABLE("NOT_APPLICABLE", "Not applicable"),
  MEDIA_NEWS("MEDIA_NEWS", "Media/News"),
  HOTLINE_PERSON("HOTLINE_PERSON", "Hotline/Person"),
  MATHEMATICAL_MODEL("MATHEMATICAL_MODEL", "Mathematical model"),
  INSTITUTIONAL_PARTNER("INSTITUTIONAL_PARTNER", "Institutional partner");

  private final String sourceTypeName;
  private final String sourceTypeCaption;
  private static Random random = new Random();

  SourceTypeValues(String sourceTypeName, String sourceTypeCaption) {
    this.sourceTypeName = sourceTypeName;
    this.sourceTypeCaption = sourceTypeCaption;
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    SourceTypeValues[] sourceTypeOptions = SourceTypeValues.values();
    for (SourceTypeValues value : sourceTypeOptions) {
      if (value.getSourceTypeName().equalsIgnoreCase(option)) return value.getSourceTypeCaption();
    }
    throw new Exception("Unable to find " + option + " value in SourceTypeValues Enum");
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
