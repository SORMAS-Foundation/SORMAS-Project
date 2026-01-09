/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.epipulse;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Static utility class for mapping common DTO fields that are identical across all disease exports.
 * This mapper handles the first fields (indices 0-27) that are shared between Pertussis, Measles,
 * and other disease exports.
 */
public class EpipulseCommonDtoMapper {

	private static final Logger logger = LoggerFactory.getLogger(EpipulseCommonDtoMapper.class);

	private EpipulseCommonDtoMapper() {
		// Utility class - prevent instantiation
	}

	/**
	 * Maps common fields that are identical for all disease exports.
	 * This method handles the standard case data, demographics, location, hospitalization,
	 * and outcome information that is common to all diseases.
	 *
	 * @param dto
	 *            the DTO to populate
	 * @param row
	 *            the database result row
	 * @param serverCountryNutsCode
	 *            the NUTS code for the server country
	 * @param subjectCodePathogenTestTypes
	 *            the pathogen test types for the disease
	 * @return the next index position for disease-specific fields
	 */
	public static int mapCommonFields(
		EpipulseDiseaseExportEntryDto dto,
		Object[] row,
		String serverCountryNutsCode,
		List<PathogenTestType> subjectCodePathogenTestTypes) {

		int index = -1;

		// Index 0: Reporting Country
		dto.setReportingCountry((String) row[++index]);

		// Index 1: Deleted flag
		dto.setDeleted((Boolean) row[++index]);

		// Index 2: Subject Code
		String subjectCodeFromDb = (String) row[++index];
		if (!StringUtils.isBlank(subjectCodeFromDb)) {
			dto.setSubjectCode(EpipulseSubjectCode.valueOf(subjectCodeFromDb));
		}

		// Index 3: National Record ID (case UUID)
		dto.setNationalRecordId((String) row[++index]);

		// Index 4: Data Source
		dto.setDataSource((String) row[++index]);

		// Index 5: Report Date
		dto.setReportDate((Date) row[++index]);

		// Index 6-8: Birth Date Components
		dto.setYearOfBirth((Integer) row[++index]);
		dto.setMonthOfBirth((Integer) row[++index]);
		dto.setDayOfBirth((Integer) row[++index]);

		// Index 9: Symptom Onset Date
		dto.setSymptomOnsetDate((Date) row[++index]);

		// Index 10: Sex
		String sex = (String) row[++index];
		if (!StringUtils.isBlank(sex)) {
			dto.setSex(Sex.valueOf(sex));
		}

		// Index 11-14: Address NUTS Codes
		dto.setAddressCommunityNutsCode((String) row[++index]);
		dto.setAddressDistrictNutsCode((String) row[++index]);
		dto.setAddressRegionNutsCode((String) row[++index]);
		dto.setAddressCountryNutsCode((String) row[++index]);

		// Index 15-17: Responsible Area NUTS Codes
		dto.setResponsibleCommunityNutsCode((String) row[++index]);
		dto.setResponsibleDistrictNutsCode((String) row[++index]);
		dto.setResponsibleRegionNutsCode((String) row[++index]);

		// Server Country NUTS Code (not from row, passed as parameter)
		dto.setServerCountryNutsCode(serverCountryNutsCode);

		// Index 18: Case Classification
		String caseClassification = (String) row[++index];
		if (!StringUtils.isBlank(caseClassification)) {
			dto.setCaseClassification(CaseClassification.valueOf(caseClassification));
		}

		// Index 19: Admitted to Health Facility
		String admittedToHealthFacility = (String) row[++index];
		if (!StringUtils.isBlank(admittedToHealthFacility)) {
			dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacility));
		}

		// Index 20: Hospitalization Reason
		String hospitalizationReason = (String) row[++index];
		if (!StringUtils.isBlank(hospitalizationReason)) {
			dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReason));
		}

		// Index 21-22: Admission and Discharge Dates
		dto.setAdmissionDate((Date) row[++index]);
		dto.setDischargeDate((Date) row[++index]);

		// Index 23: Case Outcome
		String caseOutcome = (String) row[++index];
		if (!StringUtils.isBlank(caseOutcome)) {
			dto.setCaseOutcome(CaseOutcome.valueOf(caseOutcome));
		}

		// Index 24-27: Aggregated Collections (previous hospitalizations, pathogen tests, immunizations, vaccinations)
		dto.setPreviousHospitalizations(dto.parsePreviousHospitalizationChecks((String) row[++index]));
		dto.setPathogenTests(dto.parsePathogenTestChecks((String) row[++index], subjectCodePathogenTestTypes));
		dto.setImmunizations(dto.parseImmunizationChecks((String) row[++index]));
		dto.setVaccinations(dto.parseVaccinations((String) row[++index]));

		// Return the last index used (27 for the vaccinations field)
		// The aggregated collections (previous hospitalizations, pathogen tests, immunizations, vaccinations)
		// are also common fields
		return index;
	}

	/**
	 * Calculates common max counts (pathogenTests and immunizations) that are tracked
	 * across all disease exports. Disease-specific max counts are handled by the
	 * individual strategy implementations.
	 *
	 * @param entries
	 *            the list of export entries
	 * @param result
	 *            the result object to populate with max counts
	 */
	public static void calculateCommonMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {

		int maxPathogenTests = 0;
		int maxImmunizations = 0;

		for (EpipulseDiseaseExportEntryDto entry : entries) {
			int pathogenTestCount = entry.getPathogenTests().size();
			if (pathogenTestCount > maxPathogenTests) {
				maxPathogenTests = pathogenTestCount;
			}

			int immunizationCount = entry.getImmunizations().size();
			if (immunizationCount > maxImmunizations) {
				maxImmunizations = immunizationCount;
			}
		}

		result.setMaxPathogenTests(maxPathogenTests);
		result.setMaxImmunizations(maxImmunizations);
	}
}
