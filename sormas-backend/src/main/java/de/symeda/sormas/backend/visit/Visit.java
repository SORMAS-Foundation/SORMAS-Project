package de.symeda.sormas.backend.visit;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Entity
public class Visit extends AbstractDomainObject {

	private static final long serialVersionUID = -5731538672268784234L;

	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_USER = "visitUser";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String SYMPTOMS = "symptoms";
	
	private Person person;
	private Disease disease;
	private Date visitDateTime;
	private User visitUser;
	private VisitStatus visitStatus;
	private String visitRemarks;
	private Symptoms symptoms;
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getVisitDateTime() {
		return visitDateTime;
	}
	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}
	@Enumerated(EnumType.STRING)
	public VisitStatus getVisitStatus() {
		return visitStatus;
	}
	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}
	@Column(length=512)
	public String getVisitRemarks() {
		return visitRemarks;
	}
	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}
	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	@OneToOne(cascade = CascadeType.ALL)
	public Symptoms getSymptoms() {
		if (symptoms == null) {
			symptoms = new Symptoms();
		}
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	@ManyToOne(cascade = {})
	public User getVisitUser() {
		return visitUser;
	}

	public void setVisitUser(User visitUser) {
		this.visitUser = visitUser;
	}
}
