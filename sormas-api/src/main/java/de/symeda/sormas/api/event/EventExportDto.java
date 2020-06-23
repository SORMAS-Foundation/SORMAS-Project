/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Order;

public class EventExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventExport";

	private String uuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String diseaseDetails;
	private Date eventDate;
	private String eventDesc;
	private String region;
	private String district;
	private String community;
	private String city;
	private String address;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private Date reportDateTime;

	public EventExportDto(
		String uuid,
		EventStatus eventStatus,
		Disease disease,
		String diseaseDetails,
		Date eventDate,
		String eventDesc,
		String region,
		String district,
		String community,
		String city,
		String address,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		Date reportDateTime) {
		this.uuid = uuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.eventDate = eventDate;
		this.eventDesc = eventDesc;
		this.region = region;
		this.district = district;
		this.community = community;
		this.city = city;
		this.address = address;
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.reportDateTime = reportDateTime;
	}

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Order(2)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Order(3)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Order(4)
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@Order(5)
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	@Order(6)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Order(7)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(8)
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Order(9)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(10)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Order(11)
	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	@Order(12)
	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	@Order(13)
	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	@Order(14)
	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
}
