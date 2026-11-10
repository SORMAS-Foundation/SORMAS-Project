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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.immunization.MeansOfImmunization;

/**
 * Shared SQL fragment for {@code DateOfLastVaccination} and {@code VaccinationStatus}, and for
 * {@code VaccinationStatusMaternal} where a subject code declares it.
 *
 * <p>
 * Opt-in fragment for vaccine-preventable subject codes; models should add matching vaccination
 * fields together with this SQL.
 *
 */
public final class EpipulseVaccinationSql {

	private EpipulseVaccinationSql() {
	}

	/**
	 * Provides the dose and immunization aggregates {@code VaccinationStatus} and
	 * {@code DateOfLastVaccination} are derived from.
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
	 * Every dose the patient received for the disease, newest first: one record per dose, whatever
	 * immunization it was recorded under, provided that immunization's means holds doses.
	 */
	private static String buildVaccinationsCte() {
		//@formatter:off
		return "case_all_vaccinations AS (SELECT i.person_id," +
			   "                                      STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                           COALESCE(to_char(v.vaccinationdate, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           COALESCE(to_char(v.reportdate, " + SQL_DATE_FORMAT + "), '')), " + SQL_COLLECTION_SEPARATOR +
			   "                                                 ORDER BY COALESCE(v.vaccinationdate, v.reportdate) DESC)" +
			   "                                          as all_vaccinations_from_latest" +
			   "                               FROM immunization i" +
			   "                                        INNER JOIN vaccination v ON i.id = v.immunization_id" +
			   "                                        CROSS JOIN variables" +
			   "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
			   "                                 and i.disease = variables.disease" +
			   "                                 and i.deleted = false" +
			   "                                 and i.meansofimmunization IN (" + inlined(EpipulseMapping.VACCINATION_DOSE_MEANS) + ")" +
			   "                               GROUP BY i.person_id)";
		//@formatter:on
	}

	/**
	 * The patient's immunizations for the disease that dose rows cannot account for. This covers
	 * three cases:
	 * <ul>
	 * <li>Vaccinations with no doses entered under them.</li>
	 * <li>Explicit {@code NOT_IMMUNIZED} records.</li>
	 * <li>{@code MATERNAL_VACCINATION} records, which feed {@code VaccinationStatusMaternal}.</li>
	 * </ul>
	 *
	 * <p>
	 * Each rule in {@code EpipulseMapping} reads only the means it names, so a status code that
	 * does not reference the maternal column is unaffected by maternal records.
	 */
	private static String buildImmunizationsCte() {

		Set<MeansOfImmunization> means = EnumSet.copyOf(EpipulseMapping.VACCINATION_DOSE_MEANS);
		means.add(MeansOfImmunization.NOT_IMMUNIZED);
		means.add(MeansOfImmunization.MATERNAL_VACCINATION);

		//@formatter:off
		return "case_all_immunizations AS (SELECT i.person_id," +
			   "                                      STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                           i.meansofimmunization," +
			   "                                                           COALESCE(to_char(i.startdate, " + SQL_DATE_FORMAT + "), '')," +
			   "                                                           COALESCE(to_char(i.reportdate, " + SQL_DATE_FORMAT + "), '')), " + SQL_COLLECTION_SEPARATOR +
			   "                                                 ORDER BY i.reportdate DESC)" +
			   "                                          as all_immunizations_from_latest" +
			   "                               FROM immunization i" +
			   "                                        CROSS JOIN variables" +
			   "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
			   "                                 and i.disease = variables.disease" +
			   "                                 and i.deleted = false" +
			   "                                 and i.meansofimmunization IN (" + inlined(means) + ")" +
			   "                               GROUP BY i.person_id)";
		//@formatter:on
	}

	/** Enum names as an SQL list, in declaration order */
	private static String inlined(Collection<MeansOfImmunization> means) {
		return means.stream().sorted().map(m -> "'" + m.name() + "'").collect(Collectors.joining(", "));
	}
}
