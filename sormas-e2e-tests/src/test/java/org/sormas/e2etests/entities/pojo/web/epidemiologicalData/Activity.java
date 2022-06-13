package org.sormas.e2etests.entities.pojo.web.epidemiologicalData;

import java.time.LocalDate;
import lombok.*;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ActivityAsCaseType;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ActivityDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Activity {

  String activityDetailsKnown;
  String activityDetailsNewEntry;
  LocalDate startOfActivity;
  LocalDate endOfActivity;
  String description;
  ActivityAsCaseType typeOfActivity;
  ActivityDetailsRole role;
  TypeOfPlace typeOfPlace;
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
