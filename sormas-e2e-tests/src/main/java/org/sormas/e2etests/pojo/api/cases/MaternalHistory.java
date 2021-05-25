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
public class MaternalHistory {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public int childrenNumber;
  public int ageAtBirth;
  public String conjunctivitis;
  public Date conjunctivitisOnset;
  public int conjunctivitisMonth;
  public String maculopapularRash;
  public Date maculopapularRashOnset;
  public int maculopapularRashMonth;
  public String swollenLymphs;
  public Date swollenLymphsOnset;
  public int swollenLymphsMonth;
  public String arthralgiaArthritis;
  public Date arthralgiaArthritisOnset;
  public int arthralgiaArthritisMonth;
  public String otherComplications;
  public Date otherComplicationsOnset;
  public int otherComplicationsMonth;
  public String otherComplicationsDetails;
  public String rubella;
  public Date rubellaOnset;
  public String rashExposure;
  public Date rashExposureDate;
  public int rashExposureMonth;
  public RashExposureRegion rashExposureRegion;
  public RashExposureDistrict rashExposureDistrict;
  public RashExposureCommunity rashExposureCommunity;
}
