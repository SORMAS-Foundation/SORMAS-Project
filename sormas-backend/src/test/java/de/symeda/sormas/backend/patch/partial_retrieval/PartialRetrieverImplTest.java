package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalRequest;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalResponse;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetriever;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;

class PartialRetrieverImplTest extends AbstractBeanTest {

	@Test
	void retrievePartialForDisplay_immunization_and_vaccine() {
		// PREPARE
		Disease disease = Disease.RESPIRATORY_SYNCYTIAL_VIRUS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);
		UserReferenceDto reportingUser = originalCase.getReportingUser();

		ImmunizationDto immunizationDto = ImmunizationDto.build(originalCase.getPerson());
		immunizationDto.setRelatedCase(originalCase.toReference());
		immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
		immunizationDto.setReportingUser(reportingUser);
		VaccinationDto vaccination = VaccinationDto.build(reportingUser);
		vaccination.setVaccineName(Vaccine.COMIRNATY);
		vaccination.setOtherVaccineName("actual vaccine name");
		immunizationDto.setVaccinations(List.of(vaccination));
		getImmunizationFacade().save(immunizationDto);

		String immunizationStatusFieldName = toFieldName(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_STATUS);
		String vaccineCombinedFieldName =
			toFieldName(VaccinationDto.I18N_PREFIX, String.format("(%s|%s)", VaccinationDto.VACCINE_NAME, VaccinationDto.OTHER_VACCINE_NAME));

		// EXECUTE
		DisplayablePartialRetrievalResponse actual = victim().retrievePartialForDisplay(
			new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid())
				.setFieldsToRetrieve(Set.of(immunizationStatusFieldName, vaccineCombinedFieldName)));

		// CHECK
		DisplayableFieldInfo immunizationStatusFieldInfo = actual.getFieldInfoDictionary().get(immunizationStatusFieldName);
		DisplayableFieldInfo vaccineNameFieldInfo = actual.getFieldInfoDictionary().get("Vaccination.vaccineName");
		DisplayableFieldInfo otherVaccineNameFieldInfo = actual.getFieldInfoDictionary().get("Vaccination.otherVaccineName");
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDescriptions().isEmpty()),

			() -> Assertions.assertNotNull(immunizationStatusFieldInfo),

			() -> Assertions.assertEquals("Immunization status", immunizationStatusFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals("Acquired", immunizationStatusFieldInfo.getTranslatedFieldValue()),

			() -> Assertions.assertEquals("Pfizer-BioNTech COVID-19 vaccine", vaccineNameFieldInfo.getTranslatedFieldValue()),
			() -> Assertions.assertEquals("actual vaccine name", otherVaccineNameFieldInfo.getTranslatedFieldValue()),

			() -> Assertions.assertEquals(3, actual.getFieldInfoDictionary().size()));
	}

	@Test
	void retrievePartialForDisplay() {
		// PREPARE
		I18nProperties.setUserLanguage(Language.DE);
		Disease disease = Disease.ANTHRAX;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		String caseDiseaseFieldName = toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE);

		// EXECUTE
		DisplayablePartialRetrievalResponse actual = victim().retrievePartialForDisplay(
			new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid()).setFieldsToRetrieve(Set.of(caseDiseaseFieldName)));

		// CHECK
		DisplayableFieldInfo caseDiseaseFieldInfo = actual.getFieldInfoDictionary().get(caseDiseaseFieldName);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDescriptions().isEmpty()),

			() -> Assertions.assertNotNull(caseDiseaseFieldInfo),

			() -> Assertions.assertEquals("Krankheit", caseDiseaseFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals("Milzbrand", caseDiseaseFieldInfo.getTranslatedFieldValue()));
	}

	@Test
	void retrievePartial_german() {
		// PREPARE
		I18nProperties.setUserLanguage(Language.DE);

		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		String caseDiseaseFieldName = toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE);
		String symptomsAbdominalPain = toFieldName(SymptomsDto.I18N_PREFIX, SymptomsDto.ABDOMINAL_PAIN);
		PartialRetrievalResponse actual = victim().retrievePartial(
			new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid())
				.setFieldsToRetrieve(Set.of(caseDiseaseFieldName, symptomsAbdominalPain)));

		// CHECK
		System.out.println("actual = " + actual);

		FieldInfo caseDiseaseFieldInfo = actual.getFieldInfoDictionary().get(caseDiseaseFieldName);
		FieldInfo symptomsAbdominalPainFieldInfo = actual.getFieldInfoDictionary().get(symptomsAbdominalPain);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),

			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(caseDiseaseFieldName)),
			() -> Assertions.assertEquals("Krankheit", caseDiseaseFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals(disease, caseDiseaseFieldInfo.getFieldValue()),

			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(symptomsAbdominalPain)),
			() -> Assertions.assertEquals("Abdominalschmerzen", symptomsAbdominalPainFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals(originalCase.getSymptoms().getAbdominalPain(), symptomsAbdominalPainFieldInfo.getFieldValue()));
	}

	@Test
	void retrievePartial_person() {
		// PREPARE
		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		PersonReferenceDto personRef = originalCase.getPerson();

		PersonDto person = getPersonFacade().getByUuid(personRef.getUuid());

		// EXECUTE
		String personFirstNameFieldName = toFieldName(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME);
		PartialRetrievalResponse actual = victim()
			.retrievePartial(new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid()).setFieldsToRetrieve(Set.of(personFirstNameFieldName)));

		// CHECK
		System.out.println("actual = " + actual);

		FieldInfo personFirstNameFieldInfo = actual.getFieldInfoDictionary().get(personFirstNameFieldName);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),
			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(personFirstNameFieldName)),
			() -> Assertions.assertEquals("First name", personFirstNameFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals(person.getFirstName(), personFirstNameFieldInfo.getFieldValue()));
	}

	@Test
	void retrievePartial_null_value() {
		// PREPARE
		Disease disease = Disease.DENGUE;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		String caseClassificationDateFieldName = toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.CLASSIFICATION_DATE);
		PartialRetrievalResponse actual = victim().retrievePartial(
			new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid()).setFieldsToRetrieve(Set.of(caseClassificationDateFieldName)));

		// CHECK
		System.out.println("actual = " + actual);

		FieldInfo classificationDateFieldInfo = actual.getFieldInfoDictionary().get(caseClassificationDateFieldName);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),
			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(caseClassificationDateFieldName)),
			() -> Assertions.assertEquals("Date of classification", classificationDateFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertNull(classificationDateFieldInfo.getFieldValue()));
	}

	private static String toFieldName(String prefix, String fieldName) {
		return prefix + '.' + fieldName;
	}

	private PartialRetriever victim() {
		return getPartialRetriever();
	}
}
