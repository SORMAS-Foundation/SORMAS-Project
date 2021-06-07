package org.sormas.e2etests.pojo.web.epidemiologicalData;

import lombok.*;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivity;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Exposure {
  LocalDate startOfExposure;
  LocalDate endOfExposure;
  String exposureDescription;
  TypeOfActivity typeOfActivity;
  ExposureDetailsRole exposureDetailsRole;
  YesNoUnknownOptions riskArea;
  YesNoUnknownOptions indoors;
  String outdoors;
  String wearingMask;
  String wearingPpe;
  String otherProtectiveMeasures;
  String shortDistance;
  String longFaceToFaceContact;
  String animalMarket;
  String percutaneous;
  String contactToBodyFluids;
  String handlingSamples;
  String contactToSourceCase;
  String typeOfPlace;
  String continent;
  String subcontinent;
  String country;
  String exposureRegion;
  String district;
  String community;
  String street;
  String houseNumber;
  String additionalInformation;
  String postalCode;
  String city;
  String areaType;
  String communityContactPerson;
}
