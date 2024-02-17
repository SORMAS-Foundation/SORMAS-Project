/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class AefiIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "AefiIndex";

	public static final String UUID = "uuid";
	public static final String IMMUNIZATION_UUID = "immunizationUuid";
	public static final String PERSON_UUID = "personUuid";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String REPORT_DATE = "reportDate";
	public static final String DISEASE = "disease";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String SERIOUS = "serious";
	public static final String PRIMARY_VACCINE_NAME = "primaryVaccine";
	public static final String OUTCOME = "outcome";
	public static final String VACCINATION_DATE = "vaccinationDate";
	public static final String START_DATE_TIME = "startDateTime";
	public static final String ADVERSE_EVENTS = "adverseEvents";

	private String immunizationUuid;
	private String personUuid;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;
	private Date reportDate;
	private Disease disease;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	private String region;
	private String district;
	private YesNoUnknown serious;
	private Vaccine primaryVaccine;
	private AefiOutcome outcome;
	private Date vaccinationDate;
	private Date startDateTime;
	private String adverseEvents;
	private DeletionReason deletionReason;
	private String otherDeletionReason;
	private boolean isInJurisdiction;

	public AefiIndexDto(
		String uuid,
		String immunizationUuid,
		String personUuid,
		String personFirstName,
		String personLastName,
		Disease disease,
		AgeAndBirthDateDto ageAndBirthDate,
		Sex sex,
		String region,
		String district,
		YesNoUnknown serious,
		Vaccine primaryVaccine,
		AefiOutcome outcome,
		Date vaccinationDate,
		Date reportDate,
		Date startDateTime,
		String adverseEvents,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean isInJurisdiction) {

		super(uuid);
		this.immunizationUuid = immunizationUuid;
		this.personUuid = personUuid;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.disease = disease;
		this.ageAndBirthDate = ageAndBirthDate;
		this.sex = sex;
		this.region = region;
		this.district = district;
		this.serious = serious;
		this.primaryVaccine = primaryVaccine;
		this.outcome = outcome;
		this.vaccinationDate = vaccinationDate;
		this.reportDate = reportDate;
		this.startDateTime = startDateTime;
		this.adverseEvents = adverseEvents;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getImmunizationUuid() {
		return immunizationUuid;
	}

	public void setImmunizationUuid(String immunizationUuid) {
		this.immunizationUuid = immunizationUuid;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
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

	public YesNoUnknown getSerious() {
		return serious;
	}

	public void setSerious(YesNoUnknown serious) {
		this.serious = serious;
	}

	public Vaccine getPrimaryVaccine() {
		return primaryVaccine;
	}

	public void setPrimaryVaccine(Vaccine primaryVaccine) {
		this.primaryVaccine = primaryVaccine;
	}

	public AefiOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(AefiOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getAdverseEvents() {
		return adverseEvents;
	}

	public void setAdverseEvents(String adverseEvents) {
		this.adverseEvents = adverseEvents;
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
