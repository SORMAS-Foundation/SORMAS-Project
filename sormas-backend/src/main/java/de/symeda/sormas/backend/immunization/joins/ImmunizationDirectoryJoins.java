package de.symeda.sormas.backend.immunization.joins;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.immunization.entity.ImmunizationDirectory;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;

public class ImmunizationDirectoryJoins<T> extends BaseImmunizationJoins<T, ImmunizationDirectory> {

	private Join<ImmunizationDirectory, LastVaccineType> lastVaccineType;
	private Join<ImmunizationDirectory, LastVaccinationDate> lastVaccinationDate;
	private Join<ImmunizationDirectory, FirstVaccinationDate> firstVaccinationDate;

	public ImmunizationDirectoryJoins(From<T, ImmunizationDirectory> root) {
		super(root);
	}

	public Join<ImmunizationDirectory, LastVaccineType> getLastVaccineType() {
		return getOrCreate(lastVaccineType, ImmunizationDirectory.LAST_VACCINE_TYPE, JoinType.LEFT, this::setLastVaccineType);
	}

	public void setLastVaccineType(Join<ImmunizationDirectory, LastVaccineType> lastVaccineType) {
		this.lastVaccineType = lastVaccineType;
	}

	public Join<ImmunizationDirectory, LastVaccinationDate> getLastVaccinationDate() {
		return getOrCreate(lastVaccinationDate, ImmunizationDirectory.LAST_VACCINATION_DATE, JoinType.LEFT, this::setLastVaccinationDate);
	}

	public void setLastVaccinationDate(Join<ImmunizationDirectory, LastVaccinationDate> lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public Join<ImmunizationDirectory, FirstVaccinationDate> getFirstVaccinationDate() {
		return getOrCreate(firstVaccinationDate, ImmunizationDirectory.FIRST_VACCINATION_DATE, JoinType.LEFT, this::setFirstVaccinationDate);
	}

	public void setFirstVaccinationDate(Join<ImmunizationDirectory, FirstVaccinationDate> firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}
}
