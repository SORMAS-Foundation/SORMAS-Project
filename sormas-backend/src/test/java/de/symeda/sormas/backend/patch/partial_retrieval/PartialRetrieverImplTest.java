package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.patch.partial_retrieval.*;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
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

			() -> Assertions.assertEquals("COMIRNATY", vaccineNameFieldInfo.getTranslatedFieldValue()),
			() -> Assertions.assertEquals("actual vaccine name", otherVaccineNameFieldInfo.getTranslatedFieldValue()),

			() -> Assertions.assertEquals(3, actual.getFieldInfoDictionary().size()));
	}

	@Test
	void retrievePartialForDisplay() {
		// PREPARE
		I18nProperties.setUserLanguage(Language.FR);
		Disease disease = Disease.AFP;
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

			() -> Assertions.assertEquals("Disease", caseDiseaseFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals("Paralysie Flasque Aiguë", caseDiseaseFieldInfo.getTranslatedFieldValue()));
	}

	@Test
	void retrievePartial_german() {
		// PREPARE
		I18nProperties.setUserLanguage(Language.DE);

		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		// EXECUTE
		String clinicalConfirmation = toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.CLINICAL_CONFIRMATION);
		String symptomsAbdominalPain = toFieldName(SymptomsDto.I18N_PREFIX, SymptomsDto.ABDOMINAL_PAIN);
		PartialRetrievalResponse actual = victim().retrievePartial(
			new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid())
				.setFieldsToRetrieve(Set.of(clinicalConfirmation, symptomsAbdominalPain)));

		// CHECK
		FieldInfo caseDiseaseFieldInfo = actual.getFieldInfoDictionary().get(clinicalConfirmation);
		FieldInfo symptomsAbdominalPainFieldInfo = actual.getFieldInfoDictionary().get(symptomsAbdominalPain);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),

			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(clinicalConfirmation)),
			() -> Assertions.assertEquals("Klinische Bestätigung", caseDiseaseFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals(originalCase.getClinicalConfirmation(), caseDiseaseFieldInfo.getFieldValue()),

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
		FieldInfo classificationDateFieldInfo = actual.getFieldInfoDictionary().get(caseClassificationDateFieldName);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),
			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(caseClassificationDateFieldName)),
			() -> Assertions.assertEquals("Date of classification", classificationDateFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertNull(classificationDateFieldInfo.getFieldValue()));
	}

	@RepeatedTest(12)
	void retrieve_contact_details_phone() {
		// PREPARE
		Disease disease = Disease.PERTUSSIS;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		PersonReferenceDto personRef = originalCase.getPerson();

		PersonDto person = getPersonFacade().getByUuid(personRef.getUuid());
		List<PersonContactDetailDto> contactDetails = person.getPersonContactDetails();

		PersonContactDetailDto primaryPhoneNumber = new PersonContactDetailDto();
		primaryPhoneNumber.setContactInformation("09876543");
		primaryPhoneNumber.setPersonContactDetailType(PersonContactDetailType.PHONE);
		primaryPhoneNumber.setPrimaryContact(true);
		contactDetails.add(primaryPhoneNumber);

		PersonContactDetailDto secondaryPhoneNumber = new PersonContactDetailDto();
		secondaryPhoneNumber.setContactInformation("12345678");
		secondaryPhoneNumber.setPersonContactDetailType(PersonContactDetailType.PHONE);
		contactDetails.add(secondaryPhoneNumber);

		PersonContactDetailDto emptyPhone = new PersonContactDetailDto();
		emptyPhone.setContactInformation(" ");
		emptyPhone.setPersonContactDetailType(PersonContactDetailType.PHONE);
		contactDetails.add(emptyPhone);

		PersonContactDetailDto nullPhone = new PersonContactDetailDto();
		nullPhone.setContactInformation(null);
		nullPhone.setPersonContactDetailType(PersonContactDetailType.PHONE);
		contactDetails.add(nullPhone);

		getPersonFacade().save(person);

		// EXECUTE
		String personContactDetails = toFieldName(PersonContactDetailDto.I18N_PREFIX, PersonContactDetailDto.PHONE_NUMBER_TYPE);
		PartialRetrievalResponse actual = victim()
			.retrievePartial(new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid()).setFieldsToRetrieve(Set.of(personContactDetails)));

		// CHECK
		FieldInfo personFirstNameFieldInfo = actual.getFieldInfoDictionary().get(personContactDetails);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),
			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(personContactDetails)),
			() -> Assertions.assertEquals("Phone number type", personFirstNameFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals("09876543; 12345678", personFirstNameFieldInfo.getFieldValue()));
	}

	@Test
	void retrieve_contact_details_email() {
		// PREPARE
		Disease disease = Disease.RUBELLA;
		CaseDataDto originalCase = creator.createUnclassifiedCase(disease);

		PersonReferenceDto personRef = originalCase.getPerson();

		PersonDto person = getPersonFacade().getByUuid(personRef.getUuid());
		List<PersonContactDetailDto> contactDetails = person.getPersonContactDetails();

		PersonContactDetailDto emailContactDetail = new PersonContactDetailDto();
		emailContactDetail.setContactInformation("mail@mail.ch");
		emailContactDetail.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		contactDetails.add(emailContactDetail);

		PersonContactDetailDto phoneContactDetail = new PersonContactDetailDto();
		phoneContactDetail.setContactInformation("MUST_NOT_BE_RETRIEVED");
		phoneContactDetail.setPersonContactDetailType(PersonContactDetailType.PHONE);
		contactDetails.add(phoneContactDetail);

		getPersonFacade().save(person);

		// EXECUTE
		String personContactDetails = toFieldName(PersonContactDetailDto.I18N_PREFIX, PersonContactDetailDto.CONTACT_INFORMATION);
		PartialRetrievalResponse actual = victim()
			.retrievePartial(new PartialRetrievalRequest().setCaseUuid(originalCase.getUuid()).setFieldsToRetrieve(Set.of(personContactDetails)));

		// CHECK
		FieldInfo personFirstNameFieldInfo = actual.getFieldInfoDictionary().get(personContactDetails);
		Assertions.assertAll(
			() -> Assertions.assertTrue(actual.getFailuresDictionary().isEmpty()),
			() -> Assertions.assertTrue(actual.getFieldInfoDictionary().containsKey(personContactDetails)),
			() -> Assertions.assertEquals("Contact information", personFirstNameFieldInfo.getTranslatedFieldName()),
			() -> Assertions.assertEquals("mail@mail.ch", personFirstNameFieldInfo.getFieldValue()));
	}

	private static String toFieldName(String prefix, String fieldName) {
		return prefix + '.' + fieldName;
	}

	private PartialRetriever victim() {
		return getPartialRetriever();
	}
}
