package org.sormas.e2etests.entities.pojo.web.epidemiologicalData;

import java.time.LocalDate;
import lombok.*;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Exposure {
  LocalDate startOfExposure;
  LocalDate endOfExposure;
  String exposureDescription;
  TypeOfActivityExposure typeOfActivity;
  ExposureDetailsRole exposureDetailsRole;
  YesNoUnknownOptions riskArea;
  YesNoUnknownOptions indoors;
  YesNoUnknownOptions outdoors;
  YesNoUnknownOptions wearingMask;
  YesNoUnknownOptions wearingPpe;
  YesNoUnknownOptions otherProtectiveMeasures;
  YesNoUnknownOptions shortDistance;
  YesNoUnknownOptions longFaceToFaceContact;
  YesNoUnknownOptions animalMarket;
  YesNoUnknownOptions percutaneous;
  YesNoUnknownOptions contactToBodyFluids;
  YesNoUnknownOptions handlingSamples;
  String contactToSourceCase;
  TypeOfPlace typeOfPlace;
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
}
