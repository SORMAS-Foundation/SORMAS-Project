/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.caze.porthealthinfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.caze.porthealthinfo.ConveyanceType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

@Entity(name = PortHealthInfo.TABLE_NAME)
@DatabaseTable(tableName = PortHealthInfo.TABLE_NAME)
@EmbeddedAdo
public class PortHealthInfo extends AbstractDomainObject {

	private static final long serialVersionUID = 3289289799891965437L;

	public static final String TABLE_NAME = "portHealthInfo";
	public static final String I18N_PREFIX = "PortHealthInfo";

	// Airport
	@Column(length = 512)
	private String airlineName;
	@Column(length = 512)
	private String flightNumber;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date departureDateTime;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date arrivalDateTime;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown freeSeating;
	@Column(length = 512)
	private String seatNumber;
	@Column(length = 512)
	private String departureAirport;
	@Column
	private Integer numberOfTransitStops;
	@Column(length = 512)
	private String transitStopDetails1;
	@Column(length = 512)
	private String transitStopDetails2;
	@Column(length = 512)
	private String transitStopDetails3;
	@Column(length = 512)
	private String transitStopDetails4;
	@Column(length = 512)
	private String transitStopDetails5;

	// Seaport
	@Column(length = 512)
	private String vesselName;
	@Column(length = 512)
	private String vesselDetails;
	@Column(length = 512)
	private String portOfDeparture;
	@Column(length = 512)
	private String lastPortOfCall;

	// Ground Crossing
	@Enumerated(EnumType.STRING)
	private ConveyanceType conveyanceType;
	@Column(length = 512)
	private String conveyanceTypeDetails;
	@Column(length = 512)
	private String departureLocation;
	@Column(length = 512)
	private String finalDestination;

	// Other
	@Column(length = 512)
	private String details;

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(Date departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(Date arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	public YesNoUnknown getFreeSeating() {
		return freeSeating;
	}

	public void setFreeSeating(YesNoUnknown freeSeating) {
		this.freeSeating = freeSeating;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public String getDepartureAirport() {
		return departureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public Integer getNumberOfTransitStops() {
		return numberOfTransitStops;
	}

	public void setNumberOfTransitStops(Integer numberOfTransitStops) {
		this.numberOfTransitStops = numberOfTransitStops;
	}

	public String getTransitStopDetails1() {
		return transitStopDetails1;
	}

	public void setTransitStopDetails1(String transitStopDetails1) {
		this.transitStopDetails1 = transitStopDetails1;
	}

	public String getTransitStopDetails2() {
		return transitStopDetails2;
	}

	public void setTransitStopDetails2(String transitStopDetails2) {
		this.transitStopDetails2 = transitStopDetails2;
	}

	public String getTransitStopDetails3() {
		return transitStopDetails3;
	}

	public void setTransitStopDetails3(String transitStopDetails3) {
		this.transitStopDetails3 = transitStopDetails3;
	}

	public String getTransitStopDetails4() {
		return transitStopDetails4;
	}

	public void setTransitStopDetails4(String transitStopDetails4) {
		this.transitStopDetails4 = transitStopDetails4;
	}

	public String getTransitStopDetails5() {
		return transitStopDetails5;
	}

	public void setTransitStopDetails5(String transitStopDetails5) {
		this.transitStopDetails5 = transitStopDetails5;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVesselDetails() {
		return vesselDetails;
	}

	public void setVesselDetails(String vesselDetails) {
		this.vesselDetails = vesselDetails;
	}

	public String getPortOfDeparture() {
		return portOfDeparture;
	}

	public void setPortOfDeparture(String portOfDeparture) {
		this.portOfDeparture = portOfDeparture;
	}

	public String getLastPortOfCall() {
		return lastPortOfCall;
	}

	public void setLastPortOfCall(String lastPortOfCall) {
		this.lastPortOfCall = lastPortOfCall;
	}

	public ConveyanceType getConveyanceType() {
		return conveyanceType;
	}

	public void setConveyanceType(ConveyanceType conveyanceType) {
		this.conveyanceType = conveyanceType;
	}

	public String getConveyanceTypeDetails() {
		return conveyanceTypeDetails;
	}

	public void setConveyanceTypeDetails(String conveyanceTypeDetails) {
		this.conveyanceTypeDetails = conveyanceTypeDetails;
	}

	public String getDepartureLocation() {
		return departureLocation;
	}

	public void setDepartureLocation(String departureLocation) {
		this.departureLocation = departureLocation;
	}

	public String getFinalDestination() {
		return finalDestination;
	}

	public void setFinalDestination(String finalDestination) {
		this.finalDestination = finalDestination;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
