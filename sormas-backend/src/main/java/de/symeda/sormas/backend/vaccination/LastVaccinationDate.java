package de.symeda.sormas.backend.vaccination;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import de.symeda.sormas.backend.immunization.Immunization;

@Entity
@Subselect("SELECT immunization_id, MAX(vaccinationdate) vaccinationDate FROM vaccination GROUP BY immunization_id")
@Synchronize("vaccination")
public class LastVaccinationDate {

	public static final String VACCINATION_DATE = "vaccinationDate";

	@Id
	@Column(name = "immunization_id", updatable = false, insertable = false)
	private Long immunizationId;

	private Immunization immunization;
	private Date vaccinationDate;

	@OneToOne(mappedBy = "lastVaccinationDate")
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}
}
