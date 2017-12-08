package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public class DashboardEventDto implements Serializable {

	private static final long serialVersionUID = -4108181804263076837L;

	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String DISEASE = "disease";

	private String uuid;
	private EventType eventType;
	private EventStatus eventStatus;
	private Disease disease;
	private String diseaseDetails;
	private Date eventDate;
	private Double reportLat;
	private Double reportLon;
	private String districtUuid;
	private DistrictReferenceDto district;
	
	public DashboardEventDto(String uuid, EventType eventType, EventStatus eventStatus, Disease disease, String diseaseDetails, Date eventDate, Double reportLat, Double reportLon, String districtUuid) {
		this.uuid = uuid;
		this.eventType = eventType;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.eventDate = eventDate;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.districtUuid = districtUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

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

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}
	
	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	@Override
	public String toString() {
		String diseaseString = getDisease() != Disease.OTHER
				? DataHelper.toStringNullable(getDisease())
				: DataHelper.toStringNullable(getDiseaseDetails());
		String eventTypeString = diseaseString.isEmpty() ? eventType.toString() : eventType.toString().toLowerCase();
		return diseaseString + " " + eventTypeString + " on " + DateHelper.formatDate(eventDate);
	}
	
}
