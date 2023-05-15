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
import java.util.Random;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.FacilityCategory;
import org.sormas.e2etests.enums.FacilityType;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;

public class ContactService {
  private final Faker faker;
  private static Random random = new Random();

  private String firstName;
  private String lastName;
  private final String emailDomain = "@CONTACT.com";

  @Inject
  public ContactService(Faker faker) {
    this.faker = faker;
  }

  @SneakyThrows
  public Contact buildGeneratedContactDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Contact.builder()
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGenderDE())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .returningTraveler("NEIN")
        .reportDate(LocalDate.now().minusDays(random.nextInt(5)))
        .diseaseOfSourceCase("COVID-19")
        .caseIdInExternalSystem(generateShortUUID())
        .dateOfFirstContact(LocalDate.now().minusDays(9))
        .dateOfLastContact(LocalDate.now().minusDays(6))
        .caseOrEventInformation("Automated test dummy description")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .additionalInformationOnContactType("Automated test dummy description")
        .typeOfContact(
            "Personen mit direktem Kontakt zu Sekreten oder K\u00F6rperfl\u00FCssigkeiten")
        .contactCategory("Kontaktperson der Kategorie III")
        .relationshipWithCase("Arbeiten in der gleichen Umgebung")
        .descriptionOfHowContactTookPlace("Automated test dummy description")
        .build();
  }

  @SneakyThrows
  public Contact buildGeneratedContactWithParametrizedPersonDataDE(
      String firstName, String lastName, LocalDate dateOfBirth, String sex) {

    return Contact.builder()
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(dateOfBirth)
        .sex(sex)
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .returningTraveler("NEIN")
        .reportDate(LocalDate.now().minusDays(random.nextInt(5)))
        .diseaseOfSourceCase("COVID-19")
        .caseIdInExternalSystem(generateShortUUID())
        .dateOfFirstContact(LocalDate.now().minusDays(9))
        .dateOfLastContact(LocalDate.now().minusDays(6))
        .caseOrEventInformation("Automated test dummy description")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .additionalInformationOnContactType("Automated test dummy description")
        .typeOfContact(
            "Personen mit direktem Kontakt zu Sekreten oder K\u00F6rperfl\u00FCssigkeiten")
        .contactCategory("Kontaktperson der Kategorie III")
        .relationshipWithCase("Arbeiten in der gleichen Umgebung")
        .descriptionOfHowContactTookPlace("Automated test dummy description")
        .build();
  }

  @SneakyThrows
  public Contact buildGeneratedContactWithParametrizedPersonData(
      String firstName, String lastName, LocalDate dateOfBirth) {
    return Contact.builder()
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(dateOfBirth)
        .sex(GenderValues.getRandomGender())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .returningTraveler("NO")
        .reportDate(LocalDate.now())
        .diseaseOfSourceCase("COVID-19")
        .caseIdInExternalSystem(generateShortUUID())
        .dateOfFirstContact(LocalDate.now().minusDays(15))
        .dateOfLastContact(LocalDate.now().minusDays(13))
        .caseOrEventInformation("Automated test dummy description")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .additionalInformationOnContactType(
            "Automated test dummy description " + System.currentTimeMillis())
        .typeOfContact("Touched fluid of source case")
        .contactCategory("Low risk contact")
        .relationshipWithCase("Work in the same environment")
        .descriptionOfHowContactTookPlace(
            "Automated test dummy description " + System.currentTimeMillis())
        .build();
  }

  @SneakyThrows
  public Contact buildGeneratedContact() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Contact.builder()
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .returningTraveler("NO")
        .reportDate(LocalDate.now().minusDays(random.nextInt(10)))
        .diseaseOfSourceCase("COVID-19")
        .caseIdInExternalSystem(generateShortUUID())
        .dateOfFirstContact(LocalDate.now().minusDays(15))
        .dateOfLastContact(LocalDate.now().minusDays(13))
        .caseOrEventInformation("Automated test dummy description")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .additionalInformationOnContactType(
            "Automated test dummy description " + System.currentTimeMillis())
        .typeOfContact("Touched fluid of source case")
        .contactCategory("Low risk contact")
        .relationshipWithCase("Work in the same environment")
        .descriptionOfHowContactTookPlace(
            "Automated test dummy description " + System.currentTimeMillis())
        .build();
  }

  @SneakyThrows
  public Contact buildEditContact() {
    return Contact.builder()
        .classification("CONFIRMED CONTACT")
        .multiDay("Multi-day contact")
        .dateOfFirstContact(LocalDate.now().minusDays(3))
        .dateOfLastContact(LocalDate.now().minusDays(1))
        .diseaseOfSourceCase("Measles")
        .externalId(generateShortUUID())
        .externalToken(generateShortUUID().substring(0, 8))
        .reportDate(LocalDate.now())
        .reportingDistrict("District11")
        .responsibleRegion("Region1")
        .responsibleDistrict("District12")
        .responsibleCommunity("")
        .returningTraveler("YES")
        .caseIdInExternalSystem(generateShortUUID())
        .caseOrEventInformation("Random description for case or event")
        .identificationSource("Other")
        .identificationSourceDetails("random details")
        .typeOfContact("Direct physical contact with source case")
        .additionalInformationOnContactType("random contact type")
        .contactCategory("NO RISK CONTACT")
        .relationshipWithCase("Live in the same household")
        .descriptionOfHowContactTookPlace("description it took place randomly")
        .prohibitionToWork("UNKNOWN")
        .homeBasedQuarantinePossible("NO")
        .quarantine("None")
        .highPriority("High priority contact")
        .diabetes("YES")
        .immunodeficiencyIncludingHiv("NO")
        .liverDisease("NO")
        .malignancy("NO")
        .chronicPulmonaryDisease("NO")
        .renalDisease("YES")
        .chronicNeurologicalNeuromuscularDisease("NO")
        .cardiovascularDiseaseIncludingHypertension("NO")
        .additionalRelevantPreexistingConditions("dummy text preexisting conditions")
        .vaccinationStatusForThisDisease("Unvaccinated")
        .immunosuppressiveTherapy("NO")
        .activeInCare("NO")
        .cancelFollowUp(true)
        // TODO enable it back once 6803 is fixed
        // .overwriteFollowUp("Overwrite follow-up until date")
        // .dateOfFollowUpUntil(LocalDate.now().plusDays(15))
        .followUpStatusComment("dummy comment will resume, ofc")
        .responsibleContactOfficer("")
        .generalComment("last dummy comment here")
        .build();
  }

  public Exposure buildGeneratedExposureDataForContact() {
    return Exposure.builder()
        .startOfExposure(LocalDate.now().minusDays(3))
        .endOfExposure(LocalDate.now().minusDays(1))
        .exposureDescription(faker.medical().symptoms())
        .typeOfActivity(TypeOfActivityExposure.VISIT)
        .exposureDetailsRole(ExposureDetailsRole.MEDICAL_STAFF)
        .riskArea(YesNoUnknownOptions.NO)
        .indoors(YesNoUnknownOptions.YES)
        .outdoors(YesNoUnknownOptions.NO)
        .wearingMask(YesNoUnknownOptions.NO)
        .wearingPpe(YesNoUnknownOptions.NO)
        .otherProtectiveMeasures(YesNoUnknownOptions.NO)
        .shortDistance(YesNoUnknownOptions.YES)
        .longFaceToFaceContact(YesNoUnknownOptions.YES)
        .percutaneous(YesNoUnknownOptions.NO)
        .contactToBodyFluids(YesNoUnknownOptions.NO)
        .handlingSamples(YesNoUnknownOptions.NO)
        .typeOfPlace(TypeOfPlace.HOME)
        .continent("Africa")
        .subcontinent("Central Africa")
        .country("Cameroon")
        .build();
  }

  public Exposure buildGeneratedExposureDataContactForRandomInputs() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return Exposure.builder()
        .startOfExposure(LocalDate.now().minusDays(3))
        .endOfExposure(LocalDate.now().minusDays(1))
        .exposureDescription(faker.medical().symptoms())
        .typeOfActivity(TypeOfActivityExposure.VISIT)
        .exposureDetailsRole(ExposureDetailsRole.MEDICAL_STAFF)
        .riskArea(YesNoUnknownOptions.NO)
        .indoors(YesNoUnknownOptions.YES)
        .outdoors(YesNoUnknownOptions.NO)
        .wearingMask(YesNoUnknownOptions.NO)
        .wearingPpe(YesNoUnknownOptions.NO)
        .otherProtectiveMeasures(YesNoUnknownOptions.NO)
        .shortDistance(YesNoUnknownOptions.YES)
        .longFaceToFaceContact(YesNoUnknownOptions.YES)
        .percutaneous(YesNoUnknownOptions.NO)
        .contactToBodyFluids(YesNoUnknownOptions.NO)
        .handlingSamples(YesNoUnknownOptions.NO)
        .typeOfPlace(TypeOfPlace.HOME)
        .typeOfPlaceDetails(faker.address().fullAddress())
        .continent("Europe")
        .subcontinent("Western Europe")
        .country("Germany")
        .exposureRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .street(faker.address().streetAddress())
        .houseNumber(String.valueOf(faker.number().numberBetween(1, 99)))
        .additionalInformation(faker.address().streetAddress())
        .postalCode(faker.address().zipCode())
        .city(faker.address().cityName())
        .areaType("Urban")
        .latitude(faker.address().latitude())
        .longitude(faker.address().longitude())
        .latLonAccuracy(faker.address().latitude())
        .facilityCategory(FacilityCategory.ACCOMMODATION.getFacility())
        .facilityType(FacilityType.CAMPSITE.getType())
        .facility("Other facility")
        .facilityDetails("Other facility")
        .contactPersonFirstName(firstName)
        .contactPersonLastName(lastName)
        .contactPersonPhone(faker.phoneNumber().phoneNumber())
        .contactPersonEmail(firstName + lastName + emailDomain)
        .build();
  }

  public Exposure buildGeneratedExposureDataContactForRandomInputsDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return Exposure.builder()
        .startOfExposure(LocalDate.now().minusDays(3))
        .endOfExposure(LocalDate.now().minusDays(1))
        .exposureDescription(faker.medical().symptoms())
        .typeOfActivity(TypeOfActivityExposure.VISIT)
        .exposureDetailsRole(ExposureDetailsRole.MEDICAL_STAFF)
        .riskArea(YesNoUnknownOptions.NO)
        .indoors(YesNoUnknownOptions.YES)
        .outdoors(YesNoUnknownOptions.NO)
        .wearingMask(YesNoUnknownOptions.NO)
        .wearingPpe(YesNoUnknownOptions.NO)
        .otherProtectiveMeasures(YesNoUnknownOptions.NO)
        .shortDistance(YesNoUnknownOptions.YES)
        .longFaceToFaceContact(YesNoUnknownOptions.YES)
        .percutaneous(YesNoUnknownOptions.NO)
        .contactToBodyFluids(YesNoUnknownOptions.NO)
        .handlingSamples(YesNoUnknownOptions.NO)
        .typeOfPlace(TypeOfPlace.HOME)
        .typeOfPlaceDetails(faker.address().fullAddress())
        .continent("Europa")
        .subcontinent("Westeuropa")
        .country("Deutschland")
        .exposureRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .street(faker.address().streetAddress())
        .houseNumber(String.valueOf(faker.number().numberBetween(1, 99)))
        .additionalInformation(faker.address().streetAddress())
        .postalCode(faker.address().zipCode())
        .city(faker.address().cityName())
        .areaType("St\u00E4dtisch")
        .latitude(faker.address().latitude())
        .longitude(faker.address().longitude())
        .latLonAccuracy(faker.address().latitude())
        .facilityCategory(FacilityCategory.ACCOMMODATION.getFacilityDE())
        .facilityType(FacilityType.CAMPSITE.getTypeDE())
        .facility(faker.book().title())
        .facilityDetails("Andere Einrichtung")
        .contactPersonFirstName(firstName)
        .contactPersonLastName(lastName)
        .contactPersonPhone(faker.phoneNumber().phoneNumber())
        .contactPersonEmail(firstName + "." + lastName + emailDomain)
        .build();
  }
}
