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
  public static void compareEqualEntities(Object pojoToCompare, Object referencePojo) {
    checkObjectsClassType(pojoToCompare, referencePojo);
    softly = new SoftAssert();
    for (String key : BeanUtils.describe(referencePojo).keySet()) {
      softly.assertEquals(
          getEntityFieldValue(pojoToCompare, key),
          getEntityFieldValue(referencePojo, key),
          String.format("Value for [%s] field doesn't match.", key));
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareEqualFieldsOfEntities(
      Object pojoToCompare, Object referencePojo, List<String> fieldsList) {
    checkObjectsClassType(pojoToCompare, referencePojo);
    softly = new SoftAssert();
    for (String field : fieldsList) {
      softly.assertEquals(
          getEntityFieldValue(pojoToCompare, field),
          getEntityFieldValue(referencePojo, field),
          String.format("Value for [%s] field doesn't match.", field));
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentEntities(Object pojoToCompare, Object referencePojo) {
    checkObjectsClassType(pojoToCompare, referencePojo);
    softly = new SoftAssert();
    for (String key : BeanUtils.describe(referencePojo).keySet()) {
      softly.assertNotEquals(
          getEntityFieldValue(pojoToCompare, key),
          getEntityFieldValue(referencePojo, key),
          String.format("Value for [%s] field doesn't match.", key));
    }
    softly.assertAll();
  }

  @SneakyThrows
  public static void compareDifferentFieldsOfEntities(
      Object pojoToCompare, Object referencePojo, List<String> fieldsList) {
    checkObjectsClassType(pojoToCompare, referencePojo);
    softly = new SoftAssert();
    for (String field : fieldsList) {
      softly.assertNotEquals(
          getEntityFieldValue(pojoToCompare, field),
          getEntityFieldValue(referencePojo, field),
          String.format("Value for [%s] field doesn't match.", field));
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
      throw new Exception(
          "Couldn't get Entity: "
              + pojo.getClass().getCanonicalName()
              + " due to: "
              + e.getMessage());
    }
    try {
      return pojoFields.get(fieldName).toUpperCase();
    } catch (NullPointerException e) {
      return "Empty value";
    }
  }

  private static void checkObjectsClassType(Object obj1, Object obj2) throws Exception {
    if (obj1.getClass() != obj2.getClass())
      throw new Exception(
          String.format(
              "Unable to compare different Objects: [%s] with [%s]",
              obj1.getClass(), obj2.getClass()));
  }
}
