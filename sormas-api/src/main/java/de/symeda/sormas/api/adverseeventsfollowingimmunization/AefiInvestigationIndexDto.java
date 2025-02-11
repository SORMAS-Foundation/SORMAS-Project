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

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class AefiInvestigationIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	private static final long serialVersionUID = 5659752736289073666L;

	public static final String I18N_PREFIX = "AefiInvestigationIndex";

	public static final String UUID = "uuid";
	public static final String AEFI_REPORT_UUID = "aefiReportUuid";
	public static final String INVESTIGATION_CASE_ID = "investigationCaseId";
	public static final String DISEASE = "disease";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String PLACE_OF_VACCINATION = "placeOfVaccination";
	public static final String VACCINATION_ACTIVITY = "vaccinationActivity";
	public static final String ADVERSE_EVENT_REPORT_DATE = "adverseEventReportDate";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATION_DATE = "investigationDate";
	public static final String INVESTIGATION_STAGE = "investigationStage";
	public static final String TYPE_OF_SITE = "typeOfSite";
	public static final String KEY_SYMPTOM_DATE_TIME = "keySymptomDateTime";
	public static final String HOSPITALIZATION_DATE = "hospitalizationDate";
	public static final String REPORTED_TO_HEALTH_AUTHORITY_DATE = "reportedToHealthAuthorityDate";
	public static final String STATUS_ON_DATE_OF_INVESTIGATION = "statusOnDateOfInvestigation";
	public static final String PRIMARY_VACCINE_NAME = "primaryVaccine";
	public static final String PRIMARY_VACCINE_DETAILS = "primaryVaccineDetails";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String AEFI_CLASSIFICATION = "aefiClassification";
	public static final String DELETION_REASON = "deletionReason";

	private String aefiReportUuid;
	private String investigationCaseId;
	private Disease disease;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	private String region;
	private String district;
	private PlaceOfVaccination placeOfVaccination;
	private VaccinationActivity vaccinationActivity;
	private Date aefiReportDate;
	private Date reportDate;
	private Date investigationDate;
	private AefiInvestigationStage investigationStage;
	private VaccinationSite typeOfSite;
	private Date keySymptomDateTime;
	private Date hospitalizationDate;
	private Date reportedToHealthAuthorityDate;
	private PatientStatusAtAefiInvestigation statusOnDateOfInvestigation;
	private Vaccine primaryVaccine;
	private String primaryVaccineDetails;
	private AefiInvestigationStatus investigationStatus;
	private AefiClassification aefiClassification;
	private DeletionReason deletionReason;
	private String otherDeletionReason;
	private boolean isInJurisdiction;

	public AefiInvestigationIndexDto(
		String uuid,
		String aefiReportUuid,
		String investigationCaseId,
		Disease disease,
		String personFirstName,
		String personLastName,
		AgeAndBirthDateDto ageAndBirthDate,
		Sex sex,
		String region,
		String district,
		PlaceOfVaccination placeOfVaccination,
		VaccinationActivity vaccinationActivity,
		Date aefiReportDate,
		Date reportDate,
		Date investigationDate,
		AefiInvestigationStage investigationStage,
		VaccinationSite typeOfSite,
		Date keySymptomDateTime,
		Date hospitalizationDate,
		Date reportedToHealthAuthorityDate,
		PatientStatusAtAefiInvestigation statusOnDateOfInvestigation,
		Vaccine primaryVaccine,
		String primaryVaccineDetails,
		AefiInvestigationStatus investigationStatus,
		AefiClassification aefiClassification,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean isInJurisdiction) {

		super(uuid);
		this.aefiReportUuid = aefiReportUuid;
		this.investigationCaseId = investigationCaseId;
		this.disease = disease;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.ageAndBirthDate = ageAndBirthDate;
		this.sex = sex;
		this.region = region;
		this.district = district;
		this.placeOfVaccination = placeOfVaccination;
		this.vaccinationActivity = vaccinationActivity;
		this.aefiReportDate = aefiReportDate;
		this.reportDate = reportDate;
		this.investigationDate = investigationDate;
		this.investigationStage = investigationStage;
		this.typeOfSite = typeOfSite;
		this.keySymptomDateTime = keySymptomDateTime;
		this.hospitalizationDate = hospitalizationDate;
		this.reportedToHealthAuthorityDate = reportedToHealthAuthorityDate;
		this.statusOnDateOfInvestigation = statusOnDateOfInvestigation;
		this.primaryVaccine = primaryVaccine;
		this.primaryVaccineDetails = primaryVaccineDetails;
		this.investigationStatus = investigationStatus;
		this.aefiClassification = aefiClassification;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getAefiReportUuid() {
		return aefiReportUuid;
	}

	public void setAefiReportUuid(String aefiReportUuid) {
		this.aefiReportUuid = aefiReportUuid;
	}

	public String getInvestigationCaseId() {
		return investigationCaseId;
	}

	public void setInvestigationCaseId(String investigationCaseId) {
		this.investigationCaseId = investigationCaseId;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public PlaceOfVaccination getPlaceOfVaccination() {
		return placeOfVaccination;
	}

	public void setPlaceOfVaccination(PlaceOfVaccination placeOfVaccination) {
		this.placeOfVaccination = placeOfVaccination;
	}

	public VaccinationActivity getVaccinationActivity() {
		return vaccinationActivity;
	}

	public void setVaccinationActivity(VaccinationActivity vaccinationActivity) {
		this.vaccinationActivity = vaccinationActivity;
	}

	public Date getAefiReportDate() {
		return aefiReportDate;
	}

	public void setAefiReportDate(Date aefiReportDate) {
		this.aefiReportDate = aefiReportDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getInvestigationDate() {
		return investigationDate;
	}

	public void setInvestigationDate(Date investigationDate) {
		this.investigationDate = investigationDate;
	}

	public AefiInvestigationStage getInvestigationStage() {
		return investigationStage;
	}

	public void setInvestigationStage(AefiInvestigationStage investigationStage) {
		this.investigationStage = investigationStage;
	}

	public VaccinationSite getTypeOfSite() {
		return typeOfSite;
	}

	public void setTypeOfSite(VaccinationSite typeOfSite) {
		this.typeOfSite = typeOfSite;
	}

	public Date getKeySymptomDateTime() {
		return keySymptomDateTime;
	}

	public void setKeySymptomDateTime(Date keySymptomDateTime) {
		this.keySymptomDateTime = keySymptomDateTime;
	}

	public Date getHospitalizationDate() {
		return hospitalizationDate;
	}

	public void setHospitalizationDate(Date hospitalizationDate) {
		this.hospitalizationDate = hospitalizationDate;
	}

	public Date getReportedToHealthAuthorityDate() {
		return reportedToHealthAuthorityDate;
	}

	public void setReportedToHealthAuthorityDate(Date reportedToHealthAuthorityDate) {
		this.reportedToHealthAuthorityDate = reportedToHealthAuthorityDate;
	}

	public PatientStatusAtAefiInvestigation getStatusOnDateOfInvestigation() {
		return statusOnDateOfInvestigation;
	}

	public void setStatusOnDateOfInvestigation(PatientStatusAtAefiInvestigation statusOnDateOfInvestigation) {
		this.statusOnDateOfInvestigation = statusOnDateOfInvestigation;
	}

	public Vaccine getPrimaryVaccine() {
		return primaryVaccine;
	}

	public void setPrimaryVaccine(Vaccine primaryVaccine) {
		this.primaryVaccine = primaryVaccine;
	}

	public String getPrimaryVaccineDetails() {
		return primaryVaccineDetails;
	}

	public void setPrimaryVaccineDetails(String primaryVaccineDetails) {
		this.primaryVaccineDetails = primaryVaccineDetails;
	}

	public AefiInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(AefiInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public AefiClassification getAefiClassification() {
		return aefiClassification;
	}

	public void setAefiClassification(AefiClassification aefiClassification) {
		this.aefiClassification = aefiClassification;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	@Override
	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
