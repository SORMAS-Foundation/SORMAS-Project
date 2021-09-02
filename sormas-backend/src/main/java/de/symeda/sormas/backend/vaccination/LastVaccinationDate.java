package de.symeda.sormas.backend.vaccination;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import de.symeda.sormas.backend.immunization.entity.Immunization;

@Entity
@Subselect("SELECT immunization_id, MAX(vaccinationdate) lastVaccinationDate FROM vaccination GROUP BY immunization_id")
@Synchronize("vaccination")
public class LastVaccinationDate implements Serializable {

	public static final String VACCINATION_DATE = "lastVaccinationDate";

	private Immunization immunization;
	private Date lastVaccinationDate;

	@Id
	@OneToOne
	@JoinColumn(name = "immunization_id", referencedColumnName = "id")
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date vaccinationDate) {
		this.lastVaccinationDate = vaccinationDate;
	}
}
