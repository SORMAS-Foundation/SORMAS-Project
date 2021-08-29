package de.symeda.sormas.backend.vaccination;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import de.symeda.sormas.backend.immunization.Immunization;

@Entity
@Subselect("SELECT v.immunization_id, vaccinetype FROM vaccination v INNER JOIN (SELECT immunization_id, MAX(vaccinationdate) maxdate FROM vaccination GROUP BY immunization_id) maxdates ON v.immunization_id=maxdates.immunization_id AND v.vaccinationdate=maxdates.maxdate")
@Synchronize("vaccination")
public class LastVaccineType {

	public static final String VACCINE_TYPE = "vaccineType";

	@Id
	@Column(name = "immunization_id", updatable = false, insertable = false)
	private Long immunizationId;

	private Immunization immunization;
	private String vaccineType;

	@OneToOne(mappedBy = "lastVaccineType")
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
