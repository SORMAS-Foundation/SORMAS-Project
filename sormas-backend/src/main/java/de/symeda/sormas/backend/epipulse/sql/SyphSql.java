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
 * Disease-specific SQL for SYPH.
 *
 * <p>
 * Adds two 1:1 joins and no CTEs: {@code epidata} for STI service/transmission fields and
 * {@code healthconditions} for HIV/prophylaxis fields.
 *
 * <p>
 * Country and symptom-based columns are handled elsewhere (shared fragments and common
 * {@code symptom} join), so they are intentionally absent here.
 */
public final class SyphSql {

	private SyphSql() {
	}

	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.joins(
				// ClinicalServiceType, ModeOfTransmission, SexWorker, ContactSW
				"LEFT JOIN epidata syph_epidata ON c.epidata_id = syph_epidata.id",
				// HIVStatus, HIVPrEP, AntibioticProphylaxis
				"LEFT JOIN healthconditions syph_health ON c.healthconditions_id = syph_health.id")
			.build();
	}
}
