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

import de.symeda.sormas.api.epipulse.EpipulseHospitalizationCheckDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;

/**
 * Provides the five columns derived from {@link CommonFields#HOSPITALISATION}:
 * current stay and aggregate of previous hospitalizations.
 * 
 * Opt-in rather than unconditional: EpiPulse declares {@code Hospitalisation}
 * for 19 of 37 subject codes, not all. Callers must also merge
 * {@link EpipulseHospitalisationSql#spec()} into their query model;
 * these columns read aliases only that fragment provides.
 */
final class HospitalisationFields {

	private HospitalisationFields() {
	}

	/**
	 * Adds five hospitalisation columns: current stay and previous stays aggregate.
	 * 
	 * Opt-in: EpiPulse declares this for only 19 of 37 subject codes.
	 * Callers must merge {@link EpipulseHospitalisationSql#spec()} into their query model;
	 * omitting either causes a PostgreSQL error on first query.
	 */
	static void addHospitalisationFields(List<ValueDef> fields) {

		fields.add(ValueDef.sqlOnly("admittedtohealthfacility", "hospitalization.admittedtohealthfacility", (dto, row) -> {
			String admitted = (String) row.get("admittedtohealthfacility");
			if (!StringUtils.isBlank(admitted)) {
				dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admitted));
			}
		}));

		fields.add(ValueDef.sqlOnly("hospitalizationreason", "hospitalization.hospitalizationreason", (dto, row) -> {
			String reason = (String) row.get("hospitalizationreason");
			if (!StringUtils.isBlank(reason)) {
				dto.setHospitalizationReason(HospitalizationReasonType.valueOf(reason));
			}
		}));

		fields.add(
			ValueDef.sqlOnly(
				"admissiondate",
				"cast(hospitalization.admissiondate as date)",
				(dto, row) -> dto.setAdmissionDate((Date) row.get("admissiondate"))));
		fields.add(
			ValueDef.sqlOnly(
				"dischargedate",
				"cast(hospitalization.dischargedate as date)",
				(dto, row) -> dto.setDischargeDate((Date) row.get("dischargedate"))));

		fields.add(
			ValueDef.sqlOnly(
				"all_prev_hsp_from_latest",
				"case_all_prev_hsp_from_latest.all_prev_hsp_from_latest",
				(dto, row) -> dto.setPreviousHospitalizations(parsePreviousHospitalizationChecks((String) row.get("all_prev_hsp_from_latest")))));
	}

	/**
	 * Unflattens the aggregate from {@code EpipulseHospitalisationSql}:
	 * format is {@code admitted|reason|admissionDate|dischargeDate}, records separated by {@code #}.
	 * 
	 * Field order is a contract with that CTE only.
	 * A stay without a discharge date is skipped on purpose: it does not count as a previous
	 * hospitalisation.
	 */
	private static List<EpipulseHospitalizationCheckDto> parsePreviousHospitalizationChecks(String dbPreviousHospitalizationStr) {

		SimpleDateFormat dbDateFormat = FieldReaders.dbDateFormat();

		return FieldReaders.parseRecords(dbPreviousHospitalizationStr, 4, hospitalizationArr -> {
			String admittedToHealthFacilityStr = hospitalizationArr[0];
			String hospitalizationReasonStr = hospitalizationArr[1];
			String admissionDateStr = hospitalizationArr[2];
			String dischargeDateStr = hospitalizationArr[3];

			if (StringUtils.isBlank(dischargeDateStr)) {
				return null;
			}

			EpipulseHospitalizationCheckDto dto = new EpipulseHospitalizationCheckDto();

			if (!StringUtils.isBlank(admittedToHealthFacilityStr)) {
				dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacilityStr));
			}

			if (!StringUtils.isBlank(hospitalizationReasonStr)) {
				dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReasonStr));
			}

			if (!StringUtils.isBlank(admissionDateStr)) {
				dto.setAdmissionDate(DateHelper.parseDate(admissionDateStr, dbDateFormat));
			}

			dto.setDischargeDate(DateHelper.parseDate(dischargeDateStr, dbDateFormat));

			return dto;
		});
	}
}
