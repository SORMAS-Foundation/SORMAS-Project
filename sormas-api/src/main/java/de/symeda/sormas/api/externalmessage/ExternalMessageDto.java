/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportReferenceDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

@AuditedClass
@DependingOnFeatureType(featureType = FeatureType.EXTERNAL_MESSAGES)
public class ExternalMessageDto extends SormasToSormasShareableDto {

	public static final String I18N_PREFIX = "ExternalMessage";

	public static final String TYPE = "type";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String CASE_REPORT_DATE = "caseReportDate";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_EXTERNAL_ID = "reporterExternalId";
	public static final String REPORTER_POSTAL_CODE = "reporterPostalCode";
	public static final String REPORTER_CITY = "reporterCity";
	public static final String PERSON_EXTERNAL_ID = "personExternalId";
	public static final String PERSON_NATIONAL_HEALTH_ID = "personNationalHealthId";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_SEX = "personSex";
	public static final String PERSON_BIRTH_DATE_DD = "personBirthDateDD";
	public static final String PERSON_BIRTH_DATE_MM = "personBirthDateMM";
	public static final String PERSON_BIRTH_DATE_YYYY = "personBirthDateYYYY";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String PERSON_CITY = "personCity";
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_PHONE_NUMBER_TYPE = "personPhoneNumberType";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String PERSON_COUNTRY = "personCountry";
	public static final String PERSON_FACILITY = "personFacility";
	public static final String EXTERNAL_MESSAGE_DETAILS = "externalMessageDetails";
	public static final String PROCESSED = "processed";
	public static final String REPORT_ID = "reportId";
	public static final String REPORT_MESSAGE_ID = "reportMessageId";
	public static final String STATUS = "status";
	public static final String ASSIGNEE = "assignee";
	public static final String SURVEILLANCE_REPORT = "surveillanceReport";
	public static final String AUTOMATIC_PROCESSING_POSSIBLE = "automaticProcessingPossible";

	@AuditIncludeProperty
	private ExternalMessageType type;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseVariantDetails;
	@AuditIncludeProperty
	private Date messageDateTime;
	private Date caseReportDate;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterName;
	private List<String> reporterExternalIds;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterPostalCode;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterCity;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personFirstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personLastName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String personExternalId;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personNationalHealthId;
	private Sex personSex;
	private PresentCondition personPresentCondition;
	private Integer personBirthDateDD;
	private Integer personBirthDateMM;
	private Integer personBirthDateYYYY;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personPostalCode;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personCity;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personStreet;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personHouseNumber;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private CountryReferenceDto personCountry;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private FacilityReferenceDto personFacility;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personPhone;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private PhoneNumberType personPhoneNumberType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String personEmail;
	@AuditIncludeProperty
	private List<SampleReportDto> sampleReports;
	@AuditIncludeProperty
	private SurveillanceReportReferenceDto surveillanceReport;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String externalMessageDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String reportId;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String reportMessageId;
	@AuditIncludeProperty
	private ExternalMessageStatus status = ExternalMessageStatus.UNPROCESSED;

	private UserReferenceDto assignee;
	/**
	 * Used in S2S context
	 */
	private UserReferenceDto reportingUser;

	private boolean automaticProcessingPossible;

	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
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

	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public List<String> getReporterExternalIds() {
		return reporterExternalIds;
	}

	public void setReporterExternalIds(List<String> reporterExternalIds) {
		this.reporterExternalIds = reporterExternalIds;
	}

	public String getReporterPostalCode() {
		return reporterPostalCode;
	}

	public void setReporterPostalCode(String reporterPostalCode) {
		this.reporterPostalCode = reporterPostalCode;
	}

	public String getReporterCity() {
		return reporterCity;
	}

	public void setReporterCity(String reporterCity) {
		this.reporterCity = reporterCity;
	}

	public String getPersonExternalId() {
		return personExternalId;
	}

	public void setPersonExternalId(String personExternalId) {
		this.personExternalId = personExternalId;
	}

	public String getPersonNationalHealthId() {
		return personNationalHealthId;
	}

