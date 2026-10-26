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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * Shared field definitions used by multiple disease models.
 *
 * <p>
 * {@link #defs()} returns a fresh mutable list with operational, required, and commonly optional
 * fields. Disease models append their own fields and optional shared blocks.
 */
final class CommonFields {

	private CommonFields() {
	}

	// Shared columns used by multiple diseases.

	static final ValueDef DISEASE = ValueDef.calculated(EpipulseVariable.DISEASE, EpipulseMapping::disease);
	static final ValueDef REPORTING_COUNTRY = ValueDef.calculated(EpipulseVariable.REPORTING_COUNTRY, EpipulseMapping::reportingCountry);
	static final ValueDef STATUS = ValueDef.calculated(EpipulseVariable.STATUS, EpipulseMapping::status);
	static final ValueDef SUBJECT_CODE = ValueDef.calculated(EpipulseVariable.SUBJECT_CODE, EpipulseMapping::subjectCode);
	static final ValueDef NATIONAL_RECORD_ID = ValueDef.calculated(EpipulseVariable.NATIONAL_RECORD_ID, EpipulseMapping::nationalRecordId);
	static final ValueDef DATA_SOURCE = ValueDef.calculated(EpipulseVariable.DATA_SOURCE, EpipulseMapping::dataSource);
	static final ValueDef DATE_USED_FOR_STATISTICS =
		ValueDef.calculated(EpipulseVariable.DATE_USED_FOR_STATISTICS, EpipulseMapping::dateUsedForStatistics);
	static final ValueDef AGE = ValueDef.calculated(EpipulseVariable.AGE, EpipulseMapping::ageInYears);
	static final ValueDef AGE_MONTH = ValueDef.calculated(EpipulseVariable.AGE_MONTH, EpipulseMapping::ageInMonths);
	static final ValueDef GENDER = ValueDef.calculated(EpipulseVariable.GENDER, EpipulseMapping::gender);
	static final ValueDef CASE_CLASSIFICATION = ValueDef.calculated(EpipulseVariable.CASE_CLASSIFICATION, EpipulseMapping::caseClassification);
	static final ValueDef DATE_OF_ONSET = ValueDef.calculated(EpipulseVariable.DATE_OF_ONSET, EpipulseMapping::dateOfOnset);
	static final ValueDef DATE_OF_NOTIFICATION = ValueDef.calculated(EpipulseVariable.DATE_OF_NOTIFICATION, EpipulseMapping::dateOfNotification);
	static final ValueDef HOSPITALISATION = ValueDef.calculated(EpipulseVariable.HOSPITALISATION, EpipulseMapping::hospitalisation);
	static final ValueDef OUTCOME = ValueDef.calculated(EpipulseVariable.OUTCOME, EpipulseMapping::outcome);
	static final ValueDef PLACE_OF_RESIDENCE = ValueDef.calculated(EpipulseVariable.PLACE_OF_RESIDENCE, EpipulseMapping::placeOfResidence);
	static final ValueDef PLACE_OF_NOTIFICATION = ValueDef.calculated(EpipulseVariable.PLACE_OF_NOTIFICATION, EpipulseMapping::placeOfNotification);
	static final ValueDef DATE_OF_LAST_VACCINATION =
		ValueDef.calculated(EpipulseVariable.DATE_OF_LAST_VACCINATION, EpipulseMapping::dateOfLastVaccination);

	/**
	 * Earlier of first positive lab date and doctor diagnosis date.
	 */
	static final ValueDef DATE_OF_DIAGNOSIS = ValueDef.calculated(EpipulseVariable.DATE_OF_DIAGNOSIS, EpipulseMapping::dateOfDiagnosis);

	/**
	 * {@code VaccinationStatus} with a subject-code-specific dose cap.
	 */
	static ValueDef vaccinationStatus(int maxDoses) {

		return ValueDef.calculated(EpipulseVariable.VACCINATION_STATUS, entry -> EpipulseMapping.vaccinationStatus(entry, maxDoses));
	}

	/** The default dose cap used by most subject codes. */
	static final int STANDARD_MAX_DOSES = 10;

	/**
	 * Base field list for disease models.
	 */
	static List<ValueDef> defs() {

		List<ValueDef> fields = new ArrayList<>();

		addOperationalFields(fields);
		addRequiredFields(fields);
		addOptionalFields(fields);

		return fields;
	}

	/**
	 * Operational select columns used to fill DTO internals before CSV rendering.
	 */
	static void addOperationalFields(List<ValueDef> fields) {

		fields.add(
			ValueDef
				.sqlOnly("reporting_country", "cd.reporting_country", (dto, row) -> dto.setReportingCountry((String) row.get("reporting_country"))));

		fields.add(ValueDef.sqlOnly("deleted", "c.deleted", (dto, row) -> dto.setDeleted((Boolean) row.get("deleted"))));

		fields.add(ValueDef.sqlOnly("subject_code", "cd.subject_code", CommonFields::readSubjectCode));

		fields.add(ValueDef.sqlOnly("case_uuid", "c.uuid", (dto, row) -> dto.setNationalRecordId((String) row.get("case_uuid"))));

		fields.add(ValueDef.sqlOnly("datasource", "cd.datasource", (dto, row) -> dto.setDataSource((String) row.get("datasource"))));

		fields.add(
			ValueDef.sqlOnly("case_reportdate", "cast(c.reportdate as date)", (dto, row) -> dto.setReportDate((Date) row.get("case_reportdate"))));

		fields
			.add(ValueDef.sqlOnly("birthdate_yyyy", "person.birthdate_yyyy", (dto, row) -> dto.setYearOfBirth((Integer) row.get("birthdate_yyyy"))));
		fields.add(ValueDef.sqlOnly("birthdate_mm", "person.birthdate_mm", (dto, row) -> dto.setMonthOfBirth((Integer) row.get("birthdate_mm"))));
		fields.add(ValueDef.sqlOnly("birthdate_dd", "person.birthdate_dd", (dto, row) -> dto.setDayOfBirth((Integer) row.get("birthdate_dd"))));

		fields.add(
			ValueDef.sqlOnly(
				"symptom_onsetdate",
				"cast(symptom.onsetdate as date)",
				(dto, row) -> dto.setSymptomOnsetDate((Date) row.get("symptom_onsetdate"))));

		// suppress DateOfOnset without affecting age/date derivations
		fields.add(
			ValueDef.sqlOnly(
				"asymptomatic",
				"symptom.asymptomatic",
				(dto, row) -> dto.setAsymptomatic(FieldReaders.parseSymptomState((String) row.get("asymptomatic")))));

		fields.add(ValueDef.sqlOnly("sex", "person.sex", (dto, row) -> {
			String sex = (String) row.get("sex");
			if (!StringUtils.isBlank(sex)) {
				dto.setSex(Sex.valueOf(sex));
			}
		}));

		fields.add(
			ValueDef.sqlOnly(
				"address_community_nutscode",
				"person_address_community.nutscode",
				(dto, row) -> dto.setAddressCommunityNutsCode((String) row.get("address_community_nutscode"))));
		fields.add(
			ValueDef.sqlOnly(
				"address_district_nutscode",
				"person_address_district.nutscode",
				(dto, row) -> dto.setAddressDistrictNutsCode((String) row.get("address_district_nutscode"))));
		fields.add(
			ValueDef.sqlOnly(
				"address_region_nutscode",
				"person_address_region.nutscode",
				(dto, row) -> dto.setAddressRegionNutsCode((String) row.get("address_region_nutscode"))));

		fields.add(
			ValueDef.sqlOnly(
				"address_country_nutscode",
				"person_address_country.nutscode",
				(dto, row) -> dto.setAddressCountryNutsCode((String) row.get("address_country_nutscode"))));

		fields.add(
			ValueDef.sqlOnly(
				"responsible_community_nutscode",
				"responsible_community.nutscode",
				(dto, row) -> dto.setResponsibleCommunityNutsCode((String) row.get("responsible_community_nutscode"))));
		fields.add(
			ValueDef.sqlOnly(
				"responsible_district_nutscode",
				"responsible_district.nutscode",
				(dto, row) -> dto.setResponsibleDistrictNutsCode((String) row.get("responsible_district_nutscode"))));
		fields.add(
			ValueDef.sqlOnly(
				"responsible_region_nutscode",
				"responsible_region.nutscode",
				(dto, row) -> dto.setResponsibleRegionNutsCode((String) row.get("responsible_region_nutscode"))));

		// Fallback NUTS code used by place fields when no jurisdiction code is available.
		fields.add(ValueDef.contextOnly((dto, row) -> dto.setServerCountryNutsCode(row.context().getServerCountryNutsCode())));

		fields.add(ValueDef.sqlOnly("caseclassification", "c.caseclassification", (dto, row) -> {
			String caseClassification = (String) row.get("caseclassification");
			if (!StringUtils.isBlank(caseClassification)) {
				dto.setCaseClassification(CaseClassification.valueOf(caseClassification));
			}
		}));

		fields.add(ValueDef.sqlOnly("case_outcome", "c.outcome", (dto, row) -> {
			String caseOutcome = (String) row.get("case_outcome");
			if (!StringUtils.isBlank(caseOutcome)) {
				dto.setCaseOutcome(CaseOutcome.valueOf(caseOutcome));
			}
		}));

		// Shared source dates for date/statistics/age derivations.
		fields.add(
			ValueDef.sqlOnly(
				"first_positive_test_date",
				"case_reference_dates.first_positive_test_date",
				(dto, row) -> dto.setFirstPositiveTestDate((Date) row.get("first_positive_test_date"))));
		fields.add(
			ValueDef.sqlOnly(
				"doctor_date_of_diagnosis",
				"case_reference_dates.doctor_date_of_diagnosis",
				(dto, row) -> dto.setDoctorDateOfDiagnosis((Date) row.get("doctor_date_of_diagnosis"))));
		fields.add(
			ValueDef.sqlOnly(
				"first_notification_report_date",
				"case_reference_dates.first_notification_report_date",
				(dto, row) -> dto.setFirstNotificationReportDate((Date) row.get("first_notification_report_date"))));

	}

	/**
	 * Generic CSV field candidates, filtered per subject code by metadata.
	 */
	static List<ValueDef> genericCsvDefs() {

		List<ValueDef> fields = new ArrayList<>();

		addRequiredFields(fields);
		addOptionalFields(fields);
		Collections.addAll(fields, DATE_OF_ONSET, DATE_OF_DIAGNOSIS, HOSPITALISATION, DATE_OF_LAST_VACCINATION);
		// Default model currently uses the standard scale.
		fields.add(vaccinationStatus(STANDARD_MAX_DOSES));

		return fields;
	}

	/**
	 * Fields required for every case-based export.
	 */
	private static void addRequiredFields(List<ValueDef> fields) {

		Collections.addAll(fields, DISEASE, REPORTING_COUNTRY, STATUS, SUBJECT_CODE, NATIONAL_RECORD_ID, DATA_SOURCE, DATE_USED_FOR_STATISTICS);
	}

	/**
	 * Common optional fields currently emitted by all implemented disease models.
	 */
	private static void addOptionalFields(List<ValueDef> fields) {

		Collections
			.addAll(fields, AGE, AGE_MONTH, GENDER, CASE_CLASSIFICATION, DATE_OF_NOTIFICATION, PLACE_OF_RESIDENCE, PLACE_OF_NOTIFICATION, OUTCOME);
	}

	private static void readSubjectCode(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		String subjectCodeFromDb = (String) row.get("subject_code");
		if (StringUtils.isBlank(subjectCodeFromDb)) {
			throw new IllegalStateException("Subject code is missing for Epipulse export row");
		}

		dto.setSubjectCode(EpipulseSubjectCode.valueOf(subjectCodeFromDb));
	}
}
