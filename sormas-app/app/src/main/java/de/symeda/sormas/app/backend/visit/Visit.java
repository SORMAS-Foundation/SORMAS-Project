package de.symeda.sormas.app.backend.visit;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;

@Entity(name= Visit.TABLE_NAME)
@DatabaseTable(tableName = Visit.TABLE_NAME)
public class Visit extends AbstractDomainObject {

	public static final String TABLE_NAME = "visits";
	public static final String I18N_PREFIX = "Visit";

	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_USER = "visitUser";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String SYMPTOMS = "symptoms";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false)
	private Person person;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date visitDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1, canBeNull = false)
	private User visitUser;

	@Enumerated(EnumType.STRING)
	private VisitStatus visitStatus;

	@Column(length=512)
	private String visitRemarks;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private Symptoms symptoms;
	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}
	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public User getVisitUser() {
		return visitUser;
	}
	public void setVisitUser(User visitUser) {
		this.visitUser = visitUser;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}
	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}
	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	/**
	 * return the symptoms, if null create new in service layer
	 * @return
     */
	public Symptoms getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	@Override
	public boolean isModifiedOrChildModified() {
		super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + " " + DateHelper.formatShortDate(getVisitDateTime());
	}
}
