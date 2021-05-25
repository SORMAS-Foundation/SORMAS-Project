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
public class PreviousHospitalization {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public Date admissionDate;
  public Date dischargeDate;
  public Region region;
  public District district;
  public Community community;
  public HealthFacility healthFacility;
  public String healthFacilityDetails;
  public String isolated;
  public String description;
  public String hospitalizationReason;
  public String otherHospitalizationReason;
}