	public void setPersonNationalHealthId(String personNationalHealthId) {
		this.personNationalHealthId = personNationalHealthId;
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

	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	public PresentCondition getPersonPresentCondition() {
		return personPresentCondition;
	}

	public void setPersonPresentCondition(PresentCondition personPresentCondition) {
		this.personPresentCondition = personPresentCondition;
	}

	public Integer getPersonBirthDateDD() {
		return personBirthDateDD;
	}

	public void setPersonBirthDateDD(Integer personBirthDateDD) {
		this.personBirthDateDD = personBirthDateDD;
	}

	public Integer getPersonBirthDateMM() {
		return personBirthDateMM;
	}

	public void setPersonBirthDateMM(Integer personBirthDateMM) {
		this.personBirthDateMM = personBirthDateMM;
	}

	public Integer getPersonBirthDateYYYY() {
		return personBirthDateYYYY;
	}

	public void setPersonBirthDateYYYY(Integer personBirthDateYYYY) {
		this.personBirthDateYYYY = personBirthDateYYYY;
	}

	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	public CountryReferenceDto getPersonCountry() {
		return personCountry;
	}

	public void setPersonCountry(CountryReferenceDto personCountry) {
		this.personCountry = personCountry;
	}

	public FacilityReferenceDto getPersonFacility() {
		return personFacility;
	}

	public void setPersonFacility(FacilityReferenceDto personFacility) {
		this.personFacility = personFacility;
	}

	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	public PhoneNumberType getPersonPhoneNumberType() {
		return personPhoneNumberType;
	}

	public void setPersonPhoneNumberType(PhoneNumberType personPhoneNumberType) {
		this.personPhoneNumberType = personPhoneNumberType;
	}

	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	public String getExternalMessageDetails() {
		return externalMessageDetails;
	}

	public void setExternalMessageDetails(String externalMessageDetails) {
		this.externalMessageDetails = externalMessageDetails;
	}

	public ExternalMessageStatus getStatus() {
		return status;
	}

	public void setStatus(ExternalMessageStatus status) {
		this.status = status;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportMessageId() {
		return reportMessageId;
	}

	public void setReportMessageId(String reportMessageId) {
		this.reportMessageId = reportMessageId;
	}

	public UserReferenceDto getAssignee() {
		return assignee;
	}

	public void setAssignee(UserReferenceDto assignee) {
		this.assignee = assignee;
	}

	public SurveillanceReportReferenceDto getSurveillanceReport() {
		return surveillanceReport;
	}

	public void setSurveillanceReport(SurveillanceReportReferenceDto surveillanceReport) {
		this.surveillanceReport = surveillanceReport;
	}

	public static ExternalMessageDto build() {

		ExternalMessageDto message = new ExternalMessageDto();
		message.setUuid(DataHelper.createUuid());
		return message;
	}

	public ExternalMessageReferenceDto toReference() {
		return new ExternalMessageReferenceDto(getUuid());
	}

	/**
	 * This method does never return null. When there are no sample reports, a new sample report is built and returned (in a list).
	 * Alternatively, {@link ExternalMessageDto#getSampleReports()} can be used.
	 * 
	 * @return List containing related sample reports or a newly built sample report
	 */
	public List<SampleReportDto> getSampleReportsNullSafe() {
		if (sampleReports == null) {
			sampleReports = new ArrayList<>();
		}
		if (sampleReports.isEmpty()) {
			SampleReportDto sampleReport = SampleReportDto.build();
			sampleReport.setLabMessage(this.toReference());
			sampleReports.add(sampleReport);
		}
		return sampleReports;
	}

	/**
	 * Please note that this method may return null as a valid behaviour.
	 * Alternatively {@link ExternalMessageDto#getSampleReportsNullSafe()} can be used.
	 * 
	 * @return List of related sample reports (if any)
	 */
	public List<SampleReportDto> getSampleReports() {
		return sampleReports;
	}

	/**
	 * Use this method only if you want to discard already added sample reports. In that case, remember to set the according reference in
	 * the sample report ({@link SampleReportDto#setLabMessage(ExternalMessageReferenceDto)}).
	 * Otherwise, use the {@link ExternalMessageDto#addSampleReport(SampleReportDto)}.
	 *
	 */
	public void setSampleReports(List<SampleReportDto> sampleReports) {

		this.sampleReports = new ArrayList<>(sampleReports);
	}

	public void addSampleReport(SampleReportDto sampleReport) {

		sampleReport.setLabMessage(this.toReference());
		if (sampleReports == null) {
			sampleReports = new ArrayList<>();
		}
		sampleReports.add(sampleReport);

	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isAutomaticProcessingPossible() {
		return automaticProcessingPossible;
	}

	public void setAutomaticProcessingPossible(boolean automaticProcessingPossible) {
		this.automaticProcessingPossible = automaticProcessingPossible;
	}
}
