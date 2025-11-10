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

package de.symeda.sormas.api.epipulse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseOutcomeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDiseaseRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseGenderRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseVaccinationStatusRef;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpipulseDiseaseExportEntryDto {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String COLLECTION_SPLIT_CHARACTER = "#";
	private static final String RECORD_SPLIT_CHARACTER = "\\|";
	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String reportingCountry;
	private Boolean deleted;
	private EpipulseSubjectCode subjectCode;
	private String nationalRecordId;
	private String dataSource;
	private Date reportDate;
	private Integer yearOfBirth;
	private Integer monthOfBirth;
	private Integer dayOfBirth;
	private Date symptomOnsetDate;
	private Integer ageYears;
	private Integer ageMonths;
	private Integer ageDays;
	private Sex sex;
	private String addressCommunityNutsCode;
	private String addressDistrictNutsCode;
	private String addressRegionNutsCode;
	private String addressCountryNutsCode;
	private String responsibleCommunityNutsCode;
	private String responsibleDistrictNutsCode;
	private String responsibleRegionNutsCode;
	private String serverCountryNutsCode;
	private CaseClassification caseClassification;
	private YesNoUnknown admittedToHealthFacility;
	private HospitalizationReasonType hospitalizationReason;
	private Date admissionDate;
	private Date dischargeDate;
	private CaseOutcome caseOutcome;
	private List<EpipulseHospitalizationCheckDto> previousHospitalizations;
	private List<EpipulsePathogentTestCheckDto> pathogenTests;
	private List<EpipulseImmunizationCheckDto> immunizations;
	private List<EpipulseVaccinationCheckDto> vaccinations;

	public String getReportingCountry() {
		return reportingCountry;
	}

	public void setReportingCountry(String reportingCountry) {
		this.reportingCountry = reportingCountry;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public EpipulseSubjectCode getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(EpipulseSubjectCode subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getNationalRecordId() {
		return nationalRecordId;
	}

	public void setNationalRecordId(String nationalRecordId) {
		this.nationalRecordId = nationalRecordId;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public Integer getMonthOfBirth() {
		return monthOfBirth;
	}

	public void setMonthOfBirth(Integer monthOfBirth) {
		this.monthOfBirth = monthOfBirth;
	}

	public Integer getDayOfBirth() {
		return dayOfBirth;
	}

	public void setDayOfBirth(Integer dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
	}

	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public void setSymptomOnsetDate(Date symptomOnsetDate) {
		this.symptomOnsetDate = symptomOnsetDate;
	}

	public Integer getAgeYears() {
		return ageYears;
	}

	public Integer getAgeMonths() {
		return ageMonths;
	}

	public Integer getAgeDays() {
		return ageDays;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getAddressCommunityNutsCode() {
		return addressCommunityNutsCode;
	}

	public void setAddressCommunityNutsCode(String addressCommunityNutsCode) {
		this.addressCommunityNutsCode = addressCommunityNutsCode;
	}

	public String getAddressDistrictNutsCode() {
		return addressDistrictNutsCode;
	}

	public void setAddressDistrictNutsCode(String addressDistrictNutsCode) {
		this.addressDistrictNutsCode = addressDistrictNutsCode;
	}

	public String getAddressRegionNutsCode() {
		return addressRegionNutsCode;
	}

	public void setAddressRegionNutsCode(String addressRegionNutsCode) {
		this.addressRegionNutsCode = addressRegionNutsCode;
	}

	public String getAddressCountryNutsCode() {
		return addressCountryNutsCode;
	}

	public void setAddressCountryNutsCode(String addressCountryNutsCode) {
		this.addressCountryNutsCode = addressCountryNutsCode;
	}

	public String getResponsibleCommunityNutsCode() {
		return responsibleCommunityNutsCode;
	}

	public void setResponsibleCommunityNutsCode(String responsibleCommunityNutsCode) {
		this.responsibleCommunityNutsCode = responsibleCommunityNutsCode;
	}

	public String getResponsibleDistrictNutsCode() {
		return responsibleDistrictNutsCode;
	}

	public void setResponsibleDistrictNutsCode(String responsibleDistrictNutsCode) {
		this.responsibleDistrictNutsCode = responsibleDistrictNutsCode;
	}

	public String getResponsibleRegionNutsCode() {
		return responsibleRegionNutsCode;
	}

	public void setResponsibleRegionNutsCode(String responsibleRegionNutsCode) {
		this.responsibleRegionNutsCode = responsibleRegionNutsCode;
	}

	public String getServerCountryNutsCode() {
		return serverCountryNutsCode;
	}

	public void setServerCountryNutsCode(String serverCountryNutsCode) {
		this.serverCountryNutsCode = serverCountryNutsCode;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType hospitalizationReason) {
		this.hospitalizationReason = hospitalizationReason;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public CaseOutcome getCaseOutcome() {
		return caseOutcome;
	}

	public void setCaseOutcome(CaseOutcome caseOutcome) {
		this.caseOutcome = caseOutcome;
	}

	public List<EpipulseHospitalizationCheckDto> getPreviousHospitalizations() {
		return previousHospitalizations;
	}

	public void setPreviousHospitalizations(List<EpipulseHospitalizationCheckDto> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}

	public List<EpipulsePathogentTestCheckDto> getPathogenTests() {
		return pathogenTests;
	}

	public void setPathogenTests(List<EpipulsePathogentTestCheckDto> pathogenTests) {
		this.pathogenTests = pathogenTests;
	}

	public List<EpipulseImmunizationCheckDto> getImmunizations() {
		return immunizations;
	}

	public void setImmunizations(List<EpipulseImmunizationCheckDto> immunizations) {
		this.immunizations = immunizations;
	}

	public List<EpipulseVaccinationCheckDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<EpipulseVaccinationCheckDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public String getDiseaseForCsv() {
		return EpipulseDiseaseRef.getBySubjectCode(subjectCode).name();
	}

	public String getReportingCountryForCsv() {
		return reportingCountry;
	}

	public String getStatusForCsv() {
		if (deleted == Boolean.FALSE) {
			return EpipulseStatusRef.NEW_UPDATE.getCode();
		} else {
			return EpipulseStatusRef.DELETE.getCode();
		}
	}

	public String getSubjectCodeForCsv() {
		return subjectCode.name();
	}

	public String getNationalRecordIdForCsv() {
		return nationalRecordId;
	}

	public String getDataSourceForCsv() {
		return dataSource;
	}

	public String getDateUsedForStatisticsCsv() {
		return formatDateForCsv(reportDate);
	}

	public String getAgeForCsv() {
		return ageYears == null ? null : ageYears.toString();
	}

	public String getAgeMonthForCsv() {
		switch (subjectCode) {
		case PERT:
			if (ageYears != null && ageYears < 2) {
				return ageMonths == null ? null : ageMonths.toString();
			}
			break;
		}
		return null;
	}

	public String getGenderForCsv() {
		EpipulseGenderRef genderRef = EpipulseGenderRef.getByGender(sex);
		if (genderRef != null) {
			return genderRef.name();
		}
		return null;
	}

	public String getPlaceOfResidenceForCsv() {
		switch (subjectCode) {
		case PERT:
			if (addressCommunityNutsCode != null && !addressCommunityNutsCode.isEmpty()) {
				return addressCommunityNutsCode;
			} else if (addressDistrictNutsCode != null && !addressDistrictNutsCode.isEmpty()) {
				return addressDistrictNutsCode;
			} else if (addressRegionNutsCode != null && !addressRegionNutsCode.isEmpty()) {
				return addressRegionNutsCode;
			} else {
				return serverCountryNutsCode;
			}
		}
		return null;
	}

	public String getPlaceOfNotificationForCsv() {
		switch (subjectCode) {
		case PERT:
			if (responsibleCommunityNutsCode != null && !responsibleCommunityNutsCode.isEmpty()) {
				return responsibleCommunityNutsCode;
			} else if (responsibleDistrictNutsCode != null && !responsibleDistrictNutsCode.isEmpty()) {
				return responsibleDistrictNutsCode;
			} else if (responsibleRegionNutsCode != null && !responsibleRegionNutsCode.isEmpty()) {
				return responsibleRegionNutsCode;
			} else {
				return serverCountryNutsCode;
			}
		}
		return null;
	}

	public String getCaseClassificationForCsv() {
		EpipulseCaseClassificationRef classificationRef = EpipulseCaseClassificationRef.getByCaseClassification(caseClassification);
		if (classificationRef != null) {
			return classificationRef.name();
		}
		return null;
	}

	public String getDateOfOnsetForCsv() {
		return formatDateForCsv(symptomOnsetDate);
	}

	public String getDateOfNotificationForCsv() {
		return formatDateForCsv(reportDate);
	}

	public String getHospitalizationForCsv() {
		return hasHistoryOfHospitalization();
	}

	public String getOutcomeForCsv() {
		EpipulseCaseOutcomeRef outcomeRef = EpipulseCaseOutcomeRef.getByCaseOutcome(caseOutcome);
		if (outcomeRef != null) {
			return outcomeRef.name();
		}
		return null;
	}

	public List<String> getPathogenDetectionMethodsForCsv(int maxPathogenTests) {
		List<String> pathogenDetectionMethods = new ArrayList<>();

		for (int i = 0; i < maxPathogenTests; i++) {
			if (i < pathogenTests.size()) {
				pathogenDetectionMethods.add(EpipulsePathogenTestTypeRef.getByPathogenTestType(pathogenTests.get(i).getTestType()).name());
			} else {
				pathogenDetectionMethods.add("");
			}
		}

		return pathogenDetectionMethods;
	}

	public String getDateOfLastVaccinationForCsv() {

		if (!vaccinations.isEmpty()) {
			for (EpipulseVaccinationCheckDto vaccinationCheckDto : vaccinations) {
				if (vaccinationCheckDto.getVaccinationDate() != null && symptomOnsetDate != null) {
					if (DateHelper.isDateAfter(symptomOnsetDate, vaccinationCheckDto.getVaccinationDate())) {
						return formatDateForCsv(vaccinationCheckDto.getVaccinationDate());
					}
				}
			}
		} else {
			for (EpipulseImmunizationCheckDto immunizationCheckDto : immunizations) {
				if (immunizationCheckDto.getEndDate() != null && symptomOnsetDate != null) {
					if (DateHelper.isDateAfter(symptomOnsetDate, immunizationCheckDto.getEndDate())) {
						return formatDateForCsv(immunizationCheckDto.getEndDate());
					}
				}
			}
		}

		return null;
	}

	public String getVaccinationStatusForCsv() {

		if (immunizations.isEmpty()) {
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		int totalDoses = 0;
		for (EpipulseImmunizationCheckDto immunizationCheckDto : immunizations) {
			if (immunizationCheckDto.getNumberOfDoses() == null) {
				return EpipulseVaccinationStatusRef.UNKDOSE.getCode();
			} else {
				totalDoses += immunizationCheckDto.getNumberOfDoses();
			}
		}

		if (totalDoses > 10) {
			return EpipulseVaccinationStatusRef.TEN_DOSE.getCode();
		}

		switch (totalDoses) {
		case 1:
			return EpipulseVaccinationStatusRef.ONE_DOSE.getCode();
		case 2:
			return EpipulseVaccinationStatusRef.TWO_DOSE.getCode();
		case 3:
			return EpipulseVaccinationStatusRef.THREE_DOSE.getCode();
		case 4:
			return EpipulseVaccinationStatusRef.FOUR_DOSE.getCode();
		case 5:
			return EpipulseVaccinationStatusRef.FIVE_DOSE.getCode();
		case 6:
			return EpipulseVaccinationStatusRef.SIX_DOSE.getCode();
		case 7:
			return EpipulseVaccinationStatusRef.SEVEN_DOSE.getCode();
		case 8:
			return EpipulseVaccinationStatusRef.EIGHT_DOSE.getCode();
		case 9:
			return EpipulseVaccinationStatusRef.NINE_DOSE.getCode();
		}

		return null;
	}

	public String getVaccinationStatusMaternalForCsv() {
		return null;
	}

	public String getGestationalAgeAtVaccinationForCsv() {
		return null;
	}

	public void calculateAge() {
		if (symptomOnsetDate == null || yearOfBirth == null || monthOfBirth == null || dayOfBirth == null) {
			return;
		}

		try {
			Date birthDate = DateHelper.getDateZero(yearOfBirth, monthOfBirth - 1, dayOfBirth);

			ageYears = DateHelper.getYearsBetween(birthDate, symptomOnsetDate);
			ageMonths = DateHelper.getMonthsBetween(birthDate, symptomOnsetDate);
			ageDays = DateHelper.getFullDaysBetween(birthDate, symptomOnsetDate);
		} catch (Exception e) {
			ageYears = null;
			ageMonths = null;
			ageDays = null;
		}
	}

	private String hasHistoryOfHospitalization() {
		boolean hospitalized = false;

		if (admittedToHealthFacility == YesNoUnknown.YES
			&& hospitalizationReason == HospitalizationReasonType.REPORTED_DISEASE
			&& DateHelper.getDaysBetween(admissionDate, dischargeDate) > 1) {
			hospitalized = true;
		} else {
			for (EpipulseHospitalizationCheckDto dto : previousHospitalizations) {
				if (dto.getAdmittedToHealthFacility() == YesNoUnknown.YES
					&& dto.getHospitalizationReason() == HospitalizationReasonType.REPORTED_DISEASE
					&& DateHelper.getDaysBetween(dto.getAdmissionDate(), dto.getDischargeDate()) > 1) {
					hospitalized = true;
					break;
				}
			}
		}

		return String.valueOf(hospitalized);
	}

	private boolean hasPathogenTest() {
		return (pathogenTests != null && !pathogenTests.isEmpty());
	}

	public static String formatDateForCsv(Date date) {

		if (date != null) {
			return DB_DATE_FORMAT.format(date);
		} else {
			return "";
		}
	}

	public List<EpipulseHospitalizationCheckDto> parsePreviousHospitalizationChecks(String dbPreviousHospitalizationStr) {
		if (StringUtils.isBlank(dbPreviousHospitalizationStr)) {
			return new ArrayList<>();
		}

		String[] allHospitalizationArr = dbPreviousHospitalizationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allHospitalizationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseHospitalizationCheckDto> previousHospitalizations = new ArrayList<>();
		EpipulseHospitalizationCheckDto dto = null;
		for (String hospitalizationStr : allHospitalizationArr) {
			String[] hospitalizationArr = hospitalizationStr.split(RECORD_SPLIT_CHARACTER);
			if (hospitalizationArr.length < 4) {
				continue;
			}
			String admittedToHealthFacilityStr = hospitalizationArr[0];
			String hospitalizationReasonStr = hospitalizationArr[1];
			String admissionDateStr = hospitalizationArr[2];
			String dischargeDateStr = hospitalizationArr[3];

			dto = new EpipulseHospitalizationCheckDto();

			if (!StringUtils.isBlank(admittedToHealthFacilityStr)) {
				dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacilityStr));
			}

			if (!StringUtils.isBlank(hospitalizationReasonStr)) {
				dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReasonStr));
			}

			if (!StringUtils.isBlank(admissionDateStr)) {
				dto.setAdmissionDate(DateHelper.parseDate(admissionDateStr, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(dischargeDateStr)) {
				dto.setDischargeDate(DateHelper.parseDate(dischargeDateStr, DB_DATE_FORMAT));
			}

			previousHospitalizations.add(dto);
		}

		return previousHospitalizations;
	}

	public List<EpipulsePathogentTestCheckDto> parsePathogenTestChecks(
		String dbPathogenTestStr,
		List<PathogenTestType> subjectCodePathogenTestTypes) {

		if (StringUtils.isBlank(dbPathogenTestStr)) {
			return new ArrayList<>();
		}

		String[] allPathogenTestArr = dbPathogenTestStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allPathogenTestArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulsePathogentTestCheckDto> pathogenTests = new ArrayList<>();
		EpipulsePathogentTestCheckDto dto = null;
		for (String pathogenTestStr : allPathogenTestArr) {
			String[] pathogenTestArr = pathogenTestStr.split(RECORD_SPLIT_CHARACTER);
			if (pathogenTestArr.length < 2) {
				continue;
			}
			String testTypeStr = pathogenTestArr[0];
			String testResultStr = pathogenTestArr[1];

			PathogenTestType testType = null;
			if (!StringUtils.isBlank(testTypeStr)) {
				testType = PathogenTestType.valueOf(testTypeStr);
			}

			PathogenTestResultType testResultType = null;
			if (!StringUtils.isBlank(testResultStr)) {
				testResultType = PathogenTestResultType.valueOf(testResultStr);
			}

			if (!subjectCodePathogenTestTypes.contains(testType) || testResultType != PathogenTestResultType.POSITIVE) {
				continue;
			}

			dto = new EpipulsePathogentTestCheckDto();
			dto.setTestType(testType);
			dto.setTestResult(testResultType);

			pathogenTests.add(dto);
		}

		return pathogenTests;
	}

	public List<EpipulseImmunizationCheckDto> parseImmunizationChecks(String dbPreviousImmunizationStr) {
		if (StringUtils.isBlank(dbPreviousImmunizationStr)) {
			return new ArrayList<>();
		}

		String[] allImmunizationArr = dbPreviousImmunizationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allImmunizationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseImmunizationCheckDto> immunizations = new ArrayList<>();
		EpipulseImmunizationCheckDto dto = null;
		for (String immunizationStr : allImmunizationArr) {
			String[] immunizationArr = immunizationStr.split(RECORD_SPLIT_CHARACTER);
			if (immunizationArr.length < 4) {
				continue;
			}
			String startDate = immunizationArr[0];
			String endDate = immunizationArr[1];
			String meansOfImmunization = immunizationArr[2];
			String numberOfDoses = immunizationArr[3];

			dto = new EpipulseImmunizationCheckDto();

			if (!StringUtils.isBlank(startDate)) {
				dto.setStartDate(DateHelper.parseDate(startDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(endDate)) {
				dto.setEndDate(DateHelper.parseDate(endDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(meansOfImmunization)) {
				dto.setMeansOfImmunization(MeansOfImmunization.valueOf(meansOfImmunization));
			}

			if (!StringUtils.isBlank(numberOfDoses)) {
				dto.setNumberOfDoses(Integer.parseInt(numberOfDoses));
			}

			immunizations.add(dto);
		}

		return immunizations;
	}

	public List<EpipulseVaccinationCheckDto> parseVaccinations(String dbVaccinationStr) {
		if (StringUtils.isBlank(dbVaccinationStr)) {
			return new ArrayList<>();
		}

		String[] allVaccinationArr = dbVaccinationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allVaccinationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseVaccinationCheckDto> vaccinations = new ArrayList<>();
		EpipulseVaccinationCheckDto dto = null;
		for (String vaccinationStr : allVaccinationArr) {
			String[] vaccinationArr = vaccinationStr.split(RECORD_SPLIT_CHARACTER);
			if (vaccinationArr.length < 2) {
				continue;
			}
			String vaccinationDate = vaccinationArr[0];
			String vaccineDose = vaccinationArr[1];

			dto = new EpipulseVaccinationCheckDto();

			if (!StringUtils.isBlank(vaccinationDate)) {
				dto.setVaccinationDate(DateHelper.parseDate(vaccinationDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(vaccineDose)) {
				try {
					dto.setVaccineDose(Integer.parseInt(vaccineDose));
				} catch (NumberFormatException e) {
				}
			}

			vaccinations.add(dto);
		}

		return vaccinations;
	}
}
