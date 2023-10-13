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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public final class ExternalMessageMapper {

	private final ExternalMessageDto externalMessage;

	public static ExternalMessageMapper forLabMessage(ExternalMessageDto externalMessage) {
		return new ExternalMessageMapper(externalMessage);
	}

	private ExternalMessageMapper(ExternalMessageDto externalMessage) {
		this.externalMessage = externalMessage;
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
					PersonDto.NATIONAL_HEALTH_ID)));

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

	public List<String[]> mapToLocation(LocationDto location) {

		RegionReferenceDto region = null;
		DistrictReferenceDto district = null;
		FacilityType facilityType = null;
		if (externalMessage.getPersonFacility() != null) {
			FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(externalMessage.getPersonFacility().getUuid());
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

	public List<String[]> mapFirstSampleReportToSample(SampleDto sample) {
		return mapToSample(sample, externalMessage.getSampleReportsNullSafe().get(0));
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
				Mapping.of(sample::setLab, sample.getLab(), getFacilityReference(externalMessage.getReporterExternalIds()), SampleDto.LAB),
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
							getFacilityReference(sourceTestReport.getTestLabExternalIds()),
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
							PathogenTestDto.PRESCRIBER_COUNTRY))));
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

	private Date getPathogenTestReportDate() {
		Date reportDate = null;
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			reportDate = externalMessage.getMessageDateTime();
		}
		return reportDate;
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
			testedDiseaseVariant = FacadeProvider.getCustomizableEnumFacade()
				.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, sourceTestReport.getTestedDiseaseVariant(), externalMessage.getDisease());
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

	public static FacilityReferenceDto getFacilityReference(List<String> facilityExternalIds) {

		FacilityFacade facilityFacade = FacadeProvider.getFacilityFacade();
		List<FacilityReferenceDto> labs;

		if (facilityExternalIds != null && !facilityExternalIds.isEmpty()) {

			labs = facilityExternalIds.stream()
				.filter(Objects::nonNull)
				.map(id -> facilityFacade.getByExternalIdAndType(id, FacilityType.LABORATORY, false))
				.flatMap(List::stream)
				.collect(Collectors.toList());
		} else {
			labs = null;
		}

		if (labs == null || labs.isEmpty()) {
			return facilityFacade.getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);
		} else if (labs.size() == 1) {
			return labs.get(0);
		} else {
			return null;
		}
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
