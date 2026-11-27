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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulseImmunizationCheckDto;
import de.symeda.sormas.api.epipulse.EpipulseVaccinationCheckDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
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
 * It loads the patient's doses and immunization records; the rule that turns them into the two
 * columns is in {@code EpipulseMapping}.
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
	 * Parses {@code case_all_vaccinations} records: {@code immunizationId|vaccinationDate},
	 * separated by {@code #}.
	 *
	 * <p>
	 * Field order and separators are coupled to {@code EpipulseVaccinationSql}. Package-private for
	 * parser-shape testing.
	 */
	static List<EpipulseVaccinationCheckDto> parseVaccinations(String dbVaccinationStr) {

		SimpleDateFormat dbDateFormat = FieldReaders.dbDateFormat();

		return FieldReaders.parseRecords(dbVaccinationStr, 2, vaccinationArr -> {
			EpipulseVaccinationCheckDto dto = new EpipulseVaccinationCheckDto();
			dto.setImmunizationId(parseId(vaccinationArr[0]));
			dto.setVaccinationDate(parseDate(vaccinationArr[1], dbDateFormat));

			return dto;
		});
	}

	/**
	 * Parses {@code case_all_immunizations} records:
	 * {@code id|meansOfImmunization|validFrom|validUntil}, separated by {@code #}.
	 */
	static List<EpipulseImmunizationCheckDto> parseImmunizations(String dbImmunizationStr) {

		SimpleDateFormat dbDateFormat = FieldReaders.dbDateFormat();

		return FieldReaders.parseRecords(dbImmunizationStr, 4, immunizationArr -> {
			EpipulseImmunizationCheckDto dto = new EpipulseImmunizationCheckDto();
			dto.setId(parseId(immunizationArr[0]));
			dto.setMeansOfImmunization(FieldReaders.parseEnum(MeansOfImmunization.class, immunizationArr[1]));
			dto.setValidFrom(parseDate(immunizationArr[2], dbDateFormat));
			dto.setValidUntil(parseDate(immunizationArr[3], dbDateFormat));

			return dto;
		});
	}

	private static Long parseId(String value) {
		return StringUtils.isBlank(value) ? null : Long.valueOf(value.trim());
	}

	private static Date parseDate(String value, SimpleDateFormat dbDateFormat) {
		return StringUtils.isBlank(value) ? null : DateHelper.parseDate(value, dbDateFormat);
	}
}
