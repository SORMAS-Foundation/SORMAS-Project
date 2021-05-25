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
public class Hospitalization {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public String admittedToHealthFacility;
  public Date admissionDate;
  public Date dischargeDate;
  public String isolated;
  public Date isolationDate;
  public String leftAgainstAdvice;
  public String hospitalizedPreviously;
  public List<PreviousHospitalization> previousHospitalizations;
  public String intensiveCareUnit;
  public Date intensiveCareUnitStart;
  public Date intensiveCareUnitEnd;
  public String hospitalizationReason;
  public String otherHospitalizationReason;
}
