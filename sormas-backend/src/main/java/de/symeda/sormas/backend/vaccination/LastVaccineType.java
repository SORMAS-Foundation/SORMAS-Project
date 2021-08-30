package de.symeda.sormas.backend.vaccination;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import de.symeda.sormas.backend.immunization.entity.Immunization;

@Entity
@Subselect("SELECT v.immunization_id, vaccinetype FROM vaccination v INNER JOIN (SELECT immunization_id, MAX(vaccinationdate) maxdate FROM vaccination GROUP BY immunization_id) maxdates ON v.immunization_id=maxdates.immunization_id AND v.vaccinationdate=maxdates.maxdate")
@Synchronize("vaccination")
public class LastVaccineType implements Serializable {

	public static final String VACCINE_TYPE = "vaccineType";

	private Immunization immunization;
	private String vaccineType;

	@Id
	@OneToOne
	@JoinColumn(name = "immunization_id", referencedColumnName = "id")
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	public String getVaccineType() {
		return vaccineType;
	}

	public void setVaccineType(String vaccineType) {
		this.vaccineType = vaccineType;
	}
}
