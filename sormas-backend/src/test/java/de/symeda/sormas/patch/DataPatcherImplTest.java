package de.symeda.sormas.patch;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.patch.*;
import de.symeda.sormas.api.person.*;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;

class DataPatcherImplTest extends AbstractBeanTest {

	@Test
	void patch_noErrorsReplaceAlways() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		String newLastname = "toto";
		String newSequelaeDetails = "Some very interesting sequelaeDetails";
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"Person.lastName",
					newLastname,

					"CaseData.sequelaeDetails",
					newSequelaeDetails));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			// PERSON
			() -> Assertions.assertEquals(newLastname, actualPerson.getLastName()),
			// CASE
			() -> Assertions.assertEquals(newSequelaeDetails, actualCase.getSequelaeDetails()));
	}

	@Test
	void patch_aliasUsage() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(Map.of("Symptoms.cough", "YES"));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK

		CaseDataDto actual = getCaseFacade().getByUuid(originalCase.getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			// PERSON
			() -> Assertions.assertEquals(SymptomState.YES, actual.getSymptoms().getCough()));
	}

	@Test
	void patch_noErrorsReplaceAlwaysPersonContactDetails() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		String newPhoneNumber = "123654687";
		String newEmail = "name@email.de";
		String origin = "ngSurvey";
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"PersonContactDetail.contactInformation",
					newEmail,

					"Person.personContactDetails.phoneNumberType",
					newPhoneNumber))
			.setOrigin(origin);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK

		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Supplier<Stream<PersonContactDetailDto>> contactDetailsStreamProvider = () -> actualPerson.getPersonContactDetails().stream();
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			// PERSON
			() -> Assertions
				.assertTrue(contactDetailsStreamProvider.get().allMatch(contactDetail -> origin.equals(contactDetail.getAdditionalInformation()))),

			() -> Assertions.assertTrue(
				contactDetailsStreamProvider.get()
					.anyMatch(
						contactDetail -> contactDetail.getPersonContactDetailType() == PersonContactDetailType.PHONE
							&& newPhoneNumber.equals(contactDetail.getContactInformation())
							&& contactDetail.getPhoneNumberType() == PhoneNumberType.OTHER)),

			() -> Assertions.assertTrue(
				contactDetailsStreamProvider.get()
					.anyMatch(
						contactDetail -> contactDetail.getPersonContactDetailType() == PersonContactDetailType.EMAIL
							&& newEmail.equals(contactDetail.getContactInformation()))));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"Task",
		"Event",
		"ExternalMessage" })
	void patch_invalidPrefix(String prefix) {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of(prefix + ".reportingUser", ignoredValue

		);
		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_PREFIX);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Test
	void patch_forbiddenField() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of(
			"Person.birthdate",
			ignoredValue,

			"Person.birthdateDD",
			ignoredValue,
			"Person.birthdateMM",
			ignoredValue,
			"Person.birthdateYYYY",
			ignoredValue);
		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.FORBIDDEN_FIELD);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		// DE
		" weibLiCH ",
		// 	exact match
		"Weiblich",
		"WEIblïch",
		// FR
		"Féminin          ",
		// EN,
		// ENUM exact match
		"FEMALE",
		" femaLe " })
	void patch_enum(String femaleValue) {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(Map.of("Person.sex", femaleValue))
			.setInputLanguages(List.of(Language.DE, Language.EN, Language.FR));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK

		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			// PERSON

			() -> Assertions.assertEquals(Sex.FEMALE, actualPerson.getSex()));
	}

	@Test
	void patch_invalidMultiFieldFormat() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of(
			"Person.(deathDate",
			ignoredValue,

			"Person.((deathDate))",
			ignoredValue,

			"Person.(deathDate)",
			ignoredValue,

			"Person.(deathDate|wefuiohjwerf",
			ignoredValue,

			"Person.(deathDate))",
			ignoredValue,

			"Person.()",
			ignoredValue,

			"Person.)deathDate(",
			ignoredValue);
		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	// the value is properly resolved and set into the person, but when fetching it again it's not there anymore.
	@Test
	void patch_customizableEnu_default_enum() {
		// PREPARE
		OccupationType.getDefaultValues().forEach((k, v) -> {
			CustomizableEnumValue entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.OCCUPATION_TYPE);
			entry.setValue(k);
			entry.setCaption(k);
			entry.setProperties(v);
			entry.setDefaultValue(true);
			getCustomizableEnumValueService().ensurePersisted(entry);
		});

		getCustomizableEnumFacade().loadData();

		String healthcareWorker = "HEALTHCARE_WORKER";
		OccupationType expectedOccupationType =
			getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, healthcareWorker);

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		FacilityDto healthFacility = getFacilityFacade().getByUuid(originalCase.getHealthFacility().getUuid());
		originalCase.setDistrict(healthFacility.getDistrict());
		getCaseFacade().save(originalCase);

		// must be able to ignore accents - whitespaces - case
		String input = "Im Gesundheitswesen tätig";
		Map<String, Object> patchDictionary = Map.of("Person.occupationType", input);
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.DE, Language.EN, Language.FR));;

		Mockito.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, healthcareWorker))
			.thenReturn(expectedOccupationType);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		PersonDto person = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		// CHECK

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(expectedOccupationType, person.getOccupationType()),

			() -> Assertions.assertEquals(toPatchDictionary(patchDictionary), response.getValidPatchDictionary()));
	}

	@Test
	void patch_customizableEnu_default_enum_other() {
		// PREPARE
		OccupationType.getDefaultValues().forEach((k, v) -> {
			CustomizableEnumValue entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.OCCUPATION_TYPE);
			entry.setValue(k);
			entry.setCaption(k);
			entry.setProperties(v);
			entry.setDefaultValue(true);
			getCustomizableEnumValueService().ensurePersisted(entry);
		});

		getCustomizableEnumFacade().loadData();

		String otherOccupationType = "OTHER";
		OccupationType expectedOccupationType =
			getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, otherOccupationType);

		CustomizableEnumValue customizableEnumValue = getCustomizableEnumValueService().getAll()
			.stream()
			.filter(enumMember -> otherOccupationType.equals(enumMember.getValue()))
			.findAny()
			.orElseThrow();

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		FacilityDto healthFacility = getFacilityFacade().getByUuid(originalCase.getHealthFacility().getUuid());
		originalCase.setDistrict(healthFacility.getDistrict());
		getCaseFacade().save(originalCase);

		// must be able to ignore accents - whitespaces - case
		String input = "DOES NOT MATCH TO Anythign";
		Map<String, Object> patchDictionary = Map.of("Person.(occupationType|occupationDetails|additionalDetails)", input);
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary);

		Mockito
			.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, otherOccupationType))
			.thenReturn(expectedOccupationType);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		PersonDto person = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		// CHECK

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(expectedOccupationType, person.getOccupationType()),
			() -> Assertions.assertEquals(input, person.getOccupationDetails()),

			() -> Assertions.assertEquals(
				toPatchDictionary(Map.of("Person.occupationType", input, "Person.occupationDetails", input, "Person.additionalDetails", input)),
				response.getValidPatchDictionary()));
	}

	private static @NotNull CustomizableEnumTranslation buildTranslation(String en, String irrelated) {
		CustomizableEnumTranslation e1 = new CustomizableEnumTranslation();
		e1.setLanguageCode(en);
		e1.setValue(irrelated);
		return e1;
	}

	@Test
	void patch_referenceData_country() {
		// PREPARE
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

		CountryDto dto = new CountryDto();
		dto.setIsoCode("DEU");
		dto.setDefaultName("Germany");
		CountryDto germanyReferenceDto = getCountryFacade().save(dto);

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		// must be able to ignore accents - whitespaces - case
		Map<String, Object> patchDictionary = Map.of("Person.birthCountry", " Deutschländ    ");
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.DE, Language.EN, Language.FR));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		// CHECK

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(
				new CountryReferenceDto(germanyReferenceDto.getUuid(), germanyReferenceDto.getIsoCode()),
				actualPerson.getBirthCountry()));
	}

	@Test
	void patch_notSupportedForDisease() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of("CaseData.plagueType", ignoredValue);

		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Test
	void patch_notSupportedForCountry() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of("CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue);

		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Test
	void patch_notSupportedFeature() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of("CaseData.caseReferenceNumber", ignoredValue);

		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	private static @NotNull Map<PatchField, DataPatchFailure> buildDictionaryOfFailureType(
		Map<String, Object> patchDictionary,
		DataPatchFailureCause unsupportedFieldForDisease) {
		return patchDictionary.entrySet()
			.stream()
			.map(
				entry -> Map.entry(
					PatchField.of(entry.getKey()),
					new DataPatchFailure().setDataPatchFailureCause(unsupportedFieldForDisease).setProvidedFieldValue(entry.getValue())))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Test
	void patch_patchInCaseOfFailureTrue() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		String trueString = " ja    ";
		Map<String, Object> patchDictionary =
			Map.of("CaseData.symptoms.cough", trueString, "CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setPatchedInCaseOfFailures(true)
			.setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.DE));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		CaseDataDto actual = getCaseFacade().getByUuid(originalCase.getUuid());

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(
			Map.of("CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue),
			DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.isApplied()),

			() -> Assertions.assertEquals(toPatchDictionary(Map.of("CaseData.symptoms.cough", trueString)), response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(SymptomState.YES, actual.getSymptoms().getCough()),

			() -> Assertions.assertNull(actual.getQuarantineOrderedOfficialDocumentDate()),

			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Test
	void patch_noPatchInCaseOfFailureFalse() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		String trueString = " ja    ";
		Map<String, Object> patchDictionary =
			Map.of("CaseData.symptoms.cough", trueString, "CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setPatchedInCaseOfFailures(false)
			.setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.DE));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		CaseDataDto actual = getCaseFacade().getByUuid(originalCase.getUuid());

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(
			Map.of("CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue),
			DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),

			() -> Assertions.assertEquals(toPatchDictionary(Map.of("CaseData.symptoms.cough", trueString)), response.getValidPatchDictionary()),

			() -> Assertions.assertNull(actual.getSymptoms().getCough()),

			() -> Assertions.assertNull(actual.getQuarantineOrderedOfficialDocumentDate()),

			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Test
	void patch_replacementMode_null_value() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);
		originalCase.setQuarantineChangeComment("some non empty value");

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("CaseData.quarantineChangeComment", null);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setEmptyValueBehavior(EmptyValueBehavior.REPLACE)
			.setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		CaseDataDto actual = getCaseFacade().getByUuid(originalCase.getUuid());

		// CHECK
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.isApplied()),

			() -> Assertions.assertEquals(toPatchDictionary(patchDictionary), response.getValidPatchDictionary()),

			() -> Assertions.assertNull(actual.getQuarantineChangeComment()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
	}

	@Test
	void patch_replacementMode_null_value_error() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);
		String expectedQuarantineChangeComment = "some non empty value";
		originalCase.setQuarantineChangeComment(expectedQuarantineChangeComment);

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("CaseData.quarantineChangeComment", null);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setEmptyValueBehavior(EmptyValueBehavior.IGNORE)
			.setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		CaseDataDto actual = getCaseFacade().getByUuid(originalCase.getUuid());

		// CHECK
		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),

			() -> Assertions.assertEquals(new PatchDictionary(), response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(expectedQuarantineChangeComment, actual.getQuarantineChangeComment()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
	}

	@Test
	void patch_fieldDoesNoExist() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("CaseData.NON_EXISTING_FIELD", "validValue");

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.FIELD_DOES_NOT_EXIST);

		// CHECK
		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),

			() -> Assertions.assertEquals(expectedFailures, response.getFailures()),

			() -> Assertions.assertEquals(new PatchDictionary(), response.getValidPatchDictionary()));
	}

	@Test
	void patch_epiData() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"EpiData.exposureDetailsKnown",
					"YES",

					"EpiData.contactWithSourceCaseKnown",
					"NO"));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK

		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			() -> Assertions.assertEquals(YesNoUnknown.YES, actualCase.getEpiData().getExposureDetailsKnown()),
			() -> Assertions.assertEquals(YesNoUnknown.NO, actualCase.getEpiData().getContactWithSourceCaseKnown()));
	}

	@Test
	void patch_ifNotAlreadyPresent_sameDayDifferentTime_noForbiddenValueOverride() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		// Set classificationDate to 08:30 on 2024-06-15 — a non-midnight timestamp on the same calendar day that will be patched
		java.util.Date existingDate = java.util.Date.from(LocalDateTime.of(2024, 6, 15, 12, 30, 0).atZone(ZoneId.systemDefault()).toInstant());
		originalCase.setReportDate(existingDate);
		getCaseFacade().save(originalCase);

		// Patch with the same calendar day as a plain date string — DatePatchMapper resolves this to midnight (00:00:00),
		// which differs in time from existingDate. Without DateEqualityChecker this would trigger FORBIDDEN_VALUE_OVERRIDE.
		String patchDate = "2024-06-15";
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(Map.of(toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE), patchDate));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "FORBIDDEN_VALUE_OVERRIDE must not fire for same-day dates"),
			() -> Assertions.assertTrue(response.isApplied()));
	}

	@Test
	void patch_hospitalization() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.DENGUE);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						// YesNoUnknown enum
						toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY),
						"YES",

						// Date
						toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE),
						"2024-05-10",

						// String
						toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.DESCRIPTION),
						"patient admitted urgently",

						// HospitalizationReasonType enum
						toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.HOSPITALIZATION_REASON),
						"ISOLATION")));

		// CHECK

		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertEquals(YesNoUnknown.YES, actualCase.getHospitalization().getAdmittedToHealthFacility()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse("2024-05-10").atStartOfDay(ZoneId.systemDefault()).toInstant()),
				actualCase.getHospitalization().getAdmissionDate()),
			() -> Assertions.assertEquals("patient admitted urgently", actualCase.getHospitalization().getDescription()),
			() -> Assertions.assertEquals(HospitalizationReasonType.ISOLATION, actualCase.getHospitalization().getHospitalizationReason()));
	}

	@Test
	void patch_vaccination_only() {
		// PREPARE
		Disease disease = Disease.RESPIRATORY_SYNCYTIAL_VIRUS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of("Vaccination.vaccineName", "COMIRNATY")));

		// CHECK
		List<ImmunizationDto> immunizations = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, immunizations.size()),
			() -> Assertions.assertEquals(1, immunizations.get(0).getVaccinations().size()),
			() -> Assertions.assertEquals(Vaccine.COMIRNATY, immunizations.get(0).getVaccinations().get(0).getVaccineName()));
	}

	@Test
	void patch_vaccination_and_immunization() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						"Vaccination.vaccineName",
						"COMIRNATY",

						"Immunization.immunizationStatus",
						"ACQUIRED")));

		// CHECK
		List<ImmunizationDto> immunizations = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, immunizations.size()),
			() -> Assertions.assertEquals(ImmunizationStatus.ACQUIRED, immunizations.get(0).getImmunizationStatus()),
			() -> Assertions.assertEquals(1, immunizations.get(0).getVaccinations().size()),
			() -> Assertions.assertEquals(Vaccine.COMIRNATY, immunizations.get(0).getVaccinations().get(0).getVaccineName()));
	}

	@Test
	void patch_vaccination_and_immunization_with_existing_creates_new_without_override() {
		// PREPARE
		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		ImmunizationDto existingImmunization = ImmunizationDto.build(originalCase.getPerson());
		existingImmunization.setRelatedCase(originalCase.toReference());
		existingImmunization.setImmunizationStatus(ImmunizationStatus.NOT_ACQUIRED);
		existingImmunization.setReportingUser(originalCase.getReportingUser());
		VaccinationDto existingVaccination = VaccinationDto.build(originalCase.getReportingUser());
		existingVaccination.setVaccineName(Vaccine.COMIRNATY);
		existingImmunization.setVaccinations(List.of(existingVaccination));
		getImmunizationFacade().save(existingImmunization);

		// EXECUTE — patch with ALWAYS strategy creates new immunization + vaccination, never modifies existing ones
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						"Vaccination.vaccineName",
						"MRNA_1273",

						"Immunization.immunizationStatus",
						"ACQUIRED")));

		// CHECK
		List<ImmunizationDto> immunizations = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),

			// Two immunizations: original unchanged + new one from patch
			() -> Assertions.assertEquals(2, immunizations.size()),

			// Original immunization is untouched
			() -> Assertions.assertTrue(immunizations.stream().anyMatch(imm -> ImmunizationStatus.NOT_ACQUIRED.equals(imm.getImmunizationStatus()))),

			// New immunization was created with the patched status
			() -> Assertions.assertTrue(immunizations.stream().anyMatch(imm -> ImmunizationStatus.ACQUIRED.equals(imm.getImmunizationStatus()))),

			// Original vaccination (COMIRNATY) is still there
			() -> Assertions.assertTrue(
				immunizations.stream()
					.flatMap(imm -> imm.getVaccinations().stream())
					.anyMatch(vac -> Vaccine.COMIRNATY.equals(vac.getVaccineName()))),

			// New vaccination (MRNA_1273) was created by the patch
			() -> Assertions.assertTrue(
				immunizations.stream()
					.flatMap(imm -> imm.getVaccinations().stream())
					.anyMatch(vac -> Vaccine.MRNA_1273.equals(vac.getVaccineName()))));
	}

	@Test
	void patch_exposure() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE),
						"WORK",
						toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION),
						"market visit")));

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<ExposureDto> exposures = actualCase.getEpiData().getExposures();
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, exposures.size()),
			() -> Assertions.assertEquals(ExposureType.WORK, exposures.get(0).getExposureType()),
			() -> Assertions.assertEquals("market visit", exposures.get(0).getDescription()));
	}

	@Test
	void patch_previousHospitalization() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);
		String facilityCaption = originalCase.getHealthFacility().getCaption();

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ADMITTED_TO_HEALTH_FACILITY),
						"YES",

						toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ADMISSION_DATE),
						"2024-03-15",

						toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ICU_LENGTH_OF_STAY),
						"7")));

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<PreviousHospitalizationDto> previousHospitalizations = actualCase.getHospitalization().getPreviousHospitalizations();
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, previousHospitalizations.size()),
			() -> Assertions.assertEquals(YesNoUnknown.YES, previousHospitalizations.get(0).getAdmittedToHealthFacility()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse("2024-03-15").atStartOfDay(ZoneId.systemDefault()).toInstant()),
				previousHospitalizations.get(0).getAdmissionDate()),
			() -> Assertions.assertEquals(7, previousHospitalizations.get(0).getIcuLengthOfStay()));
	}

	@Test
	void patch_previousHospitalization_admissionAndDischargeDates() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		String admissionDate = "2026-02-02";
		String dischargeDate = "2026-02-04";

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setPatchDictionary(
					Map.of(
						toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ADMISSION_DATE),
						admissionDate,

						toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.DISCHARGE_DATE),
						dischargeDate)));

		// CHECK

		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<PreviousHospitalizationDto> previousHospitalizations = actualCase.getHospitalization().getPreviousHospitalizations();
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, previousHospitalizations.size()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse(admissionDate).atStartOfDay(ZoneId.systemDefault()).toInstant()),
				previousHospitalizations.get(0).getAdmissionDate()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse(dischargeDate).atStartOfDay(ZoneId.systemDefault()).toInstant()),
				previousHospitalizations.get(0).getDischargeDate()));
	}

	@Test
	void patch_activityAsCase() {
		// PREPARE
		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.of(
						toFieldName(ActivityAsCaseDto.I18N_PREFIX, ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE),
						"WORK",
						toFieldName(ActivityAsCaseDto.I18N_PREFIX, ActivityAsCaseDto.DESCRIPTION),
						"office work")));

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<ActivityAsCaseDto> activities = actualCase.getEpiData().getActivitiesAsCase();
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(1, activities.size()),
			() -> Assertions.assertEquals(ActivityAsCaseType.WORK, activities.get(0).getActivityAsCaseType()),
			() -> Assertions.assertEquals("office work", activities.get(0).getDescription()));
	}

	@Test
	void patch_forbiddenMultiGroupField() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		PersonDto originalPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());
		String originalLastName = originalPerson.getLastName();

		String secondPersonLastName = "secondPersonLastName";

		// Use PatchDictionary with groupIndex=1 to simulate inserting a "second person".
		// Person is a singular entity (always attached), so grouping is forbidden.
		PatchDictionary patchDictionary = new PatchDictionary();
		patchDictionary.put(PatchField.of("Person.lastName", 1), secondPersonLastName);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		PatchField expectedField = PatchField.of("Person.lastName", 1);
		DataPatchFailure expectedFailure = new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_MULTI_GROUP_FIELD);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched"),
			() -> Assertions.assertFalse(response.isApplied()),
			() -> Assertions.assertEquals(Map.of(expectedField, expectedFailure), response.getFailures()),
			() -> Assertions.assertEquals(originalLastName, actualPerson.getLastName(), "Person lastName must not be altered"));
	}

	private static String toFieldName(String prefix, String field) {
		return prefix + '.' + field;
	}

	private DataPatcher victim() {
		return getCaseDataPatcher();
	}

	private PatchDictionary toPatchDictionary(Map<String, Object> patchDictionary) {
		PatchDictionary wrapper = new PatchDictionary();

		patchDictionary.forEach(wrapper::put);

		return wrapper;
	}
}
