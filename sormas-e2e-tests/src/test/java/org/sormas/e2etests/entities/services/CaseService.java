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

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import lombok.SneakyThrows;
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

  @SneakyThrows
  public Case buildGeneratedCaseForOnePerson(
      String firstName, String lastName, LocalDate dateOfBirth) {
    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
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

  public Case buildGeneratedCaseForOnePersonDE(
      String firstName, String lastName, LocalDate dateOfBirth) {
    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IM LAND")
        .dateOfReport(LocalDate.now().minusDays(1))
        .disease("COVID-19")
        .responsibleRegion("Baden-W\u00FCrttemberg")
        .responsibleDistrict("LK Alb-Donau-Kreis")
        .placeOfStay("ZUHAUSE")
        .dateOfBirth(dateOfBirth)
        .sex(GenderValues.getRandomGenderDE())
        .build();
  }

  @SneakyThrows
  public Case buildGeneratedCase() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .epidNumber(generateShortUUID())
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

  @SneakyThrows
  public Case buildGeneratedCaseWithCovidVariant(String covidVariant) {

    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .epidNumber(generateShortUUID())
        .disease("COVID-19")
        .diseaseVariant(covidVariant)
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

  @SneakyThrows
  public Case buildGeneratedCaseWithFacility() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .epidNumber(generateShortUUID())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("FACILITY")
        .facility("Other facility")
        .facilityNameAndDescription("MagicHospital")
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

  @SneakyThrows
  public Case buildGeneratedCaseWithCreatedFacilityDE(
      String facilityCategory, String facilityType, String facility) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .epidNumber(generateShortUUID())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("EINRICHTUNG")
        .facilityCategory(facilityCategory)
        .facilityType(facilityType)
        .facility(facility)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGenderDE())
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .build();
  }

  @SneakyThrows
  public Case buildCaseWithFacilityAndDifferentPlaceOfStay() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .disease("COVID-19")
        .responsibleRegion("Region1")
        .responsibleDistrict("District11")
        .placeOfStay("FACILITY")
        .placeOfStayRegion("Region2")
        .placeOfStayDistrict("District21")
        .facilityCategory("Medical facility")
        .facilityType("Hospital")
        .facility("Community212")
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
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
        //  .externalId(generateShortUUID())
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

  public Case buildGeneratedCaseWithPointOfEntryDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("EINREISEORT")
        .dateOfReport(LocalDate.now().minusDays(1))
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .pointOfEntryRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .pointOfEntryDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .pointOfEntry("Anderer Flughafen")
        .pointOfEntryDetails("Narita")
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGenderDE())
        .build();
  }

  @SneakyThrows
  public Case buildGeneratedCaseWithDifferentPlaceOfStay() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("HOME")
        .placeOfStayRegion("Berlin")
        .placeOfStayDistrict("SK Berlin Mitte")
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

  @SneakyThrows
  public Case buildCaseWithPointOfEntryAndDifferentPlaceOfStay() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("POINT OF ENTRY")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
        .disease("COVID-19")
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStayRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .placeOfStayDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .pointOfEntry("Voreingestellter Flughafen")
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .build();
  }

  @SneakyThrows
  public Case buildGeneratedCaseDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IM LAND")
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(generateShortUUID())
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

  @SneakyThrows
  public Case buildGeneratedCaseDEDaysAgo(int daysAgo) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IM LAND")
        .dateOfReport(LocalDate.now().minusDays(daysAgo))
        .externalId(generateShortUUID())
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
        .dateOfSymptomOnset(LocalDate.now().minusDays(daysAgo))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .outcomeOfCase("VERSTORBEN")
        .build();
  }

  @SneakyThrows
  public Case buildGeneratedCaseDEForOnePerson(
      String firstName,
      String lastName,
      LocalDate dateOfBirth,
      LocalDate reportDate,
      String personSex) {
    return Case.builder()
        .firstName(firstName)
        .lastName(lastName)
        .caseOrigin("IM LAND")
        .dateOfReport(reportDate)
        .externalId(generateShortUUID())
        .disease("COVID-19")
        .diseaseVariant("B.1.617.1")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .placeOfStay("ZUHAUSE")
        .placeDescription(faker.harryPotter().location())
        .dateOfBirth(dateOfBirth)
        .sex(personSex)
        .presentConditionOfPerson("Lebendig")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .outcomeOfCase("VERSTORBEN")
        .build();
  }

  @SneakyThrows
  public Case buildEditGeneratedCase() {
    return Case.builder()
        .dateOfReport(LocalDate.now().minusDays(3))
        .caseClassification("CONFIRMED CASE")
        .clinicalConfirmation("Yes")
        .epidemiologicalConfirmation("Yes")
        .laboratoryDiagnosticConfirmation("Yes")
        .investigationStatus("INVESTIGATION DONE")
        .caseOrigin("IN-COUNTRY")
        .externalId(generateShortUUID())
        .externalToken(generateShortUUID())
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
        .responsibleSurveillanceOfficer("Surveillance SUPERVISOR")
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

  @SneakyThrows
  public Case buildCaseForSurvnetFeature() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .dateOfReport(LocalDate.now())
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStay("ZUHAUSE")
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .build();
  }

  public Case buildCaseWithFacilitiesForSurvnetFeature() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .dateOfReport(LocalDate.now())
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStay("EINRICHTUNG")
        .facilityCategory("Medizinische Einrichtung")
        .facilityType("Krankenhaus")
        .facility("Andere Einrichtung")
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .build();
  }

  @SneakyThrows
  public Case buildCaseForSurvnetFeatureWithReinfection() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .dateOfReport(LocalDate.now())
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStay("ZUHAUSE")
        .reinfection("JA")
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .build();
  }

  @SneakyThrows
  public Case buildCaseForSurvnetFeatureWithDateOfBirth() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .dateOfReport(LocalDate.now())
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStay("ZUHAUSE")
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .build();
  }

  @SneakyThrows
  public Case buildCaseForSurvnetFeatureXMLCheck() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Case.builder()
        .dateOfReport(LocalDate.now())
        .responsibleRegion("Berlin")
        .responsibleDistrict("SK Berlin Mitte")
        .placeOfStay("ZUHAUSE")
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.MALE.getGenderDE())
        .build();
  }
}
