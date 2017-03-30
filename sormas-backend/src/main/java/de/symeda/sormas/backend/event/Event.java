package de.symeda.sormas.backend.event;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Entity(name="events")
public class Event extends AbstractDomainObject {

	private static final long serialVersionUID = 4964495716032049582L;
	
	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_PERSONS = "eventPersons";
	public static final String EVENT_DESC = "eventDesc";
	public static final String EVENT_DATE = "eventDate";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String TYPE_OF_PLACE = "typeOfPlace";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String SRC_EMAIL = "srcEmail";
	public static final String DISEASE = "disease";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String TYPE_OF_PLACE_TEXT = "typeOfPlaceText";
	public static final String TASKS = "tasks";
	
	private EventType eventType;
	private EventStatus eventStatus;
	private List<EventParticipant> eventPersons;
	private String eventDesc;
	private Date eventDate;
	private Date reportDateTime;
	private User reportingUser;
	private Location eventLocation;
	private TypeOfPlace typeOfPlace;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcEmail;
	private Disease disease;
	private User surveillanceOfficer;
	private String typeOfPlaceText;

	private List<Task> tasks;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public EventStatus getEventStatus() {
		return eventStatus;
	}
	
	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	@OneToMany(cascade = {}, mappedBy = EventParticipant.EVENT)
	public List<EventParticipant> getEventPersons() {
		return eventPersons;
	}
	
	public void setEventPersons(List<EventParticipant> eventPersons) {
		this.eventPersons = eventPersons;
	}
	
	@Column(length=512, nullable=false)
	public String getEventDesc() {
		return eventDesc;
	}
	
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getReportingUser() {
		return reportingUser;
	}
	
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	public Location getEventLocation() {
		if(eventLocation == null) {
			eventLocation = new Location();
		}
		return eventLocation;
	}
	
	public void setEventLocation(Location eventLocation) {
		this.eventLocation = eventLocation;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}
	
	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}
	
	@Column(length=512, nullable=false)
	public String getSrcFirstName() {
		return srcFirstName;
	}
	
	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}
	
	@Column(length=512, nullable=false)
	public String getSrcLastName() {
		return srcLastName;
	}
	
	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}
	
	@Column(length=512, nullable=false)
	public String getSrcTelNo() {
		return srcTelNo;
	}
	
	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}
	
	@Column(length=512)
	public String getSrcEmail() {
		return srcEmail;
	}
	
	public void setSrcEmail(String srcEmail) {
		this.srcEmail = srcEmail;
	}
	
	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}
	
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	@ManyToOne(cascade = {})
	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}
	
	@Column(length=512)
	public String getTypeOfPlaceText() {
		return typeOfPlaceText;
	}
	
	public void setTypeOfPlaceText(String typeOfPlaceText) {
		this.typeOfPlaceText = typeOfPlaceText;
	}
	
	@OneToMany(cascade = {}, mappedBy = Task.EVENT)
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public String toString() {
		String diseaseString = disease == null ? "" : disease.toString();
		String eventTypeString = diseaseString.isEmpty() ? eventType.toString() : eventType.toString().toLowerCase();
		return diseaseString + " " + eventTypeString + " on " + DateHelper.formatDate(eventDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (disease != other.disease)
			return false;
		if (eventDate == null) {
			if (other.eventDate != null)
				return false;
		} else if (!eventDate.equals(other.eventDate))
			return false;
		if (eventDesc == null) {
			if (other.eventDesc != null)
				return false;
		} else if (!eventDesc.equals(other.eventDesc))
			return false;
		if (eventLocation == null) {
			if (other.eventLocation != null)
				return false;
		} else if (!eventLocation.equals(other.eventLocation))
			return false;
		if (eventPersons == null) {
			if (other.eventPersons != null)
				return false;
		} else if (!eventPersons.equals(other.eventPersons))
			return false;
		if (eventStatus != other.eventStatus)
			return false;
		if (eventType != other.eventType)
			return false;
		if (reportDateTime == null) {
			if (other.reportDateTime != null)
				return false;
		} else if (!reportDateTime.equals(other.reportDateTime))
			return false;
		if (reportingUser == null) {
			if (other.reportingUser != null)
				return false;
		} else if (!reportingUser.equals(other.reportingUser))
			return false;
		if (srcEmail == null) {
			if (other.srcEmail != null)
				return false;
		} else if (!srcEmail.equals(other.srcEmail))
			return false;
		if (srcFirstName == null) {
			if (other.srcFirstName != null)
				return false;
		} else if (!srcFirstName.equals(other.srcFirstName))
			return false;
		if (srcLastName == null) {
			if (other.srcLastName != null)
				return false;
		} else if (!srcLastName.equals(other.srcLastName))
			return false;
		if (srcTelNo == null) {
			if (other.srcTelNo != null)
				return false;
		} else if (!srcTelNo.equals(other.srcTelNo))
			return false;
		if (surveillanceOfficer == null) {
			if (other.surveillanceOfficer != null)
				return false;
		} else if (!surveillanceOfficer.equals(other.surveillanceOfficer))
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		if (typeOfPlace != other.typeOfPlace)
			return false;
		if (typeOfPlaceText == null) {
			if (other.typeOfPlaceText != null)
				return false;
		} else if (!typeOfPlaceText.equals(other.typeOfPlaceText))
			return false;
		return true;
	}

}
