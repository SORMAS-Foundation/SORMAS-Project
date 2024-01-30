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

package de.symeda.sormas.api.immunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ImmunizationIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "Immunization";

	public static final String UUID = "uuid";
	public static final String PERSON_UUID = "personUuid";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String DISEASE = "disease";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String DISTRICT = "district";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String MANAGEMENT_STATUS = "managementStatus";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String LAST_VACCINE_TYPE = "lastVaccineType";
	public static final String RECOVERY_DATE = "recoveryDate";
	public static final String IMMUNIZATION_PERIOD = "immunizationPeriod";

	private String personUuid;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;
	private Disease disease;
	@EmbeddedPersonalData
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	private String district;
	private MeansOfImmunization meansOfImmunization;
	private ImmunizationManagementStatus managementStatus;
	private ImmunizationStatus immunizationStatus;
	private Date startDate;
	private Date endDate;
	private String lastVaccineType;
	private Date recoveryDate;
	private DeletionReason deletionReason;
	private String otherDeletionReason;

	private boolean isInJurisdiction;

	public ImmunizationIndexDto(
		String uuid,
		String personUuid,
		String personFirstName,
		String personLastName,
		Disease disease,
		AgeAndBirthDateDto ageAndBirthDate,
		Sex sex,
		String district,
		MeansOfImmunization meansOfImmunization,
		ImmunizationManagementStatus managementStatus,
		ImmunizationStatus immunizationStatus,
		Date startDate,
		Date endDate,
		String lastVaccineType,
		Date recoveryDate,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean isInJurisdiction) {

		super(uuid);
		this.personUuid = personUuid;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.disease = disease;
		this.ageAndBirthDate = ageAndBirthDate;
		this.sex = sex;
		this.district = district;
		this.meansOfImmunization = meansOfImmunization;
		this.managementStatus = managementStatus;
		this.immunizationStatus = immunizationStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.lastVaccineType = lastVaccineType;
		this.recoveryDate = recoveryDate;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		this.isInJurisdiction = isInJurisdiction;
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
		this.meansOfImmunization = meansOfImmunization;
	}

	public ImmunizationManagementStatus getManagementStatus() {
		return managementStatus;
	}

	public void setManagementStatus(ImmunizationManagementStatus managementStatus) {
		this.managementStatus = managementStatus;
	}

	public ImmunizationStatus getImmunizationStatus() {
		return immunizationStatus;
	}

	public void setImmunizationStatus(ImmunizationStatus immunizationStatus) {
		this.immunizationStatus = immunizationStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLastVaccineType() {
		return lastVaccineType;
	}

	public void setLastVaccineType(String lastVaccineType) {
		this.lastVaccineType = lastVaccineType;
	}

	public Date getRecoveryDate() {
		return recoveryDate;
	}

	public void setRecoveryDate(Date recoveryDate) {
		this.recoveryDate = recoveryDate;
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

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
