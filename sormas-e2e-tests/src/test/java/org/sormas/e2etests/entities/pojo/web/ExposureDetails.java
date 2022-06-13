package org.sormas.e2etests.entities.pojo.web;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class ExposureDetails {
  String startOfExposure;
  String endOfExposure;
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
  String gpsLatitude;
  String gpsLongitude;
  String gpsAccuracy;
}
