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
@Subselect("SELECT immunization_id, MIN(vaccinationdate) firstVaccinationDate FROM vaccination GROUP BY immunization_id")
@Synchronize("vaccination")
public class FirstVaccinationDate {

	public static final String VACCINATION_DATE = "firstVaccinationDate";

	@Id
	@Column(name = "immunization_id", updatable = false, insertable = false)
	private Long immunizationId;

	private Immunization immunization;
	private Date firstVaccinationDate;

	@OneToOne(mappedBy = "firstVaccinationDate")
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(Date vaccinationDate) {
		this.firstVaccinationDate = vaccinationDate;
	}
}
