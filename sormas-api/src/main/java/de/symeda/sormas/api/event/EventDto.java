package de.symeda.sormas.api.event;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.location.LocationDto;
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
	public static final String DISEASE = "disease";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String TYPE_OF_PLACE_TEXT = "typeOfPlaceText";
	
	private EventType eventType;
	private EventStatus eventStatus;
	private String eventDesc;
	private Date eventDate;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private LocationDto eventLocation;
	private TypeOfPlace typeOfPlace;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcEmail;
	private Disease disease;
	private UserReferenceDto surveillanceOfficer;
	private String typeOfPlaceText;
	
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

	public LocationDto getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(LocationDto eventLocation) {
		this.eventLocation = eventLocation;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	
	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}
	
	public String getTypeOfPlaceText() {
		return typeOfPlaceText;
	}
	
	public void setTypeOfPlaceText(String typeOfPlaceText) {
		this.typeOfPlaceText = typeOfPlaceText;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventDto other = (EventDto) obj;
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
