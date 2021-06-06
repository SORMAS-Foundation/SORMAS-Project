/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class EpidemiologicalData {
  String exposureDetailsKnown;
  String exposureDetailsNewEntry;
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
  String doneButton;
  String discardButton;
  String activityDetailsKnown;
  String activityDetailsNewEntry;
  LocalDate accStartOfActivity;
  LocalDate accEndOfActivity;
  String accDescription;
  String accActivityAsCaseType;
  String accRole;
  String accTypeOfPlace;
  String accContinent;
  String accSubcontinent;
  String accCountry;
  String accRegion;
  String accDistrict;
  String accCommunity;
  String accStreet;
  String accHouseNumber;
  String accAdditionalInformation;
  String accPostalCode;
  String accCity;
  String accAreaType;
  String accDetails;
  String accDoneButton;
  String accDiscardButton;
  String residingAreaWithRisk;
  String largeOutbreaksArea;

}
