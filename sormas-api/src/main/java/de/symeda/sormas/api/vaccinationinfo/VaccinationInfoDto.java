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

package de.symeda.sormas.api.vaccinationinfo;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Outbreaks;

public class VaccinationInfoDto extends EntityDto {

	public static final String I18N_PREFIX = "VaccinationInfo";

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

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private Vaccination vaccination;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.CSM,
		Disease.YELLOW_FEVER,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private String vaccinationDoses;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.MONKEYPOX,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private Date firstVaccinationDate;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.MONKEYPOX,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	@Outbreaks
	private Date lastVaccinationDate;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	private VaccinationInfoSource vaccinationInfoSource;

	@Diseases({
		Disease.CORONAVIRUS })
	private Vaccine vaccineName;

	@Diseases({
		Disease.CORONAVIRUS })
	private String otherVaccineName;

	@Diseases({
		Disease.CORONAVIRUS, })
	private VaccineManufacturer vaccineManufacturer;

	@Diseases({
		Disease.CORONAVIRUS, })
	private String otherVaccineManufacturer;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	private String vaccineInn;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	private String vaccineBatchNumber;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	private String vaccineUniiCode;

	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.OTHER })
	private String vaccineAtcCode;

	public static VaccinationInfoDto build() {
		VaccinationInfoDto vaccinationInfo = new VaccinationInfoDto();
		vaccinationInfo.setUuid(DataHelper.createUuid());

		return vaccinationInfo;
	}

	public Vaccination getVaccination() {
		return vaccination;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(Date firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}

	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
	}

	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	public void setOtherVaccineManufacturer(String otherVaccineManufacturer) {
		this.otherVaccineManufacturer = otherVaccineManufacturer;
	}

	public String getVaccineInn() {
		return vaccineInn;
	}

	public void setVaccineInn(String vaccineInn) {
		this.vaccineInn = vaccineInn;
	}

	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	public void setVaccineBatchNumber(String vaccineBatchNumber) {
		this.vaccineBatchNumber = vaccineBatchNumber;
	}

	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	public void setVaccineUniiCode(String vaccineUniiCode) {
		this.vaccineUniiCode = vaccineUniiCode;
	}

	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	public void setVaccineAtcCode(String vaccineAtcCode) {
		this.vaccineAtcCode = vaccineAtcCode;
	}
}
