/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse.sql;

import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_COLLECTION_SEPARATOR;
import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_DATE_FORMAT;
import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_RECORD_SEPARATOR;

import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;

/**
 * Shared SQL fragment for {@code DateOfLastVaccination} and {@code VaccinationStatus}.
 *
 * <p>
 * Opt-in fragment for vaccine-preventable subject codes; models should add matching vaccination
 * fields together with this SQL.
 */
public final class EpipulseVaccinationSql {

	private EpipulseVaccinationSql() {
	}

	/**
	 * Provides dose and immunization aggregates used by vaccination status/last-date mapping.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildVaccinationsCte(), buildImmunizationsCte())
			.joins(
				"LEFT JOIN case_all_vaccinations ON (case_all_vaccinations.person_id = c.person_id)",
				"LEFT JOIN case_all_immunizations ON (case_all_immunizations.person_id = c.person_id)")
			.build();
	}

	/**
	 * Aggregates vaccinations per person, newest first, for last-vaccination derivation.
	 */
	private static String buildVaccinationsCte() {
		//@formatter:off
		return "case_all_vaccinations AS (SELECT i.person_id," +
			   "                                      STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                           COALESCE(to_char(v.vaccinationdate, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           COALESCE(to_char(v.reportdate, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           CAST(i.id AS text)), " + SQL_COLLECTION_SEPARATOR +
			   "                                                 ORDER BY COALESCE(v.vaccinationdate, v.reportdate) DESC)" +
			   "                                          as all_vaccinations_from_latest" +
			   "                               FROM immunization i" +
			   "                                        INNER JOIN vaccination v ON i.id = v.immunization_id" +
			   "                                        CROSS JOIN variables" +
			   "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
			   "                                 and i.disease = variables.disease" +
			   "                                 and i.deleted = false" +
			   "                                 and i.meansofimmunization IN ('" + MeansOfImmunization.VACCINATION.name() + "', '" + MeansOfImmunization.VACCINATION_RECOVERY.name() + "')" +
			   "                               GROUP BY i.person_id)";
		//@formatter:on
	}

	/**
	 * Aggregates acquired immunization courses per person for vaccination-status derivation.
	 */
	private static String buildImmunizationsCte() {
		//@formatter:off
		return "case_all_immunizations AS (SELECT i.person_id," +
			   "                                      STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                           CAST(i.id AS text)," +
			   "                                                           COALESCE(to_char(i.validfrom, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           COALESCE(to_char(i.validuntil, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           COALESCE(CAST(i.numberofdoses AS text), '')), " + SQL_COLLECTION_SEPARATOR +
			   "                                                 ORDER BY i.validfrom DESC)" +
			   "                                          as all_immunizations_from_latest" +
			   "                               FROM immunization i" +
			   "                                        CROSS JOIN variables" +
			   "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
			   "                                 and i.disease = variables.disease" +
			   "                                 and i.deleted = false" +
			   "                                 and i.immunizationstatus = '" + ImmunizationStatus.ACQUIRED.name() + "'" +
			   "                                 and i.meansofimmunization IN ('" + MeansOfImmunization.VACCINATION.name() + "', '" + MeansOfImmunization.VACCINATION_RECOVERY.name() + "')" +
			   "                               GROUP BY i.person_id)";
		//@formatter:on
	}
}
