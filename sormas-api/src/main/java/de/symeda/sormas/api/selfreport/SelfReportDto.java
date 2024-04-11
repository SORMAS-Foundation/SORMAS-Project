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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.SELF_REPORTING)
public class SelfReportDto extends EntityDto {

	private static final long serialVersionUID = 604507951783731873L;

	public static final String I18N_PREFIX = "SelfReport";

	@NotNull(message = Validations.requiredField)
	private SelfReportType type;
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	@NotNull(message = Validations.requiredField)
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String firstName;
	@NotBlank(message = Validations.requiredField)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String lastName;
	@NotNull(message = Validations.requiredField)
	private Sex sex;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String nationalHealthId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String email;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String phoneNumber;
	private LocationDto address;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private UserReferenceDto responsibleUser;
	private String comment;
	@NotNull(message = Validations.requiredField)
	private SelfReportInvestigationStatus investigationStatus;
	@NotNull(message = Validations.requiredField)
	private SelfReportProcessingStatus processingStatus;

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

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
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
}
