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

import de.symeda.sormas.api.epidata.CaseImportedStatus;

/**
 * Shared SQL fragment for {@code PlaceOfInfection}.
 *
 * <p>
 * Supports imported-only and all-cases rules, applies incubation-window filtering, and resolves a
 * final place with contamination-country override before exposure-derived places.
 */
public final class EpipulsePlaceOfInfectionSql {

	/** Which subject-code rule to apply for {@code PlaceOfInfection}. */
	public enum Rule {

		/** DIPH, LEGI, MEAS, MENI, RUBE: country-level places, only for imported cases. */
		IMPORTED_COUNTRY,
		/** Other codes: lowest available level for every case (currently served at country level). */
		LOWEST_LEVEL
	}

	private EpipulsePlaceOfInfectionSql() {
	}

	public static SqlQueryModel spec(Rule rule) {

		return SqlQueryModel.builder()
			.ctes(buildExposurePlacesCte(rule), buildContaminationCountryCte(), buildPlacesOfInfectionCte())
			.joins("LEFT JOIN case_places_of_infection poi ON poi.case_id = c.id")
			.build();
	}

	/**
	 * Exposure-derived places per case as distinct {@code #}-separated entries.
	 */
	private static String buildExposurePlacesCte(Rule rule) {
		//@formatter:off
		return "case_exposure_places AS (" +
			   "    SELECT c.id as case_id," +
			   "           STRING_AGG(DISTINCT " + placeExpression(rule) + ", " + SQL_COLLECTION_SEPARATOR + " ORDER BY " + placeExpression(rule) + ") as places_of_infection " +
			   "    FROM filtered_cases c " +
			   "    CROSS JOIN variables v " +
			   "    JOIN exposures exp ON exp.epidata_id = c.epidata_id " +
			   "    JOIN location exp_loc ON exp.location_id = exp_loc.id " +
			   "    LEFT JOIN country exp_country ON exp_loc.country_id = exp_country.id " +
			   "    LEFT JOIN symptoms sym ON sym.id = c.symptoms_id " +
			   "    LEFT JOIN epidata epi ON epi.id = c.epidata_id " +
			   "    WHERE " + placeExpression(rule) + " IS NOT NULL " +
			   importedGate(rule) +
			   // Undated exposures cannot be placed inside the incubation window.
			   "      AND COALESCE(exp.startdate, exp.enddate) IS NOT NULL " +
			   // If incubation window is unknown, keep all dated exposures.
			   "      AND (v.max_incubation_days IS NULL " +
			   "           OR (CAST(COALESCE(exp.startdate, exp.enddate) AS date)" +
			   "                   <= CAST(COALESCE(sym.onsetdate, c.reportdate) AS date) - v.min_incubation_days " +
			   "               AND CAST(COALESCE(exp.enddate, exp.startdate) AS date)" +
			   "                   >= CAST(COALESCE(sym.onsetdate, c.reportdate) AS date) - v.max_incubation_days)) " +
			   "    GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * Country-of-contamination place per non-imported case.
	 */
	private static String buildContaminationCountryCte() {
		//@formatter:off
		return "case_contamination_country AS (" +
			   "    SELECT c.id as case_id," +
			   "           epi_country.nutscode as place_of_infection " +
			   "    FROM filtered_cases c " +
			   "    JOIN epidata epi ON epi.id = c.epidata_id " +
			   "    JOIN country epi_country ON epi_country.id = epi.country_id " +
			   "    WHERE epi_country.nutscode IS NOT NULL " +
			   // Blank import status counts as not imported.
			   "      AND epi.caseimportedstatus IS DISTINCT FROM '" + CaseImportedStatus.IMPORTED_CASE.name() + "')";
		//@formatter:on
	}

	/**
	 * Resolves final place with priority: contamination country, then exposure places.
	 */
	private static String buildPlacesOfInfectionCte() {
		//@formatter:off
		return "case_places_of_infection AS (" +
			   "    SELECT c.id as case_id," +
			   "           COALESCE(cc.place_of_infection, exp_places.places_of_infection) as places_of_infection " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN case_contamination_country cc ON cc.case_id = c.id " +
			   "    LEFT JOIN case_exposure_places exp_places ON exp_places.case_id = c.id)";
		//@formatter:on
	}

	/**
	 * Place expression used for exposure-derived rows.
	 */
	private static String placeExpression(Rule rule) {

		switch (rule) {
		case IMPORTED_COUNTRY:
			return "exp_country.nutscode";
		case LOWEST_LEVEL:
			return "exp_country.nutscode";
		default:
			throw new IllegalArgumentException("Unhandled place of infection rule: " + rule);
		}
	}

	/**
	 * Rule-specific gate for exposure-derived rows.
	 */
	private static String importedGate(Rule rule) {

		return rule == Rule.IMPORTED_COUNTRY ? "      AND epi.caseimportedstatus = '" + CaseImportedStatus.IMPORTED_CASE.name() + "' " : "";
	}
}
