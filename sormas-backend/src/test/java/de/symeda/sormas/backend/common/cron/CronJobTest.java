/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.common.cron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.backend.common.CronService;

public class CronJobTest {

	@Test
	public void everyDefaultExpressionIsValid() {
		for (CronJob job : CronJob.values()) {
			assertTrue(CronExpressionValidator.isValid(job.getDefaultExpression()), job.name());
		}
	}

	@Test
	public void noDefaultExpressionIsDisabled() {
		for (CronJob job : CronJob.values()) {
			assertTrue(!CronExpressionValidator.isDisabled(job.getDefaultExpression()), job.name());
		}
	}

	@Test
	public void configKeysArePrefixedAndUnique() {

		Set<String> keys = Arrays.stream(CronJob.values()).map(CronJob::getConfigKey).collect(Collectors.toSet());

		assertEquals(CronJob.values().length, keys.size());
		for (String key : keys) {
			assertTrue(key.startsWith("CRON."), key);
			assertTrue(key.matches("[A-Z0-9_.]+"), key);
		}
	}

	@Test
	public void everyCronServiceMethodIsRegisteredExactlyOnce() {

		CronService cronService = mock(CronService.class);
		for (CronJob job : CronJob.values()) {
			job.execute(cronService);
		}

		Set<String> invoked = mockingDetails(cronService).getInvocations()
			.stream()
			.map(invocation -> invocation.getMethod().getName())
			.collect(Collectors.toSet());

		Set<String> declared = Arrays.stream(CronService.class.getDeclaredMethods())
			.filter(method -> Modifier.isPublic(method.getModifiers()))
			.filter(method -> method.getParameterCount() == 0)
			.filter(method -> method.getReturnType() == void.class)
			.map(Method::getName)
			.collect(Collectors.toSet());

		assertEquals(declared, invoked);
		assertEquals(CronJob.values().length, invoked.size());
	}
}
