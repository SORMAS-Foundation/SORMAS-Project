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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataPatcher;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.EmptyValueBehavior;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryReferenceDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldMetadataFacadeEjb;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.EqualValueOverrideHelper;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationCategory;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationCategoryService;

class DataPatcherImplTest extends AbstractBeanTest {

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
	void patch_notSupportedForDisease_symptoms_offset() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.GIARDIASIS);

		Map<String, Object> patchDictionary = Map.of("CaseData.symptoms.offsetDate", new java.util.Date());

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
	void patch_ifNotAlreadyPresent_equalValueOverrideConfiguredForType_realSystemConfigurationAllowsOverride() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		originalCase.getEpiData().setExposureDetailsKnown(YesNoUnknown.UNKNOWN);
		getCaseFacade().save(originalCase);

		setAllowedEqualityValueOverride("YesNoUnknown___UNKNOWN");

		CaseDataPatchRequest request =
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(Map.of("EpiData.exposureDetailsKnown", "YES"));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(YesNoUnknown.YES, actualCase.getEpiData().getExposureDetailsKnown()));
	}

	private void setAllowedEqualityValueOverride(String overridableValue) {
		SystemConfigurationCategory category;
		try {
			category = getSystemConfigurationCategoryService().getDefaultCategory();
		} catch (IllegalStateException e) {
			category = new SystemConfigurationCategory();
			category.setUuid(DataHelper.createUuid());
			category.setName(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME);
			getSystemConfigurationCategoryService().ensurePersisted(category);
		}

		SystemConfigurationValueDto configValue = new SystemConfigurationValueDto();

		configValue.setUuid(DataHelper.createUuid());
		configValue.setKey(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY);
		configValue.setValue(overridableValue);
		configValue.setEncrypt(false);
		configValue.setCategory(new SystemConfigurationCategoryReferenceDto(category.getUuid()));

		getSystemConfigurationValueFacade().save(configValue);
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
	void patch_grouped_twoImmunizationsThreeVaccinations_thirdVaccinationCreatesNewImmunization() {
		// PREPARE
		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		PatchDictionary patchDictionary = new PatchDictionary();
		patchDictionary.put(PatchField.of(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_STATUS), 0), "ACQUIRED");
		patchDictionary.put(PatchField.of(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME), 0), "COMIRNATY");

		patchDictionary.put(PatchField.of(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_STATUS), 1), "NOT_ACQUIRED");
		patchDictionary.put(PatchField.of(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME), 1), "MRNA_1273");

		patchDictionary.put(PatchField.of(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME), 2), "AD26_COV2_S");

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(patchDictionary));

		// CHECK
		List<ImmunizationDto> immunizations = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));
		List<VaccinationDto> allVaccinations = immunizations.stream().flatMap(imm -> imm.getVaccinations().stream()).collect(Collectors.toList());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),

			() -> Assertions.assertEquals(3, immunizations.size()),
			() -> Assertions.assertEquals(3, allVaccinations.size()),
			() -> Assertions.assertTrue(immunizations.stream().allMatch(imm -> imm.getVaccinations().size() == 1)),

			() -> Assertions.assertTrue(
				immunizations.stream()
					.anyMatch(
						imm -> ImmunizationStatus.ACQUIRED.equals(imm.getImmunizationStatus())
							&& imm.getVaccinations().stream().anyMatch(vac -> Vaccine.COMIRNATY.equals(vac.getVaccineName())))),

			() -> Assertions.assertTrue(
				immunizations.stream()
					.anyMatch(
						imm -> ImmunizationStatus.NOT_ACQUIRED.equals(imm.getImmunizationStatus())
							&& imm.getVaccinations().stream().anyMatch(vac -> Vaccine.MRNA_1273.equals(vac.getVaccineName())))),

			() -> Assertions.assertTrue(allVaccinations.stream().anyMatch(vac -> Vaccine.AD26_COV2_S.equals(vac.getVaccineName()))));
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
	void patch_multipleEntities_caseDataExposureHospitalizationSymptoms() {
		// This modifies many entities are triggers case update.
		// Might require changing outdated range to reproduce every time
		// PREPARE
		Disease disease = Disease.MEASLES;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(
					Map.ofEntries(
						// CaseData
						Map.entry("CaseData.quarantineChangeComment", "some comment"),
						Map.entry(toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.FOLLOW_UP_STATUS), FollowUpStatus.FOLLOW_UP),

						// Exposures
						Map.entry(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE), "WORK"),
						Map.entry(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION), "market visit"),

						// EpiData
						Map.entry("EpiData.exposureDetailsKnown", "YES"),
						Map.entry("EpiData.contactWithSourceCaseKnown", "NO"),

						// Hospitalization
						Map.entry(toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY), "YES"),
						Map.entry(toFieldName(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE), "2024-05-10"),

						// Previous hospitalization
						Map.entry(toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ADMITTED_TO_HEALTH_FACILITY), "YES"),
						Map.entry(toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ADMISSION_DATE), "2024-03-15"),
						Map.entry(toFieldName(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ICU_LENGTH_OF_STAY), "7"),

						// Immunization — deliberately made ACQUIRED + valid across the case reportDate so that
						// saving the Immunization/Vaccination (their own facades, not CaseFacade) forces
						// CaseService#updateDeterminedVaccinationStatuses to recompute and persist the Case's vaccinationStatus.
						Map.entry(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_STATUS), "ACQUIRED"),
						Map.entry(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.MEANS_OF_IMMUNIZATION), "VACCINATION"),
						Map.entry(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.VALID_FROM), "2020-01-01"),
						Map.entry(toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.VALID_UNTIL), "2035-01-01"),

						// Vaccination
						Map.entry(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME), "COMIRNATY"),
						Map.entry(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINATION_DATE), "2024-05-01"),
						Map.entry(toFieldName(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_DOSE), "1"),

						// Person — saving Person cascades into CaseFacade#onCaseChanged for every case of that person
						Map.entry("Person.sex", "MALE"),

						// Symptoms
						Map.entry("Symptoms.cough", "YES"))));

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<ExposureDto> exposures = actualCase.getEpiData().getExposures();
		List<PreviousHospitalizationDto> previousHospitalizations = actualCase.getHospitalization().getPreviousHospitalizations();
		List<ImmunizationDto> immunizations = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));
		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),

			// CaseData
			() -> Assertions.assertEquals("some comment", actualCase.getQuarantineChangeComment()),

			// Exposures
			() -> Assertions.assertEquals(1, exposures.size()),
			() -> Assertions.assertEquals(ExposureType.WORK, exposures.get(0).getExposureType()),
			() -> Assertions.assertEquals("market visit", exposures.get(0).getDescription()),

			// EpiData
			() -> Assertions.assertEquals(YesNoUnknown.YES, actualCase.getEpiData().getExposureDetailsKnown()),
			() -> Assertions.assertEquals(YesNoUnknown.NO, actualCase.getEpiData().getContactWithSourceCaseKnown()),

			// Hospitalization
			() -> Assertions.assertEquals(YesNoUnknown.YES, actualCase.getHospitalization().getAdmittedToHealthFacility()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse("2024-05-10").atStartOfDay(ZoneId.systemDefault()).toInstant()),
				actualCase.getHospitalization().getAdmissionDate()),

			// Previous hospitalization
			() -> Assertions.assertEquals(1, previousHospitalizations.size()),
			() -> Assertions.assertEquals(YesNoUnknown.YES, previousHospitalizations.get(0).getAdmittedToHealthFacility()),
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse("2024-03-15").atStartOfDay(ZoneId.systemDefault()).toInstant()),
				previousHospitalizations.get(0).getAdmissionDate()),
			() -> Assertions.assertEquals(7, previousHospitalizations.get(0).getIcuLengthOfStay()),

			// Immunization / Vaccination
			() -> Assertions.assertEquals(1, immunizations.size()),
			() -> Assertions.assertEquals(ImmunizationStatus.ACQUIRED, immunizations.get(0).getImmunizationStatus()),
			() -> Assertions.assertEquals(MeansOfImmunization.VACCINATION, immunizations.get(0).getMeansOfImmunization()),
			() -> Assertions.assertEquals(1, immunizations.get(0).getVaccinations().size()),
			() -> Assertions.assertEquals(Vaccine.COMIRNATY, immunizations.get(0).getVaccinations().get(0).getVaccineName()),
			() -> Assertions.assertEquals("1", immunizations.get(0).getVaccinations().get(0).getVaccineDose()),

			// Case's vaccinationStatus is not set directly by the patch — it is only recomputed as a side effect
			// of ImmunizationFacade/VaccinationFacade#save() calling CaseService#updateDeterminedVaccinationStatuses
			() -> Assertions.assertEquals(VaccinationStatus.VACCINATED, actualCase.getVaccinationStatus()),

			// Person — proves saving Person cascades an update into the case as well
			() -> Assertions.assertEquals(Sex.MALE, actualPerson.getSex()),

			// Symptoms
			() -> Assertions.assertEquals(SymptomState.YES, actualCase.getSymptoms().getCough()));
	}

	@Test
	void patch_grouped_twoExposures() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		ExposureType firstExposureType = ExposureType.WORK;

		String firstDescription = "WORK visit";

		ExposureType secondExposureType = ExposureType.TRAVEL;
		String secondDescription = "Iceland";

		// Both groups share the same values — the groupIndex drives entity separation, not different content
		PatchDictionary patchDictionary = new PatchDictionary();
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE), 0), firstExposureType.name());
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION), 0), firstDescription);

		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE), 1), secondExposureType.name());
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION), 1), secondDescription);

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(patchDictionary));

		// CHECK
		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		List<ExposureDto> exposures = actualCase.getEpiData().getExposures();

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),

			() -> Assertions.assertEquals(2, exposures.size()),

			() -> Assertions.assertEquals(firstExposureType, exposures.get(0).getExposureType()),
			() -> Assertions.assertEquals(secondExposureType, exposures.get(1).getExposureType()),

			() -> Assertions.assertEquals(firstDescription, exposures.get(0).getDescription()),
			() -> Assertions.assertEquals(secondDescription, exposures.get(1).getDescription()));
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

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_text() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfText", CustomizableFieldType.TEXT);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfText";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "hello")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("hello", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "world")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("world", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_text_epidata() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createEpidataCustomField("cfText", CustomizableFieldType.TEXT);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + EpiDataDto.I18N_PREFIX + ".cfText";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "hello")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForEpiData(caze.getEpiData().getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("hello", values1.get(metadata).getValue()));
	}

	private CustomizableFieldMetadataDto createEpidataCustomField(String name, CustomizableFieldType type) {
		return createCustomFieldFor(name, type, CustomizableFieldContext.EPIDATA);
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_textarea() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfTextarea", CustomizableFieldType.TEXTAREA);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfTextarea";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "first paragraph")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("first paragraph", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "second paragraph")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("second paragraph", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_number() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfNumber", CustomizableFieldType.NUMBER);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfNumber";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "42")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("42", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "99")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("99", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_decimal() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfDecimal", CustomizableFieldType.DECIMAL);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfDecimal";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "3.14")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("3.14", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "2.71")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("2.71", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_combobox() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfCombobox", CustomizableFieldType.COMBOBOX);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfCombobox";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "option_a")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("option_a", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "option_b")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("option_b", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_checkbox() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfCheckbox", CustomizableFieldType.CHECKBOX);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfCheckbox";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "true")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals(Boolean.TRUE, values1.get(metadata).getValueAsBoolean()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "false")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals(Boolean.FALSE, values2.get(metadata).getValueAsBoolean()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_radioButtonList() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfRadio", CustomizableFieldType.RADIO_BUTTON_LIST);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfRadio";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "choice_1")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals("choice_1", values1.get(metadata).getValue()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "choice_2")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals("choice_2", values2.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_date() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfDate", CustomizableFieldType.DATE);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfDate";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "2024-06-15")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals(LocalDate.of(2024, 6, 15), values1.get(metadata).getValueAsDate()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "2025-03-21")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals(LocalDate.of(2025, 3, 21), values2.get(metadata).getValueAsDate()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_dateTime() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfDateTime", CustomizableFieldType.DATE_TIME);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfDateTime";

		// EXECUTE — value does not yet exist
		DataPatchResponse response1 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "2024-06-15T10:30")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values1 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response1.getFailures().isEmpty(), "Failures: " + response1.getFailures()),
			() -> Assertions.assertTrue(response1.isApplied()),
			() -> Assertions.assertEquals(LocalDateTime.of(2024, 6, 15, 10, 30), values1.get(metadata).getValueAsDateTime()));

		// EXECUTE — value was updated
		DataPatchResponse response2 = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "2025-01-01T08:00")));

		// CHECK
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values2 = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response2.getFailures().isEmpty(), "Failures: " + response2.getFailures()),
			() -> Assertions.assertTrue(response2.isApplied()),
			() -> Assertions.assertEquals(LocalDateTime.of(2025, 1, 1, 8, 0), values2.get(metadata).getValueAsDateTime()));
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

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_combobox_overridesExistingValue() {
		// PREPARE — seed value A directly via the facade (not through the patcher)
		CustomizableFieldMetadataDto metadata = createCaseCustomField("cfComboboxOverride", CustomizableFieldType.COMBOBOX);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String fieldKey = "Custom." + CaseDataDto.I18N_PREFIX + ".cfComboboxOverride";

		CustomizableFieldValueDto seedValue = new CustomizableFieldValueDto();
		seedValue.setValue("option_A");
		getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class)
			.saveEntityCustomFields(caze.getUuid(), CustomizableFieldContext.CASE, Map.of(metadata, seedValue));

		// EXECUTE — patch overrides with value B
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(Map.of(fieldKey, "option_B")));

		// CHECK — option_A was replaced by option_B
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values = customizableValuesForCase(caze.getUuid());
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals("option_B", values.get(metadata).getValue()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_invalidCustomContext() {
		// PREPARE — "Custom." prefix present but the context segment is not a known I18N prefix
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String value = "someValue";
		Map<String, Object> patchDictionary = Map.of("Custom.UnknownContext.someField", value);

		// EXECUTE
		DataPatchResponse response = victim().patch(new CaseDataPatchRequest().setCaseUuid(caze.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.INVALID_CUSTOM_CONTEXT);

		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty()),
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@Tag("customizable-fields")
	@Test
	void patch_customizableField_fieldDoesNotExist() {
		// PREPARE — valid context (CaseData) but no metadata registered for the given field name
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);
		String value = "someValue";
		Map<String, Object> patchDictionary = Map.of("Custom." + CaseDataDto.I18N_PREFIX + ".nonExistentField", value);

		// EXECUTE
		DataPatchResponse response = victim().patch(new CaseDataPatchRequest().setCaseUuid(caze.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<PatchField, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.FIELD_DOES_NOT_EXIST);

		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty()),
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	private CustomizableFieldMetadataDto createCaseCustomField(String name, CustomizableFieldType type) {
		return createCustomFieldFor(name, type, CustomizableFieldContext.CASE);
	}

	private CustomizableFieldMetadataDto createCustomFieldFor(String name, CustomizableFieldType type, CustomizableFieldContext context) {
		CustomizableFieldMetadataFacade facade = getBean(CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal.class);

		CustomizableFieldMetadataDto dto = new CustomizableFieldMetadataDto();

		dto.setName(name);
		dto.setFieldType(type);
		dto.setContextClass(context);
		dto.setUiGroup(CustomizableFieldGroup.getGroupsForContext(context).stream().findFirst().orElseThrow());
		dto.setUiLinePosition(1);

		return facade.save(dto);
	}

	@Tag("customizable-fields")
	@Test
	void patch_twoExposures_normalAndCustomizableFields() {
		// PREPARE
		CustomizableFieldMetadataDto metadata = createExposureCustomField("cfExposureNote", CustomizableFieldType.TEXT);
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.DENGUE);
		String customFieldKey = "Custom." + ExposureDto.I18N_PREFIX + ".cfExposureNote";

		PatchDictionary patchDictionary = new PatchDictionary();
		// Exposure group 0 — normal + customizable
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE), 0), "WORK");
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION), 0), "office");
		patchDictionary.put(PatchField.of(customFieldKey, 0), "note for work");
		// Exposure group 1 — normal + customizable
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.EXPOSURE_TYPE), 1), "TRAVEL");
		patchDictionary.put(PatchField.of(toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.DESCRIPTION), 1), "market");
		patchDictionary.put(PatchField.of(customFieldKey, 1), "note for travel");

		// EXECUTE
		DataPatchResponse response = victim().patch(
			new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
				.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
				.setPatchDictionary(patchDictionary));

		// CHECK — normal fields
		CaseDataDto actualCase = getCaseFacade().getByUuid(caze.getUuid());
		List<ExposureDto> exposures = actualCase.getEpiData().getExposures();

		ExposureDto workExposure = exposures.stream().filter(e -> ExposureType.WORK.equals(e.getExposureType())).findFirst().orElseThrow();
		ExposureDto travelExposure = exposures.stream().filter(e -> ExposureType.TRAVEL.equals(e.getExposureType())).findFirst().orElseThrow();

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failures: " + response.getFailures()),
			() -> Assertions.assertTrue(response.isApplied()),
			() -> Assertions.assertEquals(2, exposures.size()),
			// Normal fields on each exposure
			() -> Assertions.assertEquals("office", workExposure.getDescription()),
			() -> Assertions.assertEquals("market", travelExposure.getDescription()),
			// Customizable field — each exposure carries its own value
			() -> Assertions.assertEquals("note for work", customizableValuesForExposure(workExposure.getUuid()).get(metadata).getValue()),
			() -> Assertions.assertEquals("note for travel", customizableValuesForExposure(travelExposure.getUuid()).get(metadata).getValue()));
	}

	private Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableValuesForCase(String caseUuid) {
		return customizableValuesFor(caseUuid, CustomizableFieldContext.CASE);
	}

	private Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableValuesForExposure(String exposureUuid) {
		return customizableValuesFor(exposureUuid, CustomizableFieldContext.EXPOSURE);
	}

	private Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableValuesFor(
		String exposureUuid,
		CustomizableFieldContext exposure) {
		return getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class).getValuesForEntity(exposureUuid, exposure);
	}

	private Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableValuesForEpiData(String exposureUuid) {
		return customizableValuesFor(exposureUuid, CustomizableFieldContext.EPIDATA);
	}

	private CustomizableFieldMetadataDto createExposureCustomField(String name, CustomizableFieldType type) {
		return createCustomFieldFor(name, type, CustomizableFieldContext.EXPOSURE);
	}
}
