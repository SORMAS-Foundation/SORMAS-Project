package de.symeda.sormas.api.event;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class EventDto extends EventReferenceDto {

	private static final long serialVersionUID = 2430932452606853497L;
	
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
	
	private EventType eventType;
	private EventStatus eventStatus;
	private String eventDesc;
	private Date eventDate;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private LocationReferenceDto eventLocation;
	private TypeOfPlace typeOfPlace;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcEmail;
	
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
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getEventDate() {
		return eventDate;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
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

	public LocationReferenceDto getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(LocationReferenceDto eventLocation) {
		this.eventLocation = eventLocation;
	}
	
}
