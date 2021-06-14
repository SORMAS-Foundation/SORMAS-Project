package org.sormas.e2etests.pojo.web;

import java.time.LocalDate;
import lombok.*;
import org.sormas.e2etests.enums.YesNoUnknownOptions;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class ExposureDetails {
  LocalDate startOfExposure;
  LocalDate endOfExposure;
  String exposureDescription;
  String typeOfActivity;
  String exposureDetailsRole;
  String riskArea;
  String indoors;
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
