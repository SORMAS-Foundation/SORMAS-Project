package org.sormas.e2etests.pojo.helpers;

import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.testng.asserts.SoftAssert;

public abstract class ComparisonHelper {

  private static SoftAssert softly;

  @SneakyThrows
  public static void compareEqualEntities(Object pojo1, Object pojo2) {
    softly = new SoftAssert();
    for (String key : BeanUtils.describe(pojo1).keySet()) {
      softly.assertEquals(
          BeanUtils.describe(pojo1).get(key).toUpperCase(),
          BeanUtils.describe(pojo2).get(key).toUpperCase(),
          "Value for: " + key + " doesn't match");
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareEqualFieldsOfEntities(
      Object pojo1, Object pojo2, List<String> fieldsList) {
    softly = new SoftAssert();
    for (String field : fieldsList) {
      softly.assertEquals(
          BeanUtils.describe(pojo1).get(field).toUpperCase(),
          BeanUtils.describe(pojo2).get(field).toUpperCase(),
          "Value for: " + field + " doesn't match");
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentEntities(Object pojo1, Object pojo2) {
    softly = new SoftAssert();
    for (String key : BeanUtils.describe(pojo1).keySet()) {
      softly.assertNotEquals(
          BeanUtils.describe(pojo1).get(key).toUpperCase(),
          BeanUtils.describe(pojo2).get(key).toUpperCase(),
          "Value for: " + key + " doesn't match");
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentFieldsOfEntities(
      Object pojo1, Object pojo2, List<String> fieldsList) {
    softly = new SoftAssert();
    for (String field : fieldsList) {
      softly.assertNotEquals(
          BeanUtils.describe(pojo1).get(field).toUpperCase(),
          BeanUtils.describe(pojo2).get(field).toUpperCase(),
          "Value for: " + field + " doesn't match");
    }
    softly.assertAll();
  }
}
