/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.sormas.e2etests.pojo.helpers;

import java.util.List;
import java.util.Map;
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
          getEntityFieldValue(pojo1, key),
          getEntityFieldValue(pojo2, key),
          "Value for: [" + key + "] field doesn't match.");
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareEqualFieldsOfEntities(
      Object pojo1, Object pojo2, List<String> fieldsList) {
    softly = new SoftAssert();
    for (String field : fieldsList) {
      softly.assertEquals(
          getEntityFieldValue(pojo1, field),
          getEntityFieldValue(pojo2, field),
          "Value for: " + field + " doesn't match");
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentEntities(Object pojo1, Object pojo2) {
    softly = new SoftAssert();
    for (String key : BeanUtils.describe(pojo1).keySet()) {
      softly.assertNotEquals(
          getEntityFieldValue(pojo1, key),
          getEntityFieldValue(pojo2, key),
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
          getEntityFieldValue(pojo1, field),
          getEntityFieldValue(pojo2, field),
          "Value for: " + field + " doesn't match");
    }
    softly.assertAll();
  }

  private static String getEntityFieldValue(Object pojo, String fieldName) throws Exception {
    Map<String, String> pojoFields;
    try {
      if (pojo == null) {
        throw new NullPointerException(
            "Provided Entity hasn't been initialized, "
                + pojo.getClass().getCanonicalName()
                + " is null");
      }
      pojoFields = BeanUtils.describe(pojo);
    } catch (Exception e) {
      throw new Exception("Couldn't get Entity: " + pojo.toString() + " due to: " + e.getMessage());
    }
    try {
      return pojoFields.get(fieldName).toUpperCase();
    } catch (NullPointerException e) {
      return "Empty value";
    }
  }
}
