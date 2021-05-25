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
public class Location {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public Country country;
  public Region region;
  public District district;
  public Community community;
  public String details;
  public String city;
  public String areaType;
  public int latitude;
  public int longitude;
  public int latLonAccuracy;
  public String postalCode;
  public String street;
  public String houseNumber;
  public String additionalInformation;
  public String addressType;
  public String addressTypeDetails;
  public String facilityType;
  public Facility facility;
  public String facilityDetails;
}
