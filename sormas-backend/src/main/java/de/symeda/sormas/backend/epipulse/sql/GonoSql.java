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

/**
 * Disease-specific SQL for GONO.
 *
 * <p>
 * Adds two direct joins and no CTEs: {@code epidata} and {@code healthconditions} are both
 * 1:1 with the case via foreign keys, so no aggregation or row-picking is needed.
 *
 * <p>
 * Other GONO columns come from already-available sources: {@code symptoms} in the common query,
 * and shared fragments such as {@link EpipulseCountryOfBirthSql} and
 * {@link EpipulsePlaceOfInfectionSql}.
 */
public final class GonoSql {

	private GonoSql() {
	}

	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.joins(
				// ClinicalServiceType, ModeOfTransmission, SexWorker, ContactSW
				"LEFT JOIN epidata gono_epidata ON c.epidata_id = gono_epidata.id",
				// HIVStatus, HIVPrEP, AntibioticProphylaxis
				"LEFT JOIN healthconditions gono_health ON c.healthconditions_id = gono_health.id")
			.build();
	}
}
