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

package org.sormas.e2etests.common;

import java.time.Duration;
import java.time.Instant;

public class TimerLite {

  private final Instant stopAt;

  private TimerLite(Instant stopAt) {
    this.stopAt = stopAt;
  }

  public boolean isTimeUp() {
    return Instant.now().isAfter(stopAt);
  }

  public static TimerLite of(Duration duration) {
    return new TimerLite(Instant.now().plus(duration));
  }
}
