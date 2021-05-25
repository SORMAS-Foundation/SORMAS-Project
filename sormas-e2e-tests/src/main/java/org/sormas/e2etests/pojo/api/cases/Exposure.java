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
public class Exposure {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public boolean pseudonymized;
  public ReportingUser reportingUser;
  public Date startDate;
  public Date endDate;
  public String description;
  public String exposureType;
  public String exposureTypeDetails;
  public Location location;
  public String exposureRole;
  public String typeOfPlace;
  public String typeOfPlaceDetails;
  public String meansOfTransport;
  public String meansOfTransportDetails;
  public String connectionNumber;
  public String seatNumber;
  public String workEnvironment;
  public String indoors;
  public String outdoors;
  public String wearingMask;
  public String wearingPpe;
  public String otherProtectiveMeasures;
  public String protectiveMeasuresDetails;
  public String shortDistance;
  public String longFaceToFaceContact;
  public String animalMarket;
  public String percutaneous;
  public String contactToBodyFluids;
  public String handlingSamples;
  public String eatingRawAnimalProducts;
  public String handlingAnimals;
  public String animalCondition;
  public String animalVaccinated;
  public String animalContactType;
  public String animalContactTypeDetails;
  public String bodyOfWater;
  public String waterSource;
  public String waterSourceDetails;
  public ContactToCase contactToCase;
  public String prophylaxis;
  public Date prophylaxisDate;
  public String riskArea;
  public String gatheringType;
  public String gatheringDetails;
  public String habitationType;
  public String habitationDetails;
  public String typeOfAnimal;
  public String typeOfAnimalDetails;
  public String physicalContactDuringPreparation;
  public String physicalContactWithBody;
  public String deceasedPersonIll;
  public String deceasedPersonName;
  public String deceasedPersonRelation;
}
