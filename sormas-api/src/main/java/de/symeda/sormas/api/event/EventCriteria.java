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

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class EventCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 2194071020732246594L;

	public static final String EVENT_DATE_FROM = "eventDateFrom";
	public static final String EVENT_DATE_TO = "eventDateTo";
	public static final String REPORTING_USER_ROLE = "reportingUserRole";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String FREE_TEXT = "freeText";

	private EventStatus eventStatus;
	private Disease disease;
	private UserRole reportingUserRole;
	private Boolean deleted = Boolean.FALSE;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Date reportedDateFrom;
	private Date reportedDateTo;
	private EntityRelevanceStatus relevanceStatus;
	private Date eventDateFrom;
	private Date eventDateTo;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private UserReferenceDto surveillanceOfficer;
	private String freeText;

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public EventCriteria eventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public EventCriteria disease(Disease disease) {
		setDisease(disease);
		return this;
	}

	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public void setReportingUserRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
	}

	public EventCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public EventCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	public EventCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return this.region;
	}

	public EventCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return this.district;
	}

	/**
	 * @param reportedDateTo
	 *            will automatically be set to the end of the day
	 */
	public EventCriteria reportedBetween(Date reportedDateFrom, Date reportedDateTo) {

		this.reportedDateFrom = reportedDateFrom;
		this.reportedDateTo = reportedDateTo;
		return this;
	}

	public EventCriteria reportedDateFrom(Date reportedDateFrom) {
		this.reportedDateFrom = reportedDateFrom;
		return this;
	}

	public Date getReportedDateFrom() {
		return reportedDateFrom;
	}

	public EventCriteria reportedDateTo(Date reportedDateTo) {
		this.reportedDateTo = reportedDateTo;
		return this;
	}

	public Date getReportedDateTo() {
		return reportedDateTo;
	}

	public EventCriteria eventDateBetween(Date eventDateFrom, Date eventDateTo, DateFilterOption dateFilterOption) {
		this.eventDateFrom = eventDateFrom;
		this.eventDateTo = eventDateTo;
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public EventCriteria eventDateFrom(Date eventDateFrom) {
		this.eventDateFrom = eventDateFrom;
		return this;
	}

	public Date getEventDateFrom() {
		return eventDateFrom;
	}

	public EventCriteria eventDateTo(Date eventDateTo) {
		this.eventDateTo = eventDateTo;
		return this;
	}

	public Date getEventDateTo() {
		return eventDateTo;
	}

	public EventCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public EventCriteria surveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
		return this;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public EventCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}
}
