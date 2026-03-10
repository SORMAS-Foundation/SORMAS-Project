package de.symeda.sormas.patch;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
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
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;

class DataPatcherImplTest extends AbstractBeanTest {

	// TODO: test different replacement strategy: with/without failure

	@Test
	void patch_noErrorsReplaceAlways() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		String newLastname = "toto";
		String newSequelaeDetails = "Some very interesting sequelaeDetails";
		String classificationDate = "2030-02-01";
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"Person.lastName",
					newLastname,

					"CaseData.classificationDate",
					classificationDate,

					"CaseData.sequelaeDetails",
					newSequelaeDetails));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		logger.info("response: [{}]", response);

		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),
			// PERSON
			() -> Assertions.assertEquals(newLastname, actualPerson.getLastName()),
			// CASE
			() -> Assertions.assertEquals(
				Date.from(LocalDate.parse(classificationDate).atStartOfDay(ZoneId.systemDefault()).toInstant()),
				actualCase.getClassificationDate()),
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
		logger.info("response: [{}]", response);

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
					"Person.personContactDetails.details",
					newEmail,

					"Person.personContactDetails.phoneNumberType",
					newPhoneNumber))
			.setOrigin(origin);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		logger.info("response: [{}]", response);

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
							&& newPhoneNumber.equals(contactDetail.getDetails())
							&& contactDetail.getPhoneNumberType() == PhoneNumberType.OTHER)),

			() -> Assertions.assertTrue(
				contactDetailsStreamProvider.get()
					.anyMatch(
						contactDetail -> contactDetail.getPersonContactDetailType() == PersonContactDetailType.EMAIL
							&& newEmail.equals(contactDetail.getDetails()))));
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
		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_PREFIX);

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
		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.FORBIDDEN_FIELD);

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
		logger.info("response: [{}]", response);

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
		Map<String, DataPatchFailure> expectedFailures =
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
		logger.info("response: [{}]", response);
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(expectedOccupationType, person.getOccupationType()),

			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()));
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
		logger.info("response: [{}]", response);
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(expectedOccupationType, person.getOccupationType()),
			() -> Assertions.assertEquals(input, person.getOccupationDetails()),

			() -> Assertions.assertEquals(
				Map.of("Person.occupationType", input, "Person.occupationDetails", input, "Person.additionalDetails", input),
				response.getValidPatchDictionary()));
	}

	private static @NotNull CustomizableEnumTranslation buildTranslation(String en, String irrelated) {
		CustomizableEnumTranslation e1 = new CustomizableEnumTranslation();
		e1.setLanguageCode(en);
		e1.setValue(irrelated);
		return e1;
	}

	@Test
	void patch_referenceData() {
		// PREPARE
		registerBeanForLookup(RegionFacade.class, getRegionFacade());

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		FacilityDto healthFacility = getFacilityFacade().getByUuid(originalCase.getHealthFacility().getUuid());
		originalCase.setDistrict(healthFacility.getDistrict());
		getCaseFacade().save(originalCase);

		// must be able to ignore accents - whitespaces - case
		Map<String, Object> patchDictionary = Map.of("CaseData.region", " régIoN    ");
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		logger.info("response: [{}]", response);
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()));
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
		logger.info("response: [{}]", response);
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
		Map<String, DataPatchFailure> expectedFailures =
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
		Map<String, DataPatchFailure> expectedFailures =
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
		Map<String, DataPatchFailure> expectedFailures =
			buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getValidPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	private static @NotNull Map<String, DataPatchFailure> buildDictionaryOfFailureType(
		Map<String, Object> patchDictionary,
		DataPatchFailureCause unsupportedFieldForDisease) {
		return patchDictionary.entrySet()
			.stream()
			.map(
				entry -> Map.entry(
					entry.getKey(),
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
		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(
			Map.of("CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue),
			DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.isApplied()),

			() -> Assertions.assertEquals(Map.of("CaseData.symptoms.cough", trueString), response.getValidPatchDictionary()),

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
		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(
			Map.of("CaseData.quarantineOrderedOfficialDocumentDate", ignoredValue),
			DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),

			() -> Assertions.assertEquals(Map.of("CaseData.symptoms.cough", trueString), response.getValidPatchDictionary()),

			() -> Assertions.assertNull(actual.getSymptoms().getCough()),

			() -> Assertions.assertNull(actual.getQuarantineOrderedOfficialDocumentDate()),

			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"NO",
		" nein ",
		" unknown " })
	void patch_addVaccine_unknown_or_no(String unknownOrNo) {
		// PREPARE
		Disease disease = Disease.RESPIRATORY_SYNCYTIAL_VIRUS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("Immunization.immunizationStatus", unknownOrNo);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.EN, Language.DE_CH));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		List<ImmunizationDto> immunizationDtos = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));

		ImmunizationDto createdImmunizationDto = immunizationDtos.get(0);

		// CHECK
		Assertions.assertAll(() -> Assertions.assertTrue(response.isApplied()),

// TODO: check what about patch dictionary for those values.
//			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(1, immunizationDtos.size()),

			() -> Assertions.assertEquals(ImmunizationStatus.NOT_ACQUIRED, createdImmunizationDto.getImmunizationStatus()),
			() -> Assertions.assertEquals(0, createdImmunizationDto.getVaccinations().size()),
			() -> Assertions.assertEquals(disease, createdImmunizationDto.getDisease()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportDate()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportingUser()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"Maternal vaccination" })
	void patch_addVaccine_true_and_mother_vaccine(String matternalVaccination) {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("Immunization.immunizationStatus", " ja ");
		patchDictionary.put("Immunization.meansOfImmunization", matternalVaccination);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(List.of(Language.DE, Language.EN));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		List<ImmunizationDto> immunizationDtos = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));

		ImmunizationDto matternalImmunizationDto = immunizationDtos.get(1);

		Assertions.assertAll(() -> Assertions.assertTrue(response.isApplied()),

// TODO: check what about patch dictionary for those values.
//			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(2, immunizationDtos.size()),

			() -> Assertions.assertEquals(ImmunizationStatus.ACQUIRED, matternalImmunizationDto.getImmunizationStatus()),
			() -> Assertions.assertEquals(MeansOfImmunization.MATERNAL_VACCINATION, matternalImmunizationDto.getMeansOfImmunization()),

			() -> Assertions.assertEquals(disease, matternalImmunizationDto.getDisease()),

			() -> Assertions.assertEquals(0, matternalImmunizationDto.getVaccinations().size()),

			() -> Assertions.assertNotNull(matternalImmunizationDto.getReportDate()),

			() -> Assertions.assertNotNull(matternalImmunizationDto.getReportingUser()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
	}

	@Test
	void patch_addVaccine_true() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		Map<String, Object> patchDictionary = new HashMap<>();
		patchDictionary.put("Immunization.immunizationStatus", " ja ");
		patchDictionary.put("Immunization.country", "France");
		patchDictionary.put("Vaccination.country", "France");

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(Collections.singletonList(Language.DE));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		// CHECK
		List<ImmunizationDto> immunizationDtos = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));

		ImmunizationDto createdImmunizationDto = immunizationDtos.get(0);

		List<VaccinationDto> vaccinations = createdImmunizationDto.getVaccinations();
		VaccinationDto vaccinationDto = vaccinations.get(0);

		Assertions.assertAll(() -> Assertions.assertTrue(response.isApplied()),

// TODO: check what about patch dictionary for those values.
//			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(1, immunizationDtos.size()),

			() -> Assertions.assertEquals(ImmunizationStatus.ACQUIRED, createdImmunizationDto.getImmunizationStatus()),
			() -> Assertions.assertEquals(MeansOfImmunization.VACCINATION, createdImmunizationDto.getMeansOfImmunization()),

			() -> Assertions.assertEquals(disease, createdImmunizationDto.getDisease()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportDate()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportingUser()),

			() -> Assertions.assertEquals(1, vaccinations.size()),

			() -> Assertions.assertEquals(Vaccine.OTHER, vaccinationDto.getVaccineName()),
			() -> Assertions.assertNull(vaccinationDto.getOtherVaccineName()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
	}

	@Test
	void patch_addVaccine_vaccine_name() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		getCaseFacade().save(originalCase);

		Assertions.assertFalse(originalCase.getSymptoms().getSymptomatic());

		Map<String, Object> patchDictionary = new HashMap<>();
		String vaccineName = "Beyfortus";
		patchDictionary.put("Immunization.immunizationStatus", vaccineName);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setPatchDictionary(patchDictionary)
			.setInputLanguages(Collections.singletonList(Language.DE));

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		List<ImmunizationDto> immunizationDtos = getImmunizationFacade().getByPersonUuids(List.of(originalCase.getPerson().getUuid()));

		ImmunizationDto createdImmunizationDto = immunizationDtos.get(0);

		// CHECK
		List<VaccinationDto> vaccinations = createdImmunizationDto.getVaccinations();
		VaccinationDto vaccinationDto = vaccinations.get(0);
		Assertions.assertAll(() -> Assertions.assertTrue(response.isApplied()),

// TODO: check what about patch dictionary for those values.
//			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()),

			() -> Assertions.assertEquals(1, immunizationDtos.size()),

			() -> Assertions.assertEquals(ImmunizationStatus.ACQUIRED, createdImmunizationDto.getImmunizationStatus()),
			() -> Assertions.assertEquals(MeansOfImmunization.VACCINATION, createdImmunizationDto.getMeansOfImmunization()),
			() -> Assertions.assertEquals(disease, createdImmunizationDto.getDisease()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportDate()),

			() -> Assertions.assertNotNull(createdImmunizationDto.getReportingUser()),

			() -> Assertions.assertEquals(1, vaccinations.size()),

			() -> Assertions.assertEquals(Vaccine.OTHER, vaccinationDto.getVaccineName()),
			() -> Assertions.assertEquals(vaccineName, vaccinationDto.getOtherVaccineName()),

			// FAILURES
			() -> Assertions.assertEquals(Map.of(), response.getFailures()));
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

			() -> Assertions.assertEquals(patchDictionary, response.getValidPatchDictionary()),

			() -> Assertions.assertNull(actual.getQuarantineChangeComment()),

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

		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.FIELD_DOES_NOT_EXIST);

		// CHECK
		Assertions.assertAll(
			() -> Assertions.assertFalse(response.isApplied()),

			() -> Assertions.assertEquals(expectedFailures, response.getFailures()),

			() -> Assertions.assertEquals(Map.of(), response.getValidPatchDictionary()));
	}

	private DataPatcher victim() {
		return getCaseDataPatcher();
	}
}
