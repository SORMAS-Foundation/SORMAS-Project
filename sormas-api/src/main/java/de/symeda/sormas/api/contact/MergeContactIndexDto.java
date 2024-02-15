/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.MergeableIndexDto;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class MergeContactIndexDto extends PseudonymizableIndexDto implements IsContact, MergeableIndexDto, Serializable, Cloneable {

	private static final long serialVersionUID = 7147772346906228533L;

	public static final String I18N_PREFIX = "Contact";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String PERSON_FIRST_NAME = "firstName";
	public static final String PERSON_LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String CAZE = "caze";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String REGION_NAME = "regionName";
	public static final String DISTRICT_NAME = "districtName";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CREATION_DATE = "creationDate";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String COMPLETENESS = "completeness";
	public static final String REPORT_DATE_TIME = "reportDateTime";

	private long id;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	@EmbeddedPersonalData
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	@EmbeddedPersonalData
	private CaseReferenceDto caze;
	private Disease disease;
	private String diseaseDetails;
	private String regionName;
	private String districtName;
	private Date lastContactDate;
	private Date creationDate;
	private ContactClassification contactClassification;
	private Float completeness;
	private Date reportDateTime;

	private ContactJurisdictionFlagsDto contactJurisdictionFlagsDto;

	public MergeContactIndexDto(
		long id,
		String uuid,
		String firstName,
		String lastName,
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Sex sex,
		String cazeUuid,
		String caseFirstName,
		String caseLastName,
		Disease disease,
		String diseaseDetails,
		String regionName,
		String districtName,
		Date lastContactDate,
		Date creationDate,
		ContactClassification contactClassification,
		Float completeness,
		Date reportDateTime,
		boolean isInJurisdiction,
		boolean isCaseInJurisdiction) {

		super(uuid);
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.ageAndBirthDate = new AgeAndBirthDateDto(age, ageType, birthdateDD, birthdateMM, birthdateYYYY);
		this.sex = sex;
		if (cazeUuid != null) {
			this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
		}
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.regionName = regionName;
		this.districtName = districtName;
		this.lastContactDate = lastContactDate;
		this.creationDate = creationDate;
		this.contactClassification = contactClassification;
		this.completeness = completeness;
		this.reportDateTime = reportDateTime;

		this.contactJurisdictionFlagsDto = new ContactJurisdictionFlagsDto(isInJurisdiction, isCaseInJurisdiction);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
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

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public Float getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public Boolean getInJurisdiction() {
		return contactJurisdictionFlagsDto.getInJurisdiction();
	}

	public Boolean getCaseInJurisdiction() {
		return contactJurisdictionFlagsDto.getCaseInJurisdiction();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}
}
