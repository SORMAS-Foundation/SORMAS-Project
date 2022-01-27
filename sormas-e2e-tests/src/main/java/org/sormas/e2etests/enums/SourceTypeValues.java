package org.sormas.e2etests.enums;

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
}
