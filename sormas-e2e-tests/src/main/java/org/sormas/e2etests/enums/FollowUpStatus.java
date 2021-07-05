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

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;

@Getter
public enum FollowUpStatus {
  FOLLOW_UP("Under follow-up"),
  COMPLETED("Completed follow-up"),
  CANCELED("Canceled follow-up"),
  LOST("Lost follow-up"),
  NO_FOLLOW_UP("No follow-up");

  private final String followUp;

  FollowUpStatus(String fallowUpStatus) {
    followUp = fallowUpStatus;
  }

  public static String getRandomFallowUp() {
    Random random = new Random();
    return String.valueOf(FollowUpStatus.values()[random.nextInt(values().length)]);
  }
}
