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
import static de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql.exportDiseaseTest;

import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.sample.PathogenTestResultType;

/**
 * Disease-specific SQL for MALA.
 *
 * <p>
 * Adds two CTEs ({@code mala_pathogens}, {@code mala_travel}), plus a direct {@code epidata}
 * join. {@code epidata} is 1:1 with the case, while the other two sources require aggregation or
 * deterministic row selection.
 *
 * <p>
 * Columns like {@code Occupation} and {@code ClinicalCriteriaStatus} are already in scope from
 * the common query; {@code PlaceOfInfection} is provided by
 * {@link EpipulsePlaceOfInfectionSql} and merged by the field model.
 */
public final class MalaSql {

	private MalaSql() {
	}

	/**
	 * Declares MALA's CTEs and joins.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildMalaPathogensCte(), buildMalaTravelCte())
			.joins(
				// Imported, ModeOfTransmission
				"LEFT JOIN epidata mala_epidata ON c.epidata_id = mala_epidata.id",
				"LEFT JOIN mala_pathogens mala_path ON mala_path.case_id = c.id",
				"LEFT JOIN mala_travel ON mala_travel.case_id = c.id")
			.build();
	}

	/**
	 * Builds {@code Pathogen}: distinct <em>Plasmodium</em> species as a {@code #}-separated list.
	 *
	 * <p>
	 * Uses aggregation because the variable is repeatable (coinfections must keep multiple species).
	 * Counts only undeleted, positive, verified tests with non-null {@code specie}. {@code DISTINCT}
	 * removes duplicates across tests; ordering by species keeps output stable across reruns.
	 */
	private static String buildMalaPathogensCte() {
		//@formatter:off
		return "mala_pathogens AS (" +
			   "    SELECT c.id as case_id," +
			   "           STRING_AGG(DISTINCT pt.specie, " + SQL_COLLECTION_SEPARATOR + " ORDER BY pt.specie) as pathogens " +
			   "    FROM filtered_cases c " +
			   "    JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "    WHERE pt.specie IS NOT NULL " +
			   "      AND pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "      AND pt.testresultverified = true " +
			   "    GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * Builds {@code Prophylaxis} and {@code PurposeOfTravel} from one travel exposure.
	 *
	 * <p>
	 * Reads only {@code TRAVEL} exposures, keeps rows that contain at least one of the two fields,
	 * and picks the latest journey by {@code COALESCE(enddate, startdate)} (tie-breaker:
	 * {@code exp.id}).
	 *
	 * <p>
	 * No incubation-window filter is applied here: unlike {@code PlaceOfInfection}, these columns
	 * describe the reported trip itself.
	 */
	private static String buildMalaTravelCte() {
		//@formatter:off
		return "mala_travel AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           exp.travelpurpose as travel_purpose," +
			   "           exp.prophylaxisadherence as prophylaxis_adherence " +
			   "    FROM filtered_cases c " +
			   "    JOIN exposures exp ON exp.epidata_id = c.epidata_id " +
			   "    WHERE exp.exposuretype = '" + ExposureType.TRAVEL.name() + "' " +
			   "      AND (exp.travelpurpose IS NOT NULL OR exp.prophylaxisadherence IS NOT NULL) " +
			   "    ORDER BY c.id, COALESCE(exp.enddate, exp.startdate) DESC NULLS LAST, exp.id DESC)";
		//@formatter:on
	}
}
