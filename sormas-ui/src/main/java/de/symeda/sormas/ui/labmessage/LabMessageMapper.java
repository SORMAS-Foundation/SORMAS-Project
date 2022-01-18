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

package de.symeda.sormas.ui.labmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DataHelper;

public class LabMessageMapper {

	private final LabMessageDto labMessage;

	public static LabMessageMapper forLabMessage(LabMessageDto labMessage) {
		return new LabMessageMapper(labMessage);
	}

	private LabMessageMapper(LabMessageDto labMessage) {
		this.labMessage = labMessage;
	}

	public List<String[]> mapToPerson(PersonDto person) {
		List<String[]> changedFields = map(
			Stream.of(
				Mapping.of(person::setFirstName, person.getFirstName(), labMessage.getPersonFirstName(), PersonDto.FIRST_NAME),
				Mapping.of(person::setLastName, person.getLastName(), labMessage.getPersonLastName(), PersonDto.LAST_NAME),
				Mapping.of(person::setBirthdateDD, person.getBirthdateDD(), labMessage.getPersonBirthDateDD(), PersonDto.BIRTH_DATE_DD),
				Mapping.of(person::setBirthdateMM, person.getBirthdateMM(), labMessage.getPersonBirthDateMM(), PersonDto.BIRTH_DATE_MM),
				Mapping.of(person::setBirthdateYYYY, person.getBirthdateYYYY(), labMessage.getPersonBirthDateYYYY(), PersonDto.BIRTH_DATE_YYYY),
				Mapping.of(person::setSex, person.getSex(), labMessage.getPersonSex(), PersonDto.SEX),
				Mapping.of(person::setPhone, person.getPhone(), labMessage.getPersonPhone(), PersonDto.PERSON_CONTACT_DETAILS),
				Mapping.of(person::setEmailAddress, person.getEmailAddress(), labMessage.getPersonEmail(), PersonDto.PERSON_CONTACT_DETAILS)));

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
		return map(
			Stream.of(
				Mapping.of(location::setStreet, location.getStreet(), labMessage.getPersonStreet(), PersonDto.ADDRESS, LocationDto.STREET),
				Mapping.of(
					location::setHouseNumber,
					location.getHouseNumber(),
					labMessage.getPersonHouseNumber(),
					PersonDto.ADDRESS,
					LocationDto.HOUSE_NUMBER),
				Mapping.of(
					location::setPostalCode,
					location.getPostalCode(),
					labMessage.getPersonPostalCode(),
					PersonDto.ADDRESS,
					LocationDto.POSTAL_CODE),
				Mapping.of(location::setCity, location.getCity(), labMessage.getPersonCity(), PersonDto.ADDRESS, LocationDto.CITY)));
	}

	public List<String[]> mapToSample(SampleDto sample) {
		List<String[]> changedFields = map(
			Stream.of(
				Mapping.of(sample::setSampleDateTime, sample.getSampleDateTime(), labMessage.getSampleDateTime(), SampleDto.SAMPLE_DATE_TIME),
				Mapping.of(sample::setSampleMaterial, sample.getSampleMaterial(), labMessage.getSampleMaterial(), SampleDto.SAMPLE_MATERIAL),
				Mapping.of(
					sample::setSampleMaterialText,
					sample.getSampleMaterialText(),
					labMessage.getSampleMaterialText(),
					SampleDto.SAMPLE_MATERIAL_TEXT),
				Mapping
					.of(sample::setSpecimenCondition, sample.getSpecimenCondition(), labMessage.getSpecimenCondition(), SampleDto.SPECIMEN_CONDITION),
				Mapping.of(sample::setLab, sample.getLab(), getLabReference(labMessage.getLabExternalId()), SampleDto.LAB),
				Mapping.of(sample::setLabDetails, sample.getLabDetails(), labMessage.getLabName(), SampleDto.LAB_DETAILS)));

		if (labMessage.getSampleReceivedDate() != null) {
			changedFields.addAll(
				map(
					Stream.of(
						Mapping.of(sample::setReceived, sample.isReceived(), true, SampleDto.RECEIVED),
						Mapping.of(sample::setReceivedDate, sample.getReceivedDate(), labMessage.getSampleReceivedDate(), SampleDto.RECEIVED_DATE),
						Mapping.of(sample::setLabSampleID, sample.getLabSampleID(), labMessage.getLabSampleId(), SampleDto.LAB_SAMPLE_ID))));
		}

		PathogenTestResultType pathogenTestResult = null;
		if (labMessage.getSampleOverallTestResult() != null) {
			pathogenTestResult = labMessage.getSampleOverallTestResult();
		} else if (homogenousTestResultTypesIn(labMessage)) {
			pathogenTestResult = labMessage.getTestReports().get(0).getTestResult();
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
			changedFields.addAll(
				map(
					Stream.of(
						Mapping.of(
							pathogenTest::setTestResult,
							pathogenTest.getTestResult(),
							sourceTestReport.getTestResult(),
							PathogenTestDto.TEST_RESULT),
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
						Mapping.of(
							pathogenTest::setTestResultText,
							pathogenTest.getTestResultText(),
							sourceTestReport.getTestResultText(),
							PathogenTestDto.TEST_RESULT_TEXT),
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
							getLabReference(sourceTestReport.getTestLabExternalId()),
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
							PathogenTestDto.PRELIMINARY))));
		}

		changedFields.addAll(
			map(
				Stream.of(
					Mapping.of(
						pathogenTest::setTestedDisease,
						pathogenTest.getTestedDisease(),
						labMessage.getTestedDisease(),
						PathogenTestDto.TESTED_DISEASE),
					Mapping
						.of(pathogenTest::setReportDate, pathogenTest.getReportDate(), getPathogenTestReportDate(), PathogenTestDto.REPORT_DATE))));

		return changedFields;
	}

	private Date getPathogenTestReportDate() {
		Date reportDate = null;
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			reportDate = labMessage.getMessageDateTime();
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
	}

	private FacilityReferenceDto getLabReference(String labExternalId) {
		FacilityFacade facilityFacade = FacadeProvider.getFacilityFacade();
		List<FacilityReferenceDto> labs =
			labExternalId != null ? facilityFacade.getByExternalIdAndType(labExternalId, FacilityType.LABORATORY, false) : null;
		if (labs != null && labs.size() == 1) {
			return labs.get(0);
		} else {
			return facilityFacade.getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);
		}
	}

	private boolean homogenousTestResultTypesIn(LabMessageDto labMessage) {
		List<TestReportDto> testReports = labMessage.getTestReports();
		if (testReports != null && !testReports.isEmpty()) {
			List<PathogenTestResultType> testResultTypes = testReports.stream().map(TestReportDto::getTestResult).collect(Collectors.toList());
			return testResultTypes.stream().distinct().count() <= 1;
		} else {
			return false;
		}
	}
}
