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

package org.sormas.e2etests.helpers;

import static junit.framework.TestCase.fail;
import static org.awaitility.Awaitility.await;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.ThrowingRunnable;

@Slf4j
public class AssertHelpers {

  @SneakyThrows
  public void assertWithPoll(ThrowingRunnable throwingRunnable, int seconds) {
    try {
      await()
          .pollInterval(fibonacci(TimeUnit.MILLISECONDS))
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(Duration.ofSeconds(seconds))
          .untilAsserted(throwingRunnable);
    } catch (ConditionTimeoutException e) {
      fail(e.getCause().getLocalizedMessage());
    }
  }

  @SneakyThrows
  public void assertWithPollWithoutFail(ThrowingRunnable throwingRunnable, int seconds) {
    try {
      await()
          .pollInterval(fibonacci(TimeUnit.MILLISECONDS))
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(Duration.ofSeconds(seconds))
          .untilAsserted(throwingRunnable);
    } catch (ConditionTimeoutException e) {
    }
  }

  public void assertWithPoll20Second(ThrowingRunnable throwingRunnable) {
    assertWithPoll(throwingRunnable, 20);
  }
}
