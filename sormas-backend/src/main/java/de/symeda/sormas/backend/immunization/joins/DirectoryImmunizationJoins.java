package de.symeda.sormas.backend.immunization.joins;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.immunization.entity.DirectoryImmunization;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;

public class DirectoryImmunizationJoins extends BaseImmunizationJoins<DirectoryImmunization> {

	private Join<DirectoryImmunization, LastVaccineType> lastVaccineType;
	private Join<DirectoryImmunization, LastVaccinationDate> lastVaccinationDate;
	private Join<DirectoryImmunization, FirstVaccinationDate> firstVaccinationDate;

	public DirectoryImmunizationJoins(From<?, DirectoryImmunization> root) {
		super(root);
	}

	public Join<DirectoryImmunization, LastVaccineType> getLastVaccineType() {
		return getOrCreate(lastVaccineType, DirectoryImmunization.LAST_VACCINE_TYPE, JoinType.LEFT, this::setLastVaccineType);
	}

	public void setLastVaccineType(Join<DirectoryImmunization, LastVaccineType> lastVaccineType) {
		this.lastVaccineType = lastVaccineType;
	}

	public Join<DirectoryImmunization, LastVaccinationDate> getLastVaccinationDate() {
		return getOrCreate(lastVaccinationDate, DirectoryImmunization.LAST_VACCINATION_DATE, JoinType.LEFT, this::setLastVaccinationDate);
	}

	public void setLastVaccinationDate(Join<DirectoryImmunization, LastVaccinationDate> lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public Join<DirectoryImmunization, FirstVaccinationDate> getFirstVaccinationDate() {
		return getOrCreate(firstVaccinationDate, DirectoryImmunization.FIRST_VACCINATION_DATE, JoinType.LEFT, this::setFirstVaccinationDate);
	}

	public void setFirstVaccinationDate(Join<DirectoryImmunization, FirstVaccinationDate> firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}
}
