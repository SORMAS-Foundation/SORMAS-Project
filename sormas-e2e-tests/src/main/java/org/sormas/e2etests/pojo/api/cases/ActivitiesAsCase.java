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
public class ActivitiesAsCase {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public ReportingUser reportingUser;
  public Date startDate;
  public Date endDate;
  public String description;
  public String activityAsCaseType;
  public String activityAsCaseTypeDetails;
  public Location location;
  public String role;
  public String typeOfPlace;
  public String typeOfPlaceDetails;
  public String meansOfTransport;
  public String meansOfTransportDetails;
  public String connectionNumber;
  public String seatNumber;
  public String workEnvironment;
  public String gatheringType;
  public String gatheringDetails;
  public String habitationType;
  public String habitationDetails;
}
