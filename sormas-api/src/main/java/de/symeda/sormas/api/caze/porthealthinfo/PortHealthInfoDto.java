package de.symeda.sormas.api.caze.porthealthinfo;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.YesNoUnknown;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for tracing information related to airports, seaports and ground crossings")
@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class PortHealthInfoDto extends EntityDto {

	private static final long serialVersionUID = 3289289799891965437L;

	public static final String I18N_PREFIX = "PortHealthInfo";

	public static final String AIRLINE_NAME = "airlineName";
	public static final String FLIGHT_NUMBER = "flightNumber";
	public static final String DEPARTURE_DATE_TIME = "departureDateTime";
	public static final String ARRIVAL_DATE_TIME = "arrivalDateTime";
	public static final String FREE_SEATING = "freeSeating";
	public static final String SEAT_NUMBER = "seatNumber";
	public static final String DEPARTURE_AIRPORT = "departureAirport";
	public static final String NUMBER_OF_TRANSIT_STOPS = "numberOfTransitStops";
	public static final String TRANSIT_STOP_DETAILS_1 = "transitStopDetails1";
	public static final String TRANSIT_STOP_DETAILS_2 = "transitStopDetails2";
	public static final String TRANSIT_STOP_DETAILS_3 = "transitStopDetails3";
	public static final String TRANSIT_STOP_DETAILS_4 = "transitStopDetails4";
	public static final String TRANSIT_STOP_DETAILS_5 = "transitStopDetails5";
	public static final String VESSEL_NAME = "vesselName";
	public static final String VESSEL_DETAILS = "vesselDetails";
	public static final String PORT_OF_DEPARTURE = "portOfDeparture";
	public static final String LAST_PORT_OF_CALL = "lastPortOfCall";
	public static final String CONVEYANCE_TYPE = "conveyanceType";
	public static final String CONVEYANCE_TYPE_DETAILS = "conveyanceTypeDetails";
	public static final String DEPARTURE_LOCATION = "departureLocation";
	public static final String FINAL_DESTINATION = "finalDestination";
	public static final String DETAILS = "details";

	// Airport
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Name of the airline the person used. Relevant at airports")
	private String airlineName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Number of the flights the person was on. Relevant at airports")
	private String flightNumber;
	@Schema(description = "Date and time when the flight departed. Relevant at airports")
	private Date departureDateTime;
	@Schema(description = "Date and time when the flight arrived. Relevant at airports")
	private Date arrivalDateTime;
	@Schema(description = "Whether the flight had free seating. Relevant at airports")
	private YesNoUnknown freeSeating;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "The person's seat number. Relevant for tracing at airports")
	private String seatNumber;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "The airport the person originally departed from. Relevant at airports")
	private String departureAirport;
	@Schema(description = "Number of transit stops of the connection. Relevant at airports")
	private Integer numberOfTransitStops;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the first transit stop. Relevant at airports")
	private String transitStopDetails1;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the second transit stop. Relevant at airports")
	private String transitStopDetails2;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the third transit stop. Relevant at airports")
	private String transitStopDetails3;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the fourth transit stop. Relevant at airports")
	private String transitStopDetails4;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the fifth transit stop. Relevant at airports")
	private String transitStopDetails5;

	// Seaport
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Name of the vessel the person used. Relevant at seaports")
	private String vesselName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the vessel the person used. Relevant at seaports")
	private String vesselDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Port the vessel originally departed from. Relevant at seaports")
	private String portOfDeparture;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "The vessel's last port of call before reaching the destination port. Relevant at seaports")
	private String lastPortOfCall;

	// Ground Crossing
	private ConveyanceType conveyanceType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the type of overland conveyance. Relevant at ground crossings")
	private String conveyanceTypeDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Name of the location the person departed from. Relevant at ground crossings")
	private String departureLocation;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Final destination of the person's travel. Relevant at ground crossings")
	private String finalDestination;

	// Other
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	@Schema(description = "Free text details about another type of entry into a country")
	private String details;

	public static PortHealthInfoDto build() {
		PortHealthInfoDto portHealthInfo = new PortHealthInfoDto();
		portHealthInfo.setUuid(DataHelper.createUuid());
		return portHealthInfo;
	}

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
}
