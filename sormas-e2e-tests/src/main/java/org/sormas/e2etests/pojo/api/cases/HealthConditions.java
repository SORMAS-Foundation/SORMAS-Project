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
public class HealthConditions {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public String tuberculosis;
  public String asplenia;
  public String hepatitis;
  public String diabetes;
  public String hiv;
  public String hivArt;
  public String chronicLiverDisease;
  public String malignancyChemotherapy;
  public String chronicHeartFailure;
  public String chronicPulmonaryDisease;
  public String chronicKidneyDisease;
  public String chronicNeurologicCondition;
  public String downSyndrome;
  public String congenitalSyphilis;
  public String immunodeficiencyOtherThanHiv;
  public String cardiovascularDiseaseIncludingHypertension;
  public String obesity;
  public String currentSmoker;
  public String formerSmoker;
  public String asthma;
  public String sickleCellDisease;
  public String immunodeficiencyIncludingHiv;
  public String otherConditions;
}
