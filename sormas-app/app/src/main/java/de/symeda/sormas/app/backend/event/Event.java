package de.symeda.sormas.app.backend.event;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;

@Entity(name=Event.TABLE_NAME)
@DatabaseTable(tableName = Event.TABLE_NAME)
public class Event extends AbstractDomainObject {

	private static final long serialVersionUID = 4964495716032049582L;

	public static final String TABLE_NAME = "events";
	public static final String I18N_PREFIX = "Event";
	
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

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private EventType eventType;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private EventStatus eventStatus;

	@Column(length=512, nullable=false)
	private String eventDesc;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date eventDate;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date reportDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private User reportingUser;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location eventLocation;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TypeOfPlace typeOfPlace;

	@Column(length=512, nullable=false)
	private String srcFirstName;

	@Column(length=512, nullable=false)
	private String srcLastName;

	@Column(length=512, nullable=false)
	private String srcTelNo;

	@Column(length=512)
	private String srcEmail;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User surveillanceOfficer;

	@Column(length=512)
	private String typeOfPlaceText;

	@Column(columnDefinition = "float8")
	private Float reportLat;

	@Column(columnDefinition = "float8")
	private Float reportLon;

	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventStatus getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}
	

	public String getEventDesc() {
		return eventDesc;
	}
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}


	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	public Location getEventLocation() {
		return eventLocation;
	}
	
	public void setEventLocation(Location eventLocation) {
		this.eventLocation = eventLocation;
	}
	
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}
	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}
	
	public String getSrcFirstName() {
		return srcFirstName;
	}
	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}
	
	public String getSrcLastName() {
		return srcLastName;
	}
	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}
	
	public String getSrcTelNo() {
		return srcTelNo;
	}
	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}
	
	public String getSrcEmail() {
		return srcEmail;
	}
	public void setSrcEmail(String srcEmail) {
		this.srcEmail = srcEmail;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}
	
	public String getTypeOfPlaceText() {
		return typeOfPlaceText;
	}
	public void setTypeOfPlaceText(String typeOfPlaceText) {
		this.typeOfPlaceText = typeOfPlaceText;
	}

	public Float getReportLat() {
		return reportLat;
	}

	public void setReportLat(Float reportLat) {
		this.reportLat = reportLat;
	}

	public Float getReportLon() {
		return reportLon;
	}

	public void setReportLon(Float reportLon) {
		this.reportLon = reportLon;
	}

	@Override
	public String toString() {
		String diseaseString = disease == null ? "" : disease.toString();
		String eventTypeString = diseaseString.isEmpty() ? eventType.toString() : eventType.toString().toLowerCase();
		return diseaseString + " " + eventTypeString + " on " + DateHelper.formatDate(eventDate);
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
