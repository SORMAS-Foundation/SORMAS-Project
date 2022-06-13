/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum PathogenTestResults {
  INDETERMINATE("Indeterminate", "Unbestimmt"),
  PENDING("Pending", "Ausstehend"),
  NEGATIVE("Negative", "Negativ"),
  POSITIVE("Positive", "Positiv"),
  NOT_DONE("Not done", "Nicht durchgef\u00FChrt");

  private final String pathogenResults;
  private final String pathogenResultsDE;

  PathogenTestResults(String vPathogen, String vPathogenDE) {
    pathogenResults = vPathogen;
    pathogenResultsDE = vPathogenDE;
  }

  public static String getRandomResult() {
    Random random = new Random();
    return String.valueOf(PathogenTestResults.values()[random.nextInt(values().length)]);
  }

  public static String geRandomResultName() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestResults.values()[random.nextInt(values().length)].pathogenResults);
  }

  @SneakyThrows
  public static String geRandomResultNameDifferentThan(String excludedOption) {
    PathogenTestResults[] pathogenTestResults = PathogenTestResults.values();
    for (PathogenTestResults value : pathogenTestResults) {
      if (!value.getPathogenResults().equalsIgnoreCase(excludedOption))
        return value.getPathogenResults();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  @SneakyThrows
  public static String geRandomResultNameDifferentThanDE(String excludedOption) {
    PathogenTestResults[] pathogenTestResults = PathogenTestResults.values();
    for (PathogenTestResults value : pathogenTestResults) {
      if (!value.getPathogenResults().equalsIgnoreCase(excludedOption))
        return value.getPathogenResultsDE();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }
}
