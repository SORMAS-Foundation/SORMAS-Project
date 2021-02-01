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

public class EventParticipantJurisdictionDto implements Serializable {

	private String eventParticipantUuid;
	private String reportingUserUuid;
	private String regionUuid;
	private String districtUuid;
	private String eventUuid;

	public EventParticipantJurisdictionDto() {
	}

	public EventParticipantJurisdictionDto(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public EventParticipantJurisdictionDto(
		String eventParticipantUuid,
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String eventUuid) {
		this.eventParticipantUuid = eventParticipantUuid;
		this.reportingUserUuid = reportingUserUuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.eventUuid = eventUuid;
	}

	public String getEventParticipantUuid() {
		return eventParticipantUuid;
	}

	public void setEventParticipantUuid(String eventParticipantUuid) {
		this.eventParticipantUuid = eventParticipantUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}
}
