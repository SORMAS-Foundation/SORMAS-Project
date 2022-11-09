package de.symeda.sormas.backend.immunization.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;

@Entity(name = "immunizationDirectory")
@Audited
@Table(name = "immunization")
public class DirectoryImmunization extends BaseImmunization {

	public static final String LAST_VACCINE_TYPE = "lastVaccineType";
	public static final String LAST_VACCINATION_DATE = "lastVaccinationDate";
	public static final String FIRST_VACCINATION_DATE = "firstVaccinationDate";

	private LastVaccineType lastVaccineType;
	private LastVaccinationDate lastVaccinationDate;
	private FirstVaccinationDate firstVaccinationDate;

	@OneToOne(mappedBy = "immunization")
	public LastVaccineType getLastVaccineType() {
		return lastVaccineType;
	}

	public void setLastVaccineType(LastVaccineType lastVaccineType) {
		this.lastVaccineType = lastVaccineType;
	}

	@OneToOne(mappedBy = "immunization")
	public LastVaccinationDate getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(LastVaccinationDate lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	@OneToOne(mappedBy = "immunization")
	public FirstVaccinationDate getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(FirstVaccinationDate firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}
}
