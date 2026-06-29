/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.externalmessage.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class ExternalMessageMapper {

	private static final Logger logger = LoggerFactory.getLogger(ExternalMessageMapper.class);

	private final ExternalMessageDto externalMessage;

	private final ExternalMessageProcessingFacade processingFacade;

	public ExternalMessageMapper(ExternalMessageDto externalMessage, ExternalMessageProcessingFacade processingFacade) {
		this.externalMessage = externalMessage;
		this.processingFacade = processingFacade;
	}

	public ExternalMessageDto getExternalMessage() {
		return externalMessage;
	}

	public List<String[]> mapToPerson(PersonDto person) {
		List<String[]> changedFields = map(
			Stream.of(
				Mapping.of(person::setFirstName, person.getFirstName(), externalMessage.getPersonFirstName(), PersonDto.FIRST_NAME),
				Mapping.of(person::setLastName, person.getLastName(), externalMessage.getPersonLastName(), PersonDto.LAST_NAME),
				Mapping.of(person::setBirthdateDD, person.getBirthdateDD(), externalMessage.getPersonBirthDateDD(), PersonDto.BIRTH_DATE_DD),
				Mapping.of(person::setBirthdateMM, person.getBirthdateMM(), externalMessage.getPersonBirthDateMM(), PersonDto.BIRTH_DATE_MM),
				Mapping.of(person::setBirthdateYYYY, person.getBirthdateYYYY(), externalMessage.getPersonBirthDateYYYY(), PersonDto.BIRTH_DATE_YYYY),
				Mapping.of(person::setSex, person.getSex(), externalMessage.getPersonSex(), PersonDto.SEX),
				Mapping.of(
					person::setPresentCondition,
					person.getPresentCondition(),
					externalMessage.getPersonPresentCondition(),
					PersonDto.PRESENT_CONDITION),
				Mapping.of(person::setDeathDate, person.getDeathDate(), externalMessage.getDeceasedDate(), PersonDto.DEATH_DATE),
				Mapping.of(person::setPhone, person.getPhone(), externalMessage.getPersonPhone(), PersonDto.PERSON_CONTACT_DETAILS),
				Mapping.of(
					person::setPhoneNumberType,
					person.getPhoneNumberType(),
					externalMessage.getPersonPhoneNumberType(),
					PersonDto.PERSON_CONTACT_DETAILS),
				Mapping.of(person::setEmailAddress, person.getEmailAddress(), externalMessage.getPersonEmail(), PersonDto.PERSON_CONTACT_DETAILS),
				Mapping.of(person::setExternalId, person.getExternalId(), externalMessage.getPersonExternalId(), PersonDto.EXTERNAL_ID),
				Mapping.of(
					person::setNationalHealthId,
					person.getNationalHealthId(),
					externalMessage.getPersonNationalHealthId(),
					PersonDto.NATIONAL_HEALTH_ID),
				Mapping.of(
					person::setAdditionalDetails,
					person.getAdditionalDetails(),
					externalMessage.getPersonAdditionalDetails(),
					PersonDto.ADDITIONAL_DETAILS)));

		if (person.getBirthdateYYYY() != null) {
			DataHelper.Pair<Integer, ApproximateAgeType> ageAndAgeType = ApproximateAgeType.ApproximateAgeHelper
				.getApproximateAge(person.getBirthdateYYYY(), person.getBirthdateMM(), person.getBirthdateDD(), person.getDeathDate());

			changedFields.addAll(
				map(
					Stream.of(
						Mapping.of(person::setApproximateAge, person.getApproximateAge(), ageAndAgeType.getElement0(), PersonDto.APPROXIMATE_AGE),
						Mapping.of(
							person::setApproximateAgeType,
							person.getApproximateAgeType(),
							ageAndAgeType.getElement1(),
							PersonDto.APPROXIMATE_AGE_TYPE))));
		}

		return changedFields;
	}

	public List<String[]> mapFirstSampleReportToSample(SampleDto sample) {
		return mapToSample(sample, externalMessage.getSampleReportsNullSafe().get(0));
	}

	public List<String[]> mapToLocation(LocationDto location) {

		RegionReferenceDto region = null;
		DistrictReferenceDto district = null;
		FacilityType facilityType = null;
		if (externalMessage.getPersonFacility() != null) {
			FacilityDto facility = processingFacade.getFacilityByUuid(externalMessage.getPersonFacility().getUuid());
			region = facility.getRegion();
			district = facility.getDistrict();
			facilityType = facility.getType();
		}

		return map(
			Stream.of(
				Mapping.of(location::setStreet, location.getStreet(), externalMessage.getPersonStreet(), PersonDto.ADDRESS, LocationDto.STREET),
				Mapping.of(
					location::setHouseNumber,
					location.getHouseNumber(),
					externalMessage.getPersonHouseNumber(),
					PersonDto.ADDRESS,
					LocationDto.HOUSE_NUMBER),
				Mapping.of(
					location::setPostalCode,
					location.getPostalCode(),
					externalMessage.getPersonPostalCode(),
					PersonDto.ADDRESS,
					LocationDto.POSTAL_CODE),
				Mapping.of(location::setCity, location.getCity(), externalMessage.getPersonCity(), PersonDto.ADDRESS, LocationDto.CITY),
				Mapping.of(location::setCountry, location.getCountry(), externalMessage.getPersonCountry(), PersonDto.ADDRESS, LocationDto.COUNTRY),
				Mapping.of(location::setRegion, location.getRegion(), region, PersonDto.ADDRESS, LocationDto.REGION),
				Mapping.of(location::setDistrict, location.getDistrict(), district, PersonDto.ADDRESS, LocationDto.DISTRICT),
				Mapping.of(location::setFacilityType, location.getFacilityType(), facilityType, PersonDto.ADDRESS, LocationDto.FACILITY_TYPE),
				Mapping.of(
					location::setFacility,
					location.getFacility(),
					externalMessage.getPersonFacility(),
					PersonDto.ADDRESS,
					LocationDto.FACILITY)));
	}

	/**
	 * Deserializes {@link ExternalMessageDto#getAdditionalPersonContactDetails()} from JSON and merges
	 * the entries into the person. Entries already present by type + contactInformation are skipped.
	 */
	public List<String[]> mapAdditionalPersonContactDetails(PersonDto person) {
		if (externalMessage.getAdditionalPersonContactDetails() == null || externalMessage.getAdditionalPersonContactDetails().isEmpty()) {
			return Collections.emptyList();
		}
		try {
			List<PersonContactDetailDto> additionalDetails =
				new ObjectMapper().readValue(externalMessage.getAdditionalPersonContactDetails(), new TypeReference<List<PersonContactDetailDto>>() {
				});
			return mapAdditionalPersonContactDetails(person, additionalDetails);
		} catch (Exception e) {
			logger.error("[MAPPER] Error while deserializing additional person contact details", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Deserializes {@link ExternalMessageDto#getAdditionalPersonAddresses()} from JSON and appends
	 * the entries into the person's address list. No deduplication is performed.
	 */
	public List<String[]> mapAdditionalPersonAddresses(PersonDto person) {
		if (externalMessage.getAdditionalPersonAddresses() == null || externalMessage.getAdditionalPersonAddresses().isEmpty()) {
			return Collections.emptyList();
		}
		try {
			List<LocationDto> additionalAddresses =
				new ObjectMapper().readValue(externalMessage.getAdditionalPersonAddresses(), new TypeReference<List<LocationDto>>() {
				});
			return mapAdditionalPersonAddresses(person, additionalAddresses);
		} catch (Exception e) {
			logger.error("[MAPPER] Error while deserializing additional person addresses", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Applies guardian name, incapacitated/emancipated flags, and guardian contact details (email, phone)
	 * from the external message onto the given person.
	 * These fields are not covered by the regular person-creation form and must be persisted in a separate step.
	 *
	 * @param person
	 *            The person to update.
	 * @return A list of changed UI field paths; empty if nothing was changed.
	 */
	public List<String[]> mapGuardianData(PersonDto person) {
		List<String[]> changedFields = new ArrayList<>();

		final String nameOfGuardian =
			String
				.format(
					"%s %s",
					externalMessage.getPersonGuardianFirstName() != null ? externalMessage.getPersonGuardianFirstName() : "",
					externalMessage.getPersonGuardianLastName() != null ? externalMessage.getPersonGuardianLastName() : "")
				.trim();

		if (!nameOfGuardian.isBlank()) {
			person.setNamesOfGuardians(nameOfGuardian);
			// Both incapacitated and emancipated must be set together, otherwise the person is not shown correctly in the UI
			person.setIncapacitated(true);
			person.setEmancipated(false);
			changedFields.add(
				new String[] {
					PersonDto.NAMES_OF_GUARDIANS });
		}

		if (externalMessage.getPersonGuardianEmail() != null && !externalMessage.getPersonGuardianEmail().isBlank()) {
			List<PersonContactDetailDto> contactDetails = person.getPersonContactDetails();
			if (contactDetails.stream().noneMatch(pc -> externalMessage.getPersonGuardianEmail().equals(pc.getContactInformation()))) {
				final PersonContactDetailDto pcd = new PersonContactDetailDto();
				pcd.setUuid(DataHelper.createUuid());
				pcd.setPerson(person.toReference());
				pcd.setPrimaryContact(false);
				pcd.setPersonContactDetailType(PersonContactDetailType.EMAIL);
				pcd.setContactInformation(externalMessage.getPersonGuardianEmail());
				pcd.setThirdParty(true);
				pcd.setThirdPartyRole(externalMessage.getPersonGuardianRelationship());
				pcd.setThirdPartyName(nameOfGuardian);
				contactDetails.add(pcd);
				changedFields.add(
					new String[] {
						PersonDto.PERSON_CONTACT_DETAILS });
			}
		}

		if (externalMessage.getPersonGuardianPhone() != null && !externalMessage.getPersonGuardianPhone().isBlank()) {
			List<PersonContactDetailDto> contactDetails = person.getPersonContactDetails();
			if (contactDetails.stream().noneMatch(pc -> externalMessage.getPersonGuardianPhone().equals(pc.getContactInformation()))) {
				final PersonContactDetailDto pcd = new PersonContactDetailDto();
				pcd.setUuid(DataHelper.createUuid());
				pcd.setPerson(person.toReference());
				pcd.setPrimaryContact(false);
				pcd.setPersonContactDetailType(PersonContactDetailType.PHONE);
				pcd.setContactInformation(externalMessage.getPersonGuardianPhone());
				pcd.setThirdParty(true);
				pcd.setThirdPartyRole(externalMessage.getPersonGuardianRelationship());
				pcd.setThirdPartyName(nameOfGuardian);
				contactDetails.add(pcd);
				changedFields.add(
					new String[] {
						PersonDto.PERSON_CONTACT_DETAILS });
			}
		}

		return changedFields;
	}

	/**
	 * Applies occupation type and details from the external message onto the given person.
	 * The occupation type is resolved to the customizable enum value for "OTHER".
	 * If the enum value cannot be found, no changes are applied.
	 *
	 * @param person
	 *            The person to update.
	 * @return A list of changed UI field paths; empty if nothing was changed.
	 */
	public List<String[]> mapOccupationData(PersonDto person) {
		if (externalMessage.getPersonOccupation() == null || externalMessage.getPersonOccupation().isBlank()) {
			return Collections.emptyList();
		}

		try {
			final OccupationType occupationTypeOther = processingFacade.getOccupationTypeOther();
			person.setOccupationType(occupationTypeOther);
			person.setOccupationDetails(externalMessage.getPersonOccupation());
			return Collections.singletonList(
				new String[] {
					PersonDto.OCCUPATION_TYPE });
		} catch (CustomEnumNotFoundException e) {
			// do nothing if OccupationType OTHER custom enum is not found
			return Collections.emptyList();
		}
	}

	/**
	 * Merges address fields from the external message onto the given person's primary address.
	 * Only non-null values from the external message overwrite the existing address fields.
	 *
	 * @param person
	 *            The existing person to update.
	 * @return A list of changed UI field paths; empty if the person has no address.
	 */
	public List<String[]> mergePersonAddress(PersonDto person) {

		if (person == null) {
			return Collections.emptyList();
		}

		final LocationDto personAddress = person.getAddress();
		if (personAddress == null) {
			// just to be safe for whatever reason if address is null, create a new one
			final LocationDto location = LocationDto.build();
			// in this case we no longer need to merge the address, so we can just return the new location
			person.setAddress(location);
			return mapToLocation(location);
		}

		final String houseNumber = externalMessage.getPersonHouseNumber();
		if (houseNumber != null) {
			personAddress.setHouseNumber(houseNumber);
		}
		final String street = externalMessage.getPersonStreet();
		if (street != null) {
			personAddress.setStreet(street);
		}
		final String city = externalMessage.getPersonCity();
		if (city != null) {
			personAddress.setCity(city);
		}
		final String postalCode = externalMessage.getPersonPostalCode();
		if (postalCode != null) {
			personAddress.setPostalCode(postalCode);
		}
		final CountryReferenceDto country = externalMessage.getPersonCountry();
		if (country != null) {
			personAddress.setCountry(country);
		}

		return Collections.singletonList(
			new String[] {
				PersonDto.ADDRESS });
	}

	/**
	 * Merges primary phone and email contact details from the external message onto the given person.
	 * If the incoming value already exists in the list it is promoted to primary and the old primary is demoted;
	 * otherwise a new primary entry is created and the old primary is demoted.
	 *
	 * @param person
	 *            The existing person to update.
	 * @return A list of changed UI field paths; empty if nothing was changed.
	 */
	public List<String[]> mergePersonContactDetails(PersonDto person) {

		if (person == null) {
			return Collections.emptyList();
		}

		List<String[]> changedFields = new ArrayList<>();

		final List<PersonContactDetailDto> personContactDetails = person.getPersonContactDetails();

		final String phoneNumber = externalMessage.getPersonPhone();
		final PhoneNumberType phoneNumberType = externalMessage.getPersonPhoneNumberType();

		if (phoneNumber != null && !phoneNumber.isBlank()) {
			final PersonContactDetailDto primaryPhone = personContactDetails.stream()
				.filter(pdc -> pdc.getPersonContactDetailType() == PersonContactDetailType.PHONE && !pdc.isThirdParty() && pdc.isPrimaryContact())
				.findFirst()
				.orElse(null);
			final PersonContactDetailDto existingPhone = personContactDetails.stream()
				.filter(
					pdc -> pdc.getPersonContactDetailType() == PersonContactDetailType.PHONE
						&& !pdc.isThirdParty()
						&& phoneNumber.equals(pdc.getContactInformation()))
				.findFirst()
				.orElse(null);

			if (existingPhone != null) {
				// Promote the existing entry to primary, demote the old primary
				if (primaryPhone != null) {
					primaryPhone.setPrimaryContact(false);
				}
				existingPhone.setPrimaryContact(true);
			} else {
				// Create a new primary entry and demote the old primary
				final PersonContactDetailDto personContactDetail = new PersonContactDetailDto();
				personContactDetail.setUuid(DataHelper.createUuid());
				personContactDetail.setPerson(person.toReference());
				personContactDetail.setPrimaryContact(true);
				personContactDetail.setPersonContactDetailType(PersonContactDetailType.PHONE);
				personContactDetail.setPhoneNumberType(phoneNumberType);
				personContactDetail.setContactInformation(phoneNumber);
				personContactDetail.setThirdParty(false);
				personContactDetails.add(personContactDetail);
				if (primaryPhone != null) {
					primaryPhone.setPrimaryContact(false);
				}
			}
			changedFields.add(
				new String[] {
					PersonDto.PERSON_CONTACT_DETAILS });
		}

		final String emailAddress = externalMessage.getPersonEmail();

		if (emailAddress != null && !emailAddress.isBlank()) {
			final PersonContactDetailDto primaryEmail = personContactDetails.stream()
				.filter(pdc -> pdc.getPersonContactDetailType() == PersonContactDetailType.EMAIL && !pdc.isThirdParty() && pdc.isPrimaryContact())
				.findFirst()
				.orElse(null);
			final PersonContactDetailDto existingEmail = personContactDetails.stream()
				.filter(
					pdc -> pdc.getPersonContactDetailType() == PersonContactDetailType.EMAIL
						&& !pdc.isThirdParty()
						&& emailAddress.equals(pdc.getContactInformation()))
				.findFirst()
				.orElse(null);

			if (existingEmail != null) {
				// Promote the existing entry to primary, demote the old primary
				if (primaryEmail != null) {
					primaryEmail.setPrimaryContact(false);
				}
				existingEmail.setPrimaryContact(true);
			} else {
				// Create a new primary entry and demote the old primary
				final PersonContactDetailDto personContactDetail = new PersonContactDetailDto();
				personContactDetail.setUuid(DataHelper.createUuid());
				personContactDetail.setPerson(person.toReference());
				personContactDetail.setPrimaryContact(true);
				personContactDetail.setPersonContactDetailType(PersonContactDetailType.EMAIL);
				personContactDetail.setContactInformation(emailAddress);
				personContactDetail.setThirdParty(false);
				personContactDetails.add(personContactDetail);
				if (primaryEmail != null) {
					primaryEmail.setPrimaryContact(false);
				}
			}
			changedFields.add(
				new String[] {
					PersonDto.PERSON_CONTACT_DETAILS });
		}

		changedFields.addAll(mapAdditionalPersonContactDetails(person));

		return changedFields;
	}

	private List<String[]> mapAdditionalPersonContactDetails(PersonDto person, List<PersonContactDetailDto> additionalDetails) {
		if (person == null) {
			return Collections.emptyList();
		}

		if (additionalDetails == null || additionalDetails.isEmpty()) {
			return Collections.emptyList();
		}

		List<PersonContactDetailDto> existing = person.getPersonContactDetails();
		boolean changed = false;

		for (PersonContactDetailDto incoming : additionalDetails) {
			final boolean alreadyPresent = existing.stream()
				.anyMatch(
					e -> e.getPersonContactDetailType() == incoming.getPersonContactDetailType()
						&& Objects.equals(e.getContactInformation(), incoming.getContactInformation()));

			if (alreadyPresent) {
				continue;
			}

			if (incoming.getUuid() == null) {
				incoming.setUuid(DataHelper.createUuid());
			}
			incoming.setPerson(person.toReference());
			existing.add(incoming);
			changed = true;
		}

		return changed
			? Collections.singletonList(
				new String[] {
					PersonDto.PERSON_CONTACT_DETAILS })
			: Collections.emptyList();
	}

	private List<String[]> mapAdditionalPersonAddresses(PersonDto person, List<LocationDto> additionalAddresses) {

		if (person == null) {
			return Collections.emptyList();
		}

		if (additionalAddresses == null || additionalAddresses.isEmpty()) {
			return Collections.emptyList();
		}

		for (LocationDto incoming : additionalAddresses) {
			if (incoming.getUuid() == null) {
				incoming.setUuid(DataHelper.createUuid());
			}
			person.getAddresses().add(incoming);
		}

		return Collections.singletonList(
			new String[] {
				PersonDto.ADDRESSES });
	}

	public List<String[]> mapToSample(SampleDto sample, SampleReportDto sampleReport) {
		List<String[]> changedFields = map(
			Stream.of(
				Mapping.of(sample::setSampleDateTime, sample.getSampleDateTime(), sampleReport.getSampleDateTime(), SampleDto.SAMPLE_DATE_TIME),
				Mapping.of(sample::setSampleMaterial, sample.getSampleMaterial(), sampleReport.getSampleMaterial(), SampleDto.SAMPLE_MATERIAL),
				Mapping.of(
					sample::setSampleMaterialText,
					sample.getSampleMaterialText(),
					sampleReport.getSampleMaterialText(),
					SampleDto.SAMPLE_MATERIAL_TEXT),
				Mapping.of(
					sample::setSpecimenCondition,
					sample.getSpecimenCondition(),
					sampleReport.getSpecimenCondition(),
					SampleDto.SPECIMEN_CONDITION),
				Mapping.of(
					sample::setLab,
					sample.getLab(),
					processingFacade.getFacilityReference(externalMessage.getReporterExternalIds()),
					SampleDto.LAB),
				Mapping.of(sample::setLabDetails, sample.getLabDetails(), externalMessage.getReporterName(), SampleDto.LAB_DETAILS)));

		if (sampleReport.getSampleReceivedDate() != null) {
			changedFields.addAll(
				map(
					Stream.of(
						Mapping.of(sample::setReceived, sample.isReceived(), true, SampleDto.RECEIVED),
						Mapping.of(sample::setReceivedDate, sample.getReceivedDate(), sampleReport.getSampleReceivedDate(), SampleDto.RECEIVED_DATE),
						Mapping.of(sample::setLabSampleID, sample.getLabSampleID(), sampleReport.getLabSampleId(), SampleDto.LAB_SAMPLE_ID))));
		}

		PathogenTestResultType pathogenTestResult = null;
		if (sampleReport.getSampleOverallTestResult() != null) {
			pathogenTestResult = sampleReport.getSampleOverallTestResult();
		} else if (homogenousTestResultTypesIn(sampleReport)) {
			pathogenTestResult = sampleReport.getTestReports().get(0).getTestResult();
		}

		changedFields.addAll(
			map(
				Stream.of(
					Mapping.of(sample::setPathogenTestResult, sample.getPathogenTestResult(), pathogenTestResult, SampleDto.PATHOGEN_TEST_RESULT))));

		return changedFields;
	}

	public List<String[]> mapToPathogenTest(TestReportDto sourceTestReport, PathogenTestDto pathogenTest) {
		List<String[]> changedFields = new ArrayList<>();

		if (sourceTestReport != null) {
			// <testResultText, diseaseVariant, diseaseVariantDetails>
			ImmutableTriple<String, DiseaseVariant, String> migratedDiseaseVariant = migrateDiseaseVariant(sourceTestReport);

			String testResultText = StringUtils.isEmpty(migratedDiseaseVariant.getLeft())
				? sourceTestReport.getTestResultText()
				: migratedDiseaseVariant.getLeft() + sourceTestReport.getTestResultText();

			changedFields.addAll(
				map(
					Stream.of(
						Mapping
							.of(pathogenTest::setTestResultText, pathogenTest.getTestResultText(), testResultText, PathogenTestDto.TEST_RESULT_TEXT),
						Mapping.of(
							pathogenTest::setTestedDiseaseVariant,
							pathogenTest.getTestedDiseaseVariant(),
							migratedDiseaseVariant.getMiddle(),
							PathogenTestDto.TESTED_DISEASE_VARIANT),
						Mapping.of(
							pathogenTest::setTestedDiseaseVariantDetails,
							pathogenTest.getTestedDiseaseVariantDetails(),
							migratedDiseaseVariant.getRight(),
							PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS),
						Mapping.of(
							pathogenTest::setTestResult,
							pathogenTest.getTestResult(),
							sourceTestReport.getTestResult(),
							PathogenTestDto.TEST_RESULT),
						Mapping.of(
							pathogenTest::setTestDateTime,
							pathogenTest.getTestDateTime(),
							sourceTestReport.getDateOfResult(),
							PathogenTestDto.TEST_DATE_TIME),
						Mapping.of(pathogenTest::setTestType, pathogenTest.getTestType(), sourceTestReport.getTestType(), PathogenTestDto.TEST_TYPE),
						Mapping.of(
							pathogenTest::setTestResultVerified,
							pathogenTest.getTestResultVerified(),
							sourceTestReport.isTestResultVerified(),
							PathogenTestDto.TEST_RESULT_VERIFIED),
						Mapping.of(
							pathogenTest::setTestDateTime,
							pathogenTest.getTestDateTime(),
							sourceTestReport.getTestDateTime(),
							PathogenTestDto.TEST_DATE_TIME),
						Mapping.of(pathogenTest::setTypingId, pathogenTest.getTypingId(), sourceTestReport.getTypingId(), PathogenTestDto.TYPING_ID),
						Mapping.of(
							pathogenTest::setExternalId,
							pathogenTest.getExternalId(),
							sourceTestReport.getExternalId(),
							PathogenTestDto.EXTERNAL_ID),
						Mapping.of(
							pathogenTest::setExternalOrderId,
							pathogenTest.getExternalOrderId(),
							sourceTestReport.getExternalOrderId(),
							PathogenTestDto.EXTERNAL_ORDER_ID),
						Mapping.of(
							pathogenTest::setLab,
							pathogenTest.getLab(),
							processingFacade.getFacilityReference(sourceTestReport.getTestLabExternalIds()),
							PathogenTestDto.LAB),
						Mapping.of(
							pathogenTest::setLabDetails,
							pathogenTest.getLabDetails(),
							sourceTestReport.getTestLabName(),
							PathogenTestDto.LAB_DETAILS),
						Mapping.of(
							pathogenTest::setPreliminary,
							pathogenTest.getPreliminary(),
							sourceTestReport.getPreliminary(),
							PathogenTestDto.PRELIMINARY),
						Mapping.of(
							pathogenTest::setPcrTestSpecification,
							pathogenTest.getPcrTestSpecification(),
							sourceTestReport.getTestPcrTestSpecification(),
							PathogenTestDto.PCR_TEST_SPECIFICATION),
						Mapping.of(pathogenTest::setCqValue, pathogenTest.getCqValue(), sourceTestReport.getCqValue(), PathogenTestDto.CQ_VALUE),
						Mapping.of(pathogenTest::setCtValueE, pathogenTest.getCtValueE(), sourceTestReport.getCtValueE(), PathogenTestDto.CT_VALUE_E),
						Mapping.of(pathogenTest::setCtValueN, pathogenTest.getCtValueN(), sourceTestReport.getCtValueN(), PathogenTestDto.CT_VALUE_N),
						Mapping.of(
							pathogenTest::setCtValueRdrp,
							pathogenTest.getCtValueRdrp(),
							sourceTestReport.getCtValueRdrp(),
							PathogenTestDto.CT_VALUE_RDRP),
						Mapping.of(pathogenTest::setCtValueS, pathogenTest.getCtValueS(), sourceTestReport.getCtValueS(), PathogenTestDto.CT_VALUE_S),
						Mapping.of(
							pathogenTest::setCtValueOrf1,
							pathogenTest.getCtValueOrf1(),
							sourceTestReport.getCtValueOrf1(),
							PathogenTestDto.CT_VALUE_ORF_1),
						Mapping.of(
							pathogenTest::setCtValueRdrpS,
							pathogenTest.getCtValueRdrpS(),
							sourceTestReport.getCtValueRdrpS(),
							PathogenTestDto.CT_VALUE_RDRP_S),
						Mapping.of(
							pathogenTest::setPrescriberPhysicianCode,
							pathogenTest.getPrescriberPhysicianCode(),
							sourceTestReport.getPrescriberPhysicianCode(),
							PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE),
						Mapping.of(
							pathogenTest::setPrescriberFirstName,
							pathogenTest.getPrescriberFirstName(),
							sourceTestReport.getPrescriberFirstName(),
							PathogenTestDto.PRESCRIBER_FIRST_NAME),
						Mapping.of(
							pathogenTest::setPrescriberLastName,
							pathogenTest.getPrescriberLastName(),
							sourceTestReport.getPrescriberLastName(),
							PathogenTestDto.PRESCRIBER_LAST_NAME),
						Mapping.of(
							pathogenTest::setPrescriberPhoneNumber,
							pathogenTest.getPrescriberPhoneNumber(),
							sourceTestReport.getPrescriberPhoneNumber(),
							PathogenTestDto.PRESCRIBER_PHONE_NUMBER),
						Mapping.of(
							pathogenTest::setPrescriberAddress,
							pathogenTest.getPrescriberAddress(),
							sourceTestReport.getPrescriberAddress(),
							PathogenTestDto.PRESCRIBER_ADDRESS),
						Mapping.of(
							pathogenTest::setPrescriberPostalCode,
							pathogenTest.getPrescriberPostalCode(),
							sourceTestReport.getPrescriberPostalCode(),
							PathogenTestDto.PRESCRIBER_POSTAL_CODE),
						Mapping.of(
							pathogenTest::setPrescriberCity,
							pathogenTest.getPrescriberCity(),
							sourceTestReport.getPrescriberCity(),
							PathogenTestDto.PRESCRIBER_CITY),
						Mapping.of(
							pathogenTest::setPrescriberCountry,
							pathogenTest.getPrescriberCountry(),
							sourceTestReport.getPrescriberCountry(),
							PathogenTestDto.PRESCRIBER_COUNTRY),
						Mapping.of(pathogenTest::setGenoType, pathogenTest.getGenoType(), sourceTestReport.getGenoType(), PathogenTestDto.GENOTYPE),
						Mapping.of(
							pathogenTest::setSeroGroupSpecification,
							pathogenTest.getSeroGroupSpecification(),
							sourceTestReport.getSeroGroupSpecification(),
							PathogenTestDto.SERO_GROUP_SPECIFICATION),
						Mapping.of(
							pathogenTest::setSeroGroupSpecificationText,
							pathogenTest.getSeroGroupSpecificationText(),
							sourceTestReport.getSeroGroupSpecificationText(),
							PathogenTestDto.SERO_GROUP_SPECIFICATION),
						Mapping.of(pathogenTest::setSerotype, pathogenTest.getSerotype(), sourceTestReport.getSerotype(), PathogenTestDto.SEROTYPE),
						Mapping.of(
							pathogenTest::setSerotypeText,
							pathogenTest.getSerotypeText(),
							sourceTestReport.getSerotypeText(),
							PathogenTestDto.SEROTYPE_TEXT),
						Mapping.of(
							pathogenTest::setSeroTypingMethod,
							pathogenTest.getSeroTypingMethod(),
							sourceTestReport.getSeroTypingMethod(),
							PathogenTestDto.SEROTYPING_METHOD),
						Mapping.of(
							pathogenTest::setSeroTypingMethodText,
							pathogenTest.getSeroTypingMethodText(),
							sourceTestReport.getSeroTypingMethodText(),
							PathogenTestDto.SERO_TYPING_METHOD_TEXT),
						Mapping.of(
							pathogenTest::setRsvSubtype,
							pathogenTest.getRsvSubtype(),
							sourceTestReport.getRsvSubtype(),
							PathogenTestDto.RSV_SUBTYPE),
						Mapping.of(
							pathogenTest::setTubeNil, // Tube nil flag
							pathogenTest.getTubeNil(),
							sourceTestReport.getTubeNil(),
							PathogenTestDto.TUBE_NIL),
						Mapping.of(
							pathogenTest::setTubeNilGT10, // Nil >10 flag
							pathogenTest.getTubeNilGT10(),
							sourceTestReport.getTubeNilGT10(),
							PathogenTestDto.TUBE_NIL_GT10),
						Mapping.of(
							pathogenTest::setTubeAgTb1,
							pathogenTest.getTubeAgTb1(),
							sourceTestReport.getTubeAgTb1(),
							PathogenTestDto.TUBE_AG_TB1),
						Mapping.of(
							pathogenTest::setTubeAgTb1GT10,
							pathogenTest.getTubeAgTb1GT10(),
							sourceTestReport.getTubeAgTb1GT10(),
							PathogenTestDto.TUBE_AG_TB1_GT10),
						Mapping.of(
							pathogenTest::setTubeAgTb2,
							pathogenTest.getTubeAgTb2(),
							sourceTestReport.getTubeAgTb2(),
							PathogenTestDto.TUBE_AG_TB2),
						Mapping.of(
							pathogenTest::setTubeAgTb2GT10,
							pathogenTest.getTubeAgTb2GT10(),
							sourceTestReport.getTubeAgTb2GT10(),
							PathogenTestDto.TUBE_AG_TB2_GT10),
						Mapping.of(
							pathogenTest::setTubeMitogene,
							pathogenTest.getTubeMitogene(),
							sourceTestReport.getTubeMitogene(),
							PathogenTestDto.TUBE_MITOGENE),
						Mapping.of(
							pathogenTest::setTubeMitogeneGT10,
							pathogenTest.getTubeMitogeneGT10(),
							sourceTestReport.getTubeMitogeneGT10(),
							PathogenTestDto.TUBE_MITOGENE_GT10),
						Mapping.of(
							pathogenTest::setStrainCallStatus,
							pathogenTest.getStrainCallStatus(),
							sourceTestReport.getStrainCallStatus(),
							PathogenTestDto.STRAIN_CALL_STATUS),
						Mapping.of(pathogenTest::setSpecie, pathogenTest.getSpecie(), sourceTestReport.getSpecie(), PathogenTestDto.SPECIE),
						// Drug susceptibility mappings
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setAmikacinMic,
							pathogenTest.getDrugSusceptibility().getAmikacinMic(),
							sourceTestReport.getAmikacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.AMIKACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setAmikacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getAmikacinSusceptibility(),
							sourceTestReport.getAmikacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.AMIKACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setBedaquilineMic,
							pathogenTest.getDrugSusceptibility().getBedaquilineMic(),
							sourceTestReport.getBedaquilineMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.BEDAQUILINE_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setBedaquilineSusceptibility,
							pathogenTest.getDrugSusceptibility().getBedaquilineSusceptibility(),
							sourceTestReport.getBedaquilineSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.BEDAQUILINE_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCapreomycinMic,
							pathogenTest.getDrugSusceptibility().getCapreomycinMic(),
							sourceTestReport.getCapreomycinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CAPREOMYCIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCapreomycinSusceptibility,
							pathogenTest.getDrugSusceptibility().getCapreomycinSusceptibility(),
							sourceTestReport.getCapreomycinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CAPREOMYCIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCiprofloxacinMic,
							pathogenTest.getDrugSusceptibility().getCiprofloxacinMic(),
							sourceTestReport.getCiprofloxacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CIPROFLOXACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCiprofloxacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getCiprofloxacinSusceptibility(),
							sourceTestReport.getCiprofloxacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CIPROFLOXACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setDelamanidMic,
							pathogenTest.getDrugSusceptibility().getDelamanidMic(),
							sourceTestReport.getDelamanidMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.DELAMANID_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setDelamanidSusceptibility,
							pathogenTest.getDrugSusceptibility().getDelamanidSusceptibility(),
							sourceTestReport.getDelamanidSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.DELAMANID_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setEthambutolMic,
							pathogenTest.getDrugSusceptibility().getEthambutolMic(),
							sourceTestReport.getEthambutolMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ETHAMBUTOL_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setEthambutolSusceptibility,
							pathogenTest.getDrugSusceptibility().getEthambutolSusceptibility(),
							sourceTestReport.getEthambutolSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ETHAMBUTOL_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setGatifloxacinMic,
							pathogenTest.getDrugSusceptibility().getGatifloxacinMic(),
							sourceTestReport.getGatifloxacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.GATIFLOXACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setGatifloxacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getGatifloxacinSusceptibility(),
							sourceTestReport.getGatifloxacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.GATIFLOXACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setIsoniazidMic,
							pathogenTest.getDrugSusceptibility().getIsoniazidMic(),
							sourceTestReport.getIsoniazidMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ISONIAZID_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setIsoniazidSusceptibility,
							pathogenTest.getDrugSusceptibility().getIsoniazidSusceptibility(),
							sourceTestReport.getIsoniazidSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ISONIAZID_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setKanamycinMic,
							pathogenTest.getDrugSusceptibility().getKanamycinMic(),
							sourceTestReport.getKanamycinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.KANAMYCIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setKanamycinSusceptibility,
							pathogenTest.getDrugSusceptibility().getKanamycinSusceptibility(),
							sourceTestReport.getKanamycinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.KANAMYCIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setLevofloxacinMic,
							pathogenTest.getDrugSusceptibility().getLevofloxacinMic(),
							sourceTestReport.getLevofloxacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.LEVOFLOXACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setLevofloxacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getLevofloxacinSusceptibility(),
							sourceTestReport.getLevofloxacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.LEVOFLOXACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setMoxifloxacinMic,
							pathogenTest.getDrugSusceptibility().getMoxifloxacinMic(),
							sourceTestReport.getMoxifloxacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.MOXIFLOXACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setMoxifloxacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getMoxifloxacinSusceptibility(),
							sourceTestReport.getMoxifloxacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.MOXIFLOXACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setOfloxacinMic,
							pathogenTest.getDrugSusceptibility().getOfloxacinMic(),
							sourceTestReport.getOfloxacinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.OFLOXACIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setOfloxacinSusceptibility,
							pathogenTest.getDrugSusceptibility().getOfloxacinSusceptibility(),
							sourceTestReport.getOfloxacinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.OFLOXACIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setRifampicinMic,
							pathogenTest.getDrugSusceptibility().getRifampicinMic(),
							sourceTestReport.getRifampicinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.RIFAMPICIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setRifampicinSusceptibility,
							pathogenTest.getDrugSusceptibility().getRifampicinSusceptibility(),
							sourceTestReport.getRifampicinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.RIFAMPICIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setStreptomycinMic,
							pathogenTest.getDrugSusceptibility().getStreptomycinMic(),
							sourceTestReport.getStreptomycinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.STREPTOMYCIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setStreptomycinSusceptibility,
							pathogenTest.getDrugSusceptibility().getStreptomycinSusceptibility(),
							sourceTestReport.getStreptomycinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.STREPTOMYCIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCeftriaxoneMic,
							pathogenTest.getDrugSusceptibility().getCeftriaxoneMic(),
							sourceTestReport.getCeftriaxoneMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CEFTRIAXONE_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setCeftriaxoneSusceptibility,
							pathogenTest.getDrugSusceptibility().getCeftriaxoneSusceptibility(),
							sourceTestReport.getCeftriaxoneSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.CEFTRIAXONE_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setPenicillinMic,
							pathogenTest.getDrugSusceptibility().getPenicillinMic(),
							sourceTestReport.getPenicillinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.PENICILLIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setPenicillinSusceptibility,
							pathogenTest.getDrugSusceptibility().getPenicillinSusceptibility(),
							sourceTestReport.getPenicillinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.PENICILLIN_SUSCEPTIBILITY),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setErythromycinMic,
							pathogenTest.getDrugSusceptibility().getErythromycinMic(),
							sourceTestReport.getErythromycinMic(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ERYTHROMYCIN_MIC),
						Mapping.of(
							pathogenTest.getDrugSusceptibility()::setErythromycinSusceptibility,
							pathogenTest.getDrugSusceptibility().getErythromycinSusceptibility(),
							sourceTestReport.getErythromycinSusceptibility(),
							PathogenTestDto.DRUG_SUSCEPTIBILITY,
							DrugSusceptibilityDto.ERYTHROMYCIN_SUSCEPTIBILITY),

						Mapping.of(
							pathogenTest::setFourFoldIncreaseAntibodyTiter,
							pathogenTest.isFourFoldIncreaseAntibodyTiter(),
							sourceTestReport.getFourFoldIncreaseAntibodyTiter(),
							PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER),
						Mapping.of(
							pathogenTest::setPerformedByReferenceLaboratory,
							pathogenTest.getPerformedByReferenceLaboratory(),
							sourceTestReport.getPerformedByReferenceLaboratory(),
							PathogenTestDto.PERFORMED_BY_REFERENCE_LABORATORY))

				));
		}

		changedFields.addAll(
			map(
				Stream.of(
					Mapping.of(
						pathogenTest::setTestedDisease,
						pathogenTest.getTestedDisease(),
						externalMessage.getDisease(),
						PathogenTestDto.TESTED_DISEASE),
					Mapping.of(
						pathogenTest::setReportDate,
						pathogenTest.getReportDate(),
						getPathogenTestReportDate(),
						DateHelper::getStartOfDay,
						PathogenTestDto.REPORT_DATE))));

		return changedFields;
	}

	private List<String[]> map(Stream<Mapping<?>> mappings) {
		List<String[]> changedFields = new ArrayList<>();

		mappings.forEach((m) -> {
			if (mapField(m)) {
				changedFields.add(m.uiFieldPath);
			}
		});

		return changedFields;
	}

	@SuppressWarnings("rawtypes")
	private boolean mapField(Mapping m) {
		if (m.newValue != null && !DataHelper.equal(m.newValue, m.originalValue)) {
			m.mapper.accept(m.newValue);
			return true;
		}

		return false;
	}

	private static class Mapping<T> {

		private String[] uiFieldPath;
		private Consumer<T> mapper;
		private T originalValue;
		private T newValue;

		static <T> Mapping<T> of(Consumer<T> mapper, T originalValue, T newValue, String... fieldPath) {
			if (fieldPath.length == 0) {
				throw new IllegalArgumentException("fieldPath should not be empty");
			}

			Mapping<T> m = new Mapping<>();

			m.uiFieldPath = fieldPath;
			m.mapper = mapper;
			m.originalValue = originalValue;
			m.newValue = newValue;

			return m;
		}

		static <T, X> Mapping<T> of(Consumer<T> mapper, X originalValue, X newValue, Function<X, T> valueConvert, String... fieldPath) {
			return of(
				mapper,
				originalValue != null ? valueConvert.apply(originalValue) : null,
				newValue != null ? valueConvert.apply(newValue) : null,
				fieldPath);
		}
	}

	private Date getPathogenTestReportDate() {
		Date reportDate = null;
		if (processingFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			reportDate = externalMessage.getMessageDateTime();
		}
		return reportDate;
	}

	/**
	 * The migration depends on whether the disease variant can be found as a customizable enum value or not.
	 * If yes, the enum is set as disease variant. If not, the disease variant is added to the test result text,
	 * along with the disease variant details.
	 */
	public ImmutableTriple<String, DiseaseVariant, String> migrateDiseaseVariant(TestReportDto sourceTestReport) {
		if (sourceTestReport.getTestedDiseaseVariant() == null && sourceTestReport.getTestedDiseaseVariantDetails() == null) {
			return new ImmutableTriple<>(null, null, null);
		}
		String testResultText = null;
		DiseaseVariant testedDiseaseVariant = null;
		String testedDiseaseVariantDetails = null;

		try {
			testedDiseaseVariant = processingFacade.getDiseaseVariant(sourceTestReport.getTestedDiseaseVariant(), externalMessage.getDisease());
			testedDiseaseVariantDetails = sourceTestReport.getTestedDiseaseVariantDetails();
		} catch (CustomEnumNotFoundException e) {
			String diseaseVariantString = sourceTestReport.getTestedDiseaseVariant();
			testResultText = StringUtils.isEmpty(diseaseVariantString)
				? null
				: I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT) + ": " + diseaseVariantString
					+ "\n";

			String diseaseVariantDetailsString = sourceTestReport.getTestedDiseaseVariantDetails();
			if (!StringUtils.isEmpty(diseaseVariantDetailsString)) {
				diseaseVariantDetailsString =
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) + ": "
						+ diseaseVariantDetailsString + "\n";
				testResultText = StringUtils.isEmpty(testResultText) ? diseaseVariantDetailsString : testResultText + diseaseVariantDetailsString;
			}
		}
		return new ImmutableTriple<>(testResultText, testedDiseaseVariant, testedDiseaseVariantDetails);
	}

	private boolean homogenousTestResultTypesIn(SampleReportDto sampleReport) {
		List<TestReportDto> testReports = sampleReport.getTestReports();
		if (testReports != null && !testReports.isEmpty()) {
			List<PathogenTestResultType> testResultTypes = testReports.stream().map(TestReportDto::getTestResult).collect(Collectors.toList());
			return testResultTypes.stream().distinct().count() <= 1;
		} else {
			return false;
		}
	}
}
