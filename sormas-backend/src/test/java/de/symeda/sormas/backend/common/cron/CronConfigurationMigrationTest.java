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

import static org.junit.Assume.assumeNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.hilling.junit.cdi.CdiTestJunitExtension;
import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.backend.common.HistoryTablesTest.SormasPostgresSQLContainer;

@ExtendWith(CdiTestJunitExtension.class)
public class CronConfigurationMigrationTest {

	@Test
	public void migrationSeedsEveryCronConfigurationValue() {

		SormasPostgresSQLContainer container = new SormasPostgresSQLContainer();
		try {
			container.start();
		} catch (IllegalStateException e) {
			assumeNoException("Could not find a valid Docker environment, skipping test", e);
		}

		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", container.getJdbcUrl());
		properties.put("javax.persistence.jdbc.user", container.getUsername());
		properties.put("javax.persistence.jdbc.password", container.getPassword());
		properties.put("javax.persistence.jdbc.driver", container.getDriverClassName());
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
		properties.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform");
		properties.put("hibernate.hbm2ddl.auto", "none");

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("beanTestPU", properties);
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		@SuppressWarnings("unchecked")
		List<Object[]> rows = entityManager
			.createNativeQuery(
				"SELECT v.config_key, v.config_value, v.value_pattern, c.name "
					+ "FROM systemconfigurationvalue v JOIN systemconfigurationcategory c ON c.id = v.category_id "
					+ "WHERE v.config_key LIKE 'CRON.%'")
			.getResultList();

		Map<String, Object[]> rowsByKey = rows.stream().collect(Collectors.toMap(row -> (String) row[0], row -> row));

		assertEquals(CronJob.values().length, rowsByKey.size());

		for (CronJob job : CronJob.values()) {
			Object[] row = rowsByKey.get(job.getConfigKey());
			assertNotNull(row, job.getConfigKey());
			assertEquals(job.getDefaultExpression(), row[1], job.getConfigKey());
			assertEquals(CronExpressionValidator.VALUE_PATTERN, row[2], job.getConfigKey());
			assertEquals("CRON", row[3], job.getConfigKey());
		}

		java.util.regex.Pattern stored = java.util.regex.Pattern.compile((String) rowsByKey.values().iterator().next()[2]);
		assertFalse(stored.matcher("0 */70 * * * *").matches(), "stored pattern must reject an out of range increment");
		assertFalse(stored.matcher("0 0 0 */2 * *").matches(), "stored pattern must reject an increment on day of month");
		assertTrue(stored.matcher("0 15 1 * * *").matches());
		assertTrue(stored.matcher("").matches());
	}
}
