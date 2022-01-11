package org.sormas.e2etests.pojo.helpers;

import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.assertj.core.api.SoftAssertions;

public abstract class ComparisonHelper {

  private static SoftAssertions softly;

  @SneakyThrows
  public static void compareEqualEntities(Object pojo1, Object pojo2) {
    softly = new SoftAssertions();
    for (String key : BeanUtils.describe(pojo1).keySet()) {
      if (key.contains("name") || key.equalsIgnoreCase("disease")) {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(key))
            .withFailMessage("[" + key + "]" + " value doesn't match.")
            .isEqualToIgnoringCase(BeanUtils.describe(pojo2).get(key));
      } else {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(key))
            .withFailMessage("[" + key + "]" + " value doesn't match.")
            .isEqualTo(BeanUtils.describe(pojo2).get(key));
      }
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareEqualFieldsOfEntities(
      Object pojo1, Object pojo2, List<String> fieldsList) {
    softly = new SoftAssertions();
    for (String field : fieldsList) {
      if (field.contains("name") || field.equalsIgnoreCase("disease")) {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(field))
            .withFailMessage("[" + field + "]" + " value doesn't match.")
            .isEqualToIgnoringCase(BeanUtils.describe(pojo2).get(field));
      } else {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(field))
            .withFailMessage("[" + field + "]" + " value doesn't match.")
            .isEqualTo(BeanUtils.describe(pojo2).get(field));
      }
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentEntities(Object pojo1, Object pojo2) {
    softly = new SoftAssertions();
    for (String key : BeanUtils.describe(pojo1).keySet()) {
      if (key.contains("name") || key.equalsIgnoreCase("disease")) {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(key))
            .withFailMessage("[" + key + "]" + " value is not different.")
            .isNotEqualToIgnoringCase(BeanUtils.describe(pojo2).get(key));
      } else {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(key))
            .withFailMessage("[" + key + "]" + " value is not different.")
            .isNotEqualTo(BeanUtils.describe(pojo2).get(key));
      }
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentFieldsOfEntities(
      Object pojo1, Object pojo2, List<String> fieldsList) {
    softly = new SoftAssertions();
    for (String field : fieldsList) {
      if (field.contains("name") || field.equalsIgnoreCase("disease")) {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(field))
            .withFailMessage("[" + field + "]" + " value doesn't match.")
            .isNotEqualToIgnoringCase(BeanUtils.describe(pojo2).get(field));
      } else {
        softly
            .assertThat(BeanUtils.describe(pojo1).get(field))
            .withFailMessage("[" + field + "]" + " value doesn't match.")
            .isNotEqualTo(BeanUtils.describe(pojo2).get(field));
      }
    }
    softly.assertAll();
  }
}
