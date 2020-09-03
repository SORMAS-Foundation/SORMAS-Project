/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;

public class DashboardEventDto implements Serializable {

	private static final long serialVersionUID = -4108181804263076837L;

	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_STATUS = "eventStatus";
	public static final String DISEASE = "disease";

	private String uuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String diseaseDetails;
	private Date eventDate;
	private Double reportLat;
	private Double reportLon;
	private Double locationLat;
	private Double locationLon;
	private String districtUuid;
	private DistrictReferenceDto district;

	public DashboardEventDto(
		String uuid,
		EventStatus eventStatus,
		Disease disease,
		String diseaseDetails,
		Date eventDate,
		Double reportLat,
		Double reportLon,
		Double locationLat,
		Double locationLon,
		String districtUuid) {

		this.uuid = uuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.eventDate = eventDate;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.locationLat = locationLat;
		this.locationLon = locationLon;
		this.districtUuid = districtUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public Double getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(Double locationLat) {
		this.locationLat = locationLat;
	}

	public Double getLocationLon() {
		return locationLon;
	}

	public void setLocationLon(Double locationLon) {
		this.locationLon = locationLon;
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
		return EventReferenceDto.buildCaption(getDisease(), getDiseaseDetails(), getEventStatus(), getEventDate());
	}
}
