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

package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.pojo.web.Case;

public class CaseService {
  private final Faker faker;

  @Inject
  public CaseService(Faker faker) {
    this.faker = faker;
  }

  public Case buildGeneratedCase() {
    return Case.builder()
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeDescription(faker.address().streetAddressNumber())
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .dateOfBirth(LocalDate.of(1902, 3, 7))
        .sex("Male")
        .nationalHealthId(UUID.randomUUID().toString())
        .passportNumber(String.valueOf(System.currentTimeMillis()))
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(faker.internet().emailAddress())
        .build();
  }

  public Case buildEditGeneratedCase() {
    return Case.builder()
        .dateOfReport(LocalDate.now().minusDays(3))
        .caseClassification("Confirmed case with unknown symptoms")
        .clinicalConfirmation("Yes")
        .epidemiologicalConfirmation("Yes")
        .laboratoryDiagnosticConfirmation("Yes")
        .investigationStatus("INVESTIGATION DONE")
        .caseOrigin("IN-COUNTRY")
        .externalId(UUID.randomUUID().toString())
        .externalToken(UUID.randomUUID().toString())
        .disease("COVID-19")
        .reinfection("NO")
        .outcomeOfCase("RECOVERED")
        .reportingDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .caseIdentificationSource("Suspicion report")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .prohibitionToWork("NO")
        .homeBasedQuarantinePossible("NO")
        .quarantine("None")
        .reportGpsLatitude("21")
        .reportGpsLongitude("21")
        .reportGpsAccuracyInM("21")
        .sequelae("NO")
        .bloodOrganTissueDonationInTheLast6Months("NO")
        .vaccinationStatusForThisDisease("Unvaccinated")
        .responsibleSurveillanceOfficer("Surveillance OFFICER - Surveillance Officer")
        .dateReceivedAtDistrictLevel(LocalDate.now().minusDays(1))
        .dateReceivedAtRegionLevel(LocalDate.now().minusDays(2))
        .dateReceivedAtNationalLevel(LocalDate.now().minusDays(3))
        .dateReceivedAtNationalLevel(LocalDate.now().minusDays(3))
        .generalComment(faker.book().title())
        .placeDescription(faker.business().creditCardExpiry())
        .build();
  }

  public Case buildCaseForLineListingFeature() {
    return Case.builder()
        .disease(DiseasesValues.MONKEYPOX.getDiseaseCaption())
        .region("Voreingestellte")
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .facilityCategory("Accommodation")
        .facilityType("Other Accommodation")
        .dateOfReport(LocalDate.now().minusDays(1))
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .placeDescription(faker.address().streetAddressNumber()) // used for Facility Name
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName() + LocalDate.now())
        .dateOfBirth(LocalDate.of(1902, 3, 7))
        .sex("Male")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .build();
  }
}
