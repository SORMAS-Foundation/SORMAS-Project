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

/**
 * Shared SQL fragment for {@code Hospitalisation}.
 *
 * <p>
 * Opt-in fragment used by subject codes declaring the variable; models should add matching CSV
 * fields together with this SQL.
 */
public final class EpipulseHospitalisationSql {

	private EpipulseHospitalisationSql() {
	}

	/**
	 * The current stay comes from the {@code hospitalization} row the case points at; previous
	 * stays come from the aggregate below.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildPreviousHospitalizationsCte())
			.joins(
				"LEFT JOIN hospitalization ON c.hospitalization_id = hospitalization.id",
				"LEFT JOIN case_all_prev_hsp_from_latest ON (hospitalization.id = case_all_prev_hsp_from_latest.hospitalization_id)")
			.build();
	}

	/**
	 * Aggregates previous hospitalisation rows per hospitalization record.
	 */
	private static String buildPreviousHospitalizationsCte() {
		//@formatter:off
		return "case_all_prev_hsp_from_latest AS (SELECT prev_hsp.hospitalization_id," +
			   "                                              STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                                   COALESCE(prev_hsp.admittedtohealthfacility, '')," +
			   "                                                                   COALESCE(prev_hsp.hospitalizationreason, '')," +
			   "                                                                   COALESCE(" +
			   "                                                                           TO_CHAR(prev_hsp.admissiondate, " + SQL_DATE_FORMAT + ")," +
			   "                                                                           '')," +
			   "                                                                   COALESCE(" +
			   "                                                                           TO_CHAR(prev_hsp.dischargedate, " + SQL_DATE_FORMAT + ")," +
			   "                                                                           '')" +
			   "                                                         ), " + SQL_COLLECTION_SEPARATOR +
			   "                                                         ORDER BY prev_hsp.admissiondate DESC) as all_prev_hsp_from_latest" +
			   "                                       FROM previoushospitalization as prev_hsp" +
			   "                                       WHERE hospitalization_id IN (SELECT hospitalization_id" +
			   "                                                                    FROM filtered_cases" +
			   "                                                                    WHERE hospitalization_id IS NOT NULL)" +
			   "                                       GROUP BY prev_hsp.hospitalization_id)";
		//@formatter:on
	}
}
