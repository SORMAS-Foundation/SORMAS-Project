package de.symeda.sormas.patch;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.CaseDataPatcher;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;

class CaseDataPatcherImplTest extends AbstractBeanTest {

	@BeforeEach
	void setUp() {
		// TODO: create reference data: country.
		// TODO: try to change language to use the languages names / with without accent and stuff: Pérou
	}

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

	@Test
	void patch_invalidPrefix() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);

		String ignoredValue = "ignoredValue";

		Map<String, Object> patchDictionary = Map.of(
			"ActivityAsCase.reportingUser",
			ignoredValue,
			"PreviousHospitalization.region",
			ignoredValue,
			"Symptoms.bedridden",
			ignoredValue,
			"EpiData.exposureDetailsKnown",
			ignoredValue

		);
		// EXECUTE
		DataPatchResponse response =
			victim().patch(new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid()).setPatchDictionary(patchDictionary));

		// CHECK
		Map<String, DataPatchFailure> expectedFailures = buildDictionaryOfFailureType(patchDictionary, DataPatchFailureCause.UNSUPPORTED_PREFIX);

		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
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
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
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
			.setPatchDictionary(Map.of("Person.sex", femaleValue));

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
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	// TODO: This one fails even though it should not
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

		CustomizableEnumValue customizableEnumValue = getCustomizableEnumValueService().getAll()
			.stream()
			.filter(enumMember -> healthcareWorker.equals(enumMember.getValue()))
			.findAny()
			.orElseThrow();

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		FacilityDto healthFacility = getFacilityFacade().getByUuid(originalCase.getHealthFacility().getUuid());
		originalCase.setDistrict(healthFacility.getDistrict());
		getCaseFacade().save(originalCase);

		// must be able to ignore accents - whitespaces - case
		Map<String, Object> patchDictionary = Map.of("Person.occupationType", "Im Gesundheitswesen tätig");
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary);

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

			() -> Assertions.assertEquals(patchDictionary, response.getPatchDictionary()));
	}

	// TODO: this should work
	@Test
	void patch_customizableEnu_non_default_enum() {
		// PREPARE
		CustomizableEnumValue entry = new CustomizableEnumValue();
		entry.setDataType(CustomizableEnumType.OCCUPATION_TYPE);
		String customValue = "A custom value";

		String translation = "expectedTranslation";
		entry.setValue(customValue);
		entry.setCaption("");
		entry.setTranslations(
			List.of(
				buildTranslation("en", translation),
				buildTranslation("fr", "irrelated"),
				buildTranslation("de", "irrelated"),
				buildTranslation("lu", "irrelated")));
		entry.setDefaultValue(false);
		getCustomizableEnumValueService().ensurePersisted(entry);

		getCustomizableEnumFacade().loadData();

		OccupationType expectedOccupationType = getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, customValue);

		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		FacilityDto healthFacility = getFacilityFacade().getByUuid(originalCase.getHealthFacility().getUuid());
		originalCase.setDistrict(healthFacility.getDistrict());
		getCaseFacade().save(originalCase);

		// must be able to ignore accents - whitespaces - case
		Map<String, Object> patchDictionary = Map.of("Person.occupationType", "     " + customValue.toUpperCase() + "   ");
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(patchDictionary);

		Mockito.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, customValue))
			.thenReturn(expectedOccupationType);

		// EXECUTE
		DataPatchResponse response = victim().patch(request);

		PersonDto person = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		// CHECK
		logger.info("response: [{}]", response);
		Assertions.assertAll(
			() -> Assertions.assertTrue(response.getFailures().isEmpty(), "Failure found, but should be empty"),

			() -> Assertions.assertEquals(expectedOccupationType, person.getOccupationType()),

			() -> Assertions.assertEquals(patchDictionary, response.getPatchDictionary()));
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

			() -> Assertions.assertEquals(patchDictionary, response.getPatchDictionary()));
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
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
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
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
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
			() -> Assertions.assertTrue(response.getPatchDictionary().isEmpty(), "Nothing should have been patched, should be empty"),
			// FAILURES
			() -> Assertions.assertEquals(expectedFailures, response.getFailures()));
	}

	private static @NotNull Map<String, DataPatchFailure> buildDictionaryOfFailureType(
		Map<String, Object> patchDictionary,
		DataPatchFailureCause unsupportedFieldForDisease) {
		Map<String, DataPatchFailure> expectedFailures = patchDictionary.entrySet()
			.stream()
			.map(
				entry -> Map.entry(
					entry.getKey(),
					new DataPatchFailure().setDataPatchFailureCause(unsupportedFieldForDisease).setProvidedFieldValue(entry.getValue())))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return expectedFailures;
	}

	@Test
	void patch_addVaccine() {
		throw new IllegalStateException("toImplement");
	}

	private CaseDataPatcher victim() {
		return getCaseDataPatcher();
	}
}
