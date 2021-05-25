package org.sormas.e2etests.pojo.api.cases;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class PortHealthInfo {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public String airlineName;
  public String flightNumber;
  public Date departureDateTime;
  public Date arrivalDateTime;
  public String freeSeating;
  public String seatNumber;
  public String departureAirport;
  public int numberOfTransitStops;
  public String transitStopDetails1;
  public String transitStopDetails2;
  public String transitStopDetails3;
  public String transitStopDetails4;
  public String transitStopDetails5;
  public String vesselName;
  public String vesselDetails;
  public String portOfDeparture;
  public String lastPortOfCall;
  public String conveyanceType;
  public String conveyanceTypeDetails;
  public String departureLocation;
  public String finalDestination;
  public String details;
}
