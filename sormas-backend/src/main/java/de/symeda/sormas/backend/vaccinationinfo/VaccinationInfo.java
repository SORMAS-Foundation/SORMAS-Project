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

package de.symeda.sormas.backend.vaccinationinfo;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class VaccinationInfo extends AbstractDomainObject {

	private static final long serialVersionUID = 110758935266105730L;

	public static final String VACCINATION = "vaccination";
	public static final String VACCINATION_DOSES = "vaccinationDoses";
	public static final String VACCINATION_INFO_SOURCE = "vaccinationInfoSource";
	public static final String FIRST_VACCINATION_DATE = "firstVaccinationDate";
	public static final String LAST_VACCINATION_DATE = "lastVaccinationDate";
	public static final String VACCINE_NAME = "vaccineName";
	public static final String OTHER_VACCINE_NAME = "otherVaccineName";
	public static final String VACCINE_MANUFACTURER = "vaccineManufacturer";
	public static final String OTHER_VACCINE_MANUFACTURER = "otherVaccineManufacturer";
	public static final String VACCINE_INN = "vaccineInn";
	public static final String VACCINE_BATCH_NUMBER = "vaccineBatchNumber";
	public static final String VACCINE_UNII_CODE = "vaccineUniiCode";
	public static final String VACCINE_ATC_CODE = "vaccineAtcCode";

	private Vaccination vaccination;
	private String vaccinationDoses;
	private VaccinationInfoSource vaccinationInfoSource;
	private Date firstVaccinationDate;
	private Date lastVaccinationDate;
	private Vaccine vaccineName;
	private String otherVaccineName;
	private VaccineManufacturer vaccineManufacturer;
	private String otherVaccineManufacturer;
	private String vaccineInn;
	private String vaccineBatchNumber;
	private String vaccineUniiCode;
	private String vaccineAtcCode;

	@Enumerated(EnumType.STRING)
	public Vaccination getVaccination() {
		return vaccination;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	@Enumerated(EnumType.STRING)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(Date firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date vaccinationDate) {
		this.lastVaccinationDate = vaccinationDate;
	}

	@Enumerated(EnumType.STRING)
	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	@Column(columnDefinition = "text")
	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	@Enumerated(EnumType.STRING)
	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
	}

	@Column(columnDefinition = "text")
	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	public void setOtherVaccineManufacturer(String otherVaccineManufacturer) {
		this.otherVaccineManufacturer = otherVaccineManufacturer;
	}

	@Column(columnDefinition = "text")
	public String getVaccineInn() {
		return vaccineInn;
	}

	public void setVaccineInn(String vaccineInn) {
		this.vaccineInn = vaccineInn;
	}

	@Column(columnDefinition = "text")
	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	public void setVaccineBatchNumber(String vaccineBatchNumber) {
		this.vaccineBatchNumber = vaccineBatchNumber;
	}

	@Column(columnDefinition = "text")
	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	public void setVaccineUniiCode(String vaccineUniiCode) {
		this.vaccineUniiCode = vaccineUniiCode;
	}

	@Column(columnDefinition = "text")
	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	public void setVaccineAtcCode(String vaccineAtcCode) {
		this.vaccineAtcCode = vaccineAtcCode;
	}
}
