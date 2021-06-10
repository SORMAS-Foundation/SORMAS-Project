package org.sormas.e2etests.pojo.web.epidemiologicalData;

import lombok.*;

import java.time.LocalDate;
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, ess = essLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Activity {

  String activityDetailsKnown;
  String activityDetailsNewEntry;
  LocalDate startOfActivity;
  LocalDate endOfActivity;
  String description;
  String typeOfActivity;
  String role;
  String typeOfPlace;
  String continent;
  String subcontinent;
  String country;
  String region;
  String district;
  String community;
  String street;
  String houseNumber;
  String additionalInformation;
  String postalCode;
  String city;
  String areaType;
  String details;
  String residingAreaWithRisk;
  String largeOutbreaksArea;
}
