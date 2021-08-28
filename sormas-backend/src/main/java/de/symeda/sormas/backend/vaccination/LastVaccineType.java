package de.symeda.sormas.backend.vaccination;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity(name = "")
@Subselect("SELECT v.immunization_id as immunizationId, vaccinetype FROM vaccination v INNER JOIN (SELECT immunization_id, MAX(vaccinationdate) maxdate FROM vaccination GROUP BY immunization_id) maxdates ON v.immunization_id=maxdates.immunization_id AND v.vaccinationdate=maxdates.maxdate")
@Synchronize("vaccination")
public class LastVaccineType {

	public static final String IMMUNIZATION_ID = "immunizationId";
	public static final String VACCINE_TYPE = "vaccineType";

	@Id
	private Long id;

	private Long immunizationId;
	private String vaccineType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getImmunizationId() {
		return immunizationId;
	}

	public void setImmunizationId(Long immunizationId) {
		this.immunizationId = immunizationId;
	}

	public String getVaccineType() {
		return vaccineType;
	}

	public void setVaccineType(String vaccineType) {
		this.vaccineType = vaccineType;
	}
}
