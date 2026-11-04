/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.environment.Environment;

@Stateless
@LocalBean
public class SiteClimateReadingService extends BaseAdoService<SiteClimateReading> {

	public SiteClimateReadingService() {
		super(SiteClimateReading.class);
	}

	/**
	 * @return the environment's readings taken from the start of {@code from} to the end of {@code to}, newest first, with
	 *         their reporting users
	 */
	public List<SiteClimateReading> getByEnvironment(Environment environment, LocalDate from, LocalDate to) {
		return em
			.createQuery(
				"SELECT r FROM " + SiteClimateReading.TABLE_NAME + " r LEFT JOIN FETCH r." + SiteClimateReading.REPORTING_USER + " WHERE r."
					+ SiteClimateReading.ENVIRONMENT + " = :environment AND r." + SiteClimateReading.READING_DATE_TIME + " >= :from AND r."
					+ SiteClimateReading.READING_DATE_TIME + " < :to ORDER BY r." + SiteClimateReading.READING_DATE_TIME + " DESC",
				SiteClimateReading.class)
			.setParameter("environment", environment)
			.setParameter("from", ClimateCalendar.toDate(from))
			.setParameter("to", ClimateCalendar.toDate(to.plusDays(1)))
			.getResultList();
	}

	/**
	 * Deletes all readings of the environment, before the environment itself is deleted permanently.
	 */
	public void deleteByEnvironment(Environment environment) {
		em.createQuery("DELETE FROM " + SiteClimateReading.TABLE_NAME + " r WHERE r." + SiteClimateReading.ENVIRONMENT + " = :environment")
			.setParameter("environment", environment)
			.executeUpdate();
	}
}
