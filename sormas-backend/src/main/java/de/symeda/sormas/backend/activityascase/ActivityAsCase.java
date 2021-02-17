/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.activityascase;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureRole;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;

@Entity(name = "activityascase")
@Audited
public class ActivityAsCase extends AbstractDomainObject {

	private static final long serialVersionUID = -5570515874416024602L;

	public static final String TABLE_NAME = "activityascase";

	public static final String EPI_DATA = "epiData";
	public static final String LOCATION = "location";

	private EpiData epiData;
	private User reportingUser;
	private Date startDate;
	private Date endDate;
	private String description;
	private ActivityAsCaseType activityAsCaseType;
	private String activityAsCaseTypeDetails;
	private Location location;
	private ExposureRole role;

	// Type of Place
	private TypeOfPlace typeOfPlace;
	private String typeOfPlaceDetails;
	private MeansOfTransport meansOfTransport;
	private String meansOfTransportDetails;
	private String connectionNumber;
	private String seatNumber;
	private WorkEnvironment workEnvironment;

	// Exposure sub-types
	private GatheringType gatheringType;
	private String gatheringDetails;
	private HabitationType habitationType;
	private String habitationDetails;

	@ManyToOne
	@JoinColumn(nullable = false)
	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	@ManyToOne
	@JoinColumn
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ActivityAsCaseType getActivityAsCaseType() {
		return activityAsCaseType;
	}

	public void setActivityAsCaseType(ActivityAsCaseType activityAsCaseType) {
		this.activityAsCaseType = activityAsCaseType;
	}

	@Column(columnDefinition = "text")
	public String getActivityAsCaseTypeDetails() {
		return activityAsCaseTypeDetails;
	}

	public void setActivityAsCaseTypeDetails(String activityAsCaseTypeDetails) {
		this.activityAsCaseTypeDetails = activityAsCaseTypeDetails;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	public Location getLocation() {
		if (location == null) {
			location = new Location();
		}
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Enumerated(EnumType.STRING)
	public ExposureRole getRole() {
		return role;
	}

	public void setRole(ExposureRole role) {
		this.role = role;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	@Column(columnDefinition = "text")
	public String getTypeOfPlaceDetails() {
		return typeOfPlaceDetails;
	}

	public void setTypeOfPlaceDetails(String typeOfPlaceDetails) {
		this.typeOfPlaceDetails = typeOfPlaceDetails;
	}

	@Enumerated(EnumType.STRING)
	public MeansOfTransport getMeansOfTransport() {
		return meansOfTransport;
	}

	public void setMeansOfTransport(MeansOfTransport meansOfTransport) {
		this.meansOfTransport = meansOfTransport;
	}

	@Column(columnDefinition = "text")
	public String getMeansOfTransportDetails() {
		return meansOfTransportDetails;
	}

	public void setMeansOfTransportDetails(String meansOfTransportDetails) {
		this.meansOfTransportDetails = meansOfTransportDetails;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getConnectionNumber() {
		return connectionNumber;
	}

	public void setConnectionNumber(String connectionNumber) {
		this.connectionNumber = connectionNumber;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	@Enumerated(EnumType.STRING)
	public WorkEnvironment getWorkEnvironment() {
		return workEnvironment;
	}

	public void setWorkEnvironment(WorkEnvironment workEnvironment) {
		this.workEnvironment = workEnvironment;
	}

	@Enumerated(EnumType.STRING)
	public GatheringType getGatheringType() {
		return gatheringType;
	}

	public void setGatheringType(GatheringType gatheringType) {
		this.gatheringType = gatheringType;
	}

	@Column(columnDefinition = "text")
	public String getGatheringDetails() {
		return gatheringDetails;
	}

	public void setGatheringDetails(String gatheringDetails) {
		this.gatheringDetails = gatheringDetails;
	}

	@Enumerated(EnumType.STRING)
	public HabitationType getHabitationType() {
		return habitationType;
	}

	public void setHabitationType(HabitationType habitationType) {
		this.habitationType = habitationType;
	}

	@Column(columnDefinition = "text")
	public String getHabitationDetails() {
		return habitationDetails;
	}

	public void setHabitationDetails(String habitationDetails) {
		this.habitationDetails = habitationDetails;
	}
}
