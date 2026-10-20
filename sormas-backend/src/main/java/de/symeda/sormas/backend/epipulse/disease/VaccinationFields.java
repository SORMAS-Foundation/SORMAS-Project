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

package de.symeda.sormas.backend.epipulse.disease;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulseImmunizationCheckDto;
import de.symeda.sormas.api.epipulse.EpipulseVaccinationCheckDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * Vaccination source fields used to derive {@code DateOfLastVaccination} and
 * {@code VaccinationStatus}.
 *
 * <p>
 * This block is disease-selective (vaccine-preventable subject codes), so it is not part of
 * {@link CommonFields}. Callers must also merge {@code EpipulseVaccinationSql.spec()}.
 *
 * <p>
 * It loads aggregated vaccination/immunization records; final course/dose selection rules are in
 * {@code EpipulseMapping}.
 */
final class VaccinationFields {

	private VaccinationFields() {
	}

	static void addVaccinationFields(List<ValueDef> fields) {

		fields.add(
			ValueDef.sqlOnly(
				"all_vaccinations_from_latest",
				"case_all_vaccinations.all_vaccinations_from_latest",
				(dto, row) -> dto.setVaccinations(parseVaccinations((String) row.get("all_vaccinations_from_latest")))));

		fields.add(
			ValueDef.sqlOnly(
				"all_immunizations_from_latest",
				"case_all_immunizations.all_immunizations_from_latest",
				(dto, row) -> dto.setImmunizations(parseImmunizations((String) row.get("all_immunizations_from_latest")))));
	}

	/**
	 * Parses {@code case_all_vaccinations} records:
	 * {@code vaccinationDate|reportDate|immunizationId}, separated by {@code #}.
	 *
	 * <p>
	 * Field order and separators are coupled to {@code EpipulseVaccinationSql}. Package-private for
	 * parser-shape testing.
	 */
	static List<EpipulseVaccinationCheckDto> parseVaccinations(String dbVaccinationStr) {

		SimpleDateFormat dbDateFormat = FieldReaders.dbDateFormat();

		return FieldReaders.parseRecords(dbVaccinationStr, 3, vaccinationArr -> {
			String vaccinationDate = vaccinationArr[0];
			String reportDate = vaccinationArr[1];
			String immunizationId = vaccinationArr[2];

			EpipulseVaccinationCheckDto dto = new EpipulseVaccinationCheckDto();

			if (!StringUtils.isBlank(vaccinationDate)) {
				dto.setVaccinationDate(DateHelper.parseDate(vaccinationDate, dbDateFormat));
			}

			if (!StringUtils.isBlank(reportDate)) {
				dto.setReportDate(DateHelper.parseDate(reportDate, dbDateFormat));
			}

			if (!StringUtils.isBlank(immunizationId)) {
				try {
					dto.setImmunizationId(Long.parseLong(immunizationId));
				} catch (NumberFormatException e) {
				}
			}

			return dto;
		});
	}

	/**
	 * Parses {@code case_all_immunizations} records:
	 * {@code id|validFrom|validUntil|numberOfDoses}, separated by {@code #}.
	 *
	 * <p>
	 * The id links immunization courses to parsed doses from {@link #parseVaccinations(String)}.
	 */
	static List<EpipulseImmunizationCheckDto> parseImmunizations(String dbImmunizationStr) {

		SimpleDateFormat dbDateFormat = FieldReaders.dbDateFormat();

		return FieldReaders.parseRecords(dbImmunizationStr, 4, immunizationArr -> {
			String id = immunizationArr[0];
			String validFrom = immunizationArr[1];
			String validUntil = immunizationArr[2];
			String numberOfDoses = immunizationArr[3];

			EpipulseImmunizationCheckDto dto = new EpipulseImmunizationCheckDto();

			if (!StringUtils.isBlank(id)) {
				try {
					dto.setId(Long.parseLong(id));
				} catch (NumberFormatException e) {
				}
			}

			if (!StringUtils.isBlank(validFrom)) {
				dto.setValidFrom(DateHelper.parseDate(validFrom, dbDateFormat));
			}

			if (!StringUtils.isBlank(validUntil)) {
				dto.setValidUntil(DateHelper.parseDate(validUntil, dbDateFormat));
			}

			if (!StringUtils.isBlank(numberOfDoses)) {
				try {
					dto.setNumberOfDoses(Integer.parseInt(numberOfDoses));
				} catch (NumberFormatException e) {
				}
			}

			return dto;
		});
	}
}
