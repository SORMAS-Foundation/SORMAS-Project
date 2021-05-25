package org.sormas.e2etests.pojo.api.cases;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class EpiData {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public String exposureDetailsKnown;
  public String activityAsCaseDetailsKnown;
  public String contactWithSourceCaseKnown;
  public String highTransmissionRiskArea;
  public String largeOutbreaksArea;
  public String areaInfectedAnimals;
  public List<Exposure> exposures;
  public List<ActivitiesAsCase> activitiesAsCase;
}
