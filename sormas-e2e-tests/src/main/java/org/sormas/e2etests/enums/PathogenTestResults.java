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
  INDETERMINATE("Indeterminate"),
  PENDING("Pending"),
  NEGATIVE("Negative"),
  POSITIVE("Positive"),
  NOT_DONE("Not done");

  private final String pathogenResults;

  PathogenTestResults(String vPathogen) {
    pathogenResults = vPathogen;
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
}
