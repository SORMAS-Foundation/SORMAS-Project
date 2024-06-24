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

package de.symeda.sormas.api.selfreport;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.SELF_REPORTING)
public class SelfReportDto extends PseudonymizableDto {

	private static final long serialVersionUID = 604507951783731873L;

	public static final String I18N_PREFIX = "SelfReport";

	public static final String TYPE = "type";
	public static final String REPORT_DATE = "reportDate";
	public static final String CASE_REFERENCE = "caseReference";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String EMAIL = "email";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String ADDRESS = "address";
	public static final String DATE_OF_TEST = "dateOfTest";
	public static final String DATE_OF_SYMPTOMS = "dateOfSymptoms";
	public static final String WORKPLACE = "workplace";
	public static final String DATE_WORKPLACE = "dateWorkplace";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String CONTACT_DATE = "contactDate";
	public static final String COMMENT = "comment";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PROCESSING_STATUS = "processingStatus";
	public static final String DELETED = "deleted";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@NotNull(message = Validations.requiredField)
	private SelfReportType type;
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@SensitiveData
	private String caseReference;
	@NotNull(message = Validations.requiredField)
	private Disease disease;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseVariantDetails;
	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String firstName;
	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String lastName;
	@NotNull(message = Validations.requiredField)
	private Sex sex;
	@PersonalData
	@SensitiveData
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries
	@SensitiveData
	private String nationalHealthId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@SensitiveData
	private String email;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@SensitiveData
	private String phoneNumber;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Valid
	private LocationDto address;
	private Date dateOfTest;
	private Date dateOfSymptoms;
	@SensitiveData
	private String workplace;
	private Date dateWorkplace;
	private Date isolationDate;
	private Date contactDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	@SensitiveData
	private String comment;
	private UserReferenceDto responsibleUser;
	@NotNull(message = Validations.requiredField)
	private SelfReportInvestigationStatus investigationStatus;
	@NotNull(message = Validations.requiredField)
	private SelfReportProcessingStatus processingStatus;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@SensitiveData
	private String otherDeletionReason;

	public static SelfReportDto build(SelfReportType type) {
		SelfReportDto dto = new SelfReportDto();

		dto.setUuid(DataHelper.createUuid());
		dto.setType(type);
		dto.setAddress(LocationDto.build());
		dto.setInvestigationStatus(SelfReportInvestigationStatus.PENDING);
		dto.setProcessingStatus(SelfReportProcessingStatus.UNPROCESSED);

		return dto;
	}

	public SelfReportType getType() {
		return type;
	}

	public void setType(SelfReportType type) {
		this.type = type;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getCaseReference() {
		return caseReference;
	}

	public void setCaseReference(String caseReference) {
		this.caseReference = caseReference;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
	}

	public Date getDateOfTest() {
		return dateOfTest;
	}

	public void setDateOfTest(Date dateOfTest) {
		this.dateOfTest = dateOfTest;
	}

	public Date getDateOfSymptoms() {
		return dateOfSymptoms;
	}

	public void setDateOfSymptoms(Date dateOfSymptoms) {
		this.dateOfSymptoms = dateOfSymptoms;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public Date getDateWorkplace() {
		return dateWorkplace;
	}

	public void setDateWorkplace(Date dateWorkplace) {
		this.dateWorkplace = dateWorkplace;
	}

	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	public Date getContactDate() {
		return contactDate;
	}

	public void setContactDate(Date contactDate) {
		this.contactDate = contactDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(SelfReportProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	public SelfReportReferenceDto toReference() {
		return new SelfReportReferenceDto(getUuid());
	}
}
