/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;

public class CaseService {
  private final Faker faker;

  private String firstName;
  private String lastName;
  private final String emailDomain = "@CASE.com";

  @Inject
  public CaseService(Faker faker) {
    this.faker = faker;
  }

  public Case buildEditGeneratedCaseForPositivePathogenTestResult() {
    return Case.builder()
        .dateOfReport(LocalDate.now().minusDays(1))
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeDescription(faker.harryPotter().location())
        .build();
  }

  public Case buildGeneratedCaseForOnePerson(
      String firstName, String lastName, LocalDate dateOfBirth) {
    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease(DiseasesValues.getRandomDiseaseCaption())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeDescription(faker.harryPotter().location())
        .dateOfBirth(dateOfBirth)
        .sex(GenderValues.getRandomGender())
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .build();
  }

  public Case buildGeneratedCase() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeDescription(faker.harryPotter().location())
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .build();
  }

  public Case buildCaseWithDisease(String diseaseValue) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease(DiseasesValues.getCaptionFor(diseaseValue))
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeDescription(faker.harryPotter().location())
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .build();
  }

  public Case buildGeneratedCaseDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IM LAND")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease("COVID-19")
        .diseaseVariant("B.1.617.1")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("ZUHAUSE")
        .placeDescription(faker.harryPotter().location())
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGenderDE())
        .presentConditionOfPerson("Lebendig")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .outcomeOfCase("VERSTORBEN")
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
        .placeDescription(faker.harryPotter().location() + "2")
        .build();
  }

  public Case buildCaseForLineListingFeatureDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseCaption())
        .region("Voreingestellte")
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .facilityCategory("Beherbergungsst\u00E4tten")
        .facilityType("Andere Beherbergungsst\u00E4tte")
        .facility("Andere Einrichtung")
        .dateOfReport(LocalDate.now().minusDays(8)) // fix for line listing, don't touch!
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .placeDescription(faker.harryPotter().location()) // used for Facility Name
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGenderDE())
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .build();
  }

  public Case buildCaseForLineListingFeature() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .disease(DiseasesValues.MONKEYPOX.getDiseaseCaption())
        .region("Voreingestellte")
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .facilityCategory("Accommodation")
        .facilityType("Other Accommodation")
        .dateOfReport(LocalDate.now().minusDays(8)) // fix for line listing, don't change!
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .placeDescription(faker.address().streetAddressNumber()) // used for Facility Name
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .build();
  }

  public Case buildAddress() {
    return Case.builder()
        .country("Germany")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory("Accommodation")
        .facilityType("Campsite")
        .facility("Other facility")
        .facilityNameAndDescription("Dummy description" + System.currentTimeMillis())
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation("Dummy description" + System.currentTimeMillis())
        .postalCode(faker.address().zipCode())
        .city(faker.address().cityName())
        .areaType("Urban")
        .build();
  }
}
