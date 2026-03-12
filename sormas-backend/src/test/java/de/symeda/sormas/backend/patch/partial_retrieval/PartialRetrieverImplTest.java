package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalRequest;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalResponse;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetriever;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.backend.AbstractBeanTest;

class PartialRetrieverImplTest extends AbstractBeanTest {

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
			() -> Assertions.assertEquals("Krankheit", symptomsAbdominalPainFieldInfo.getTranslatedFieldName()),
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
