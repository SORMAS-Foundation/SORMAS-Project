package de.symeda.sormas.ui.contact.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.importer.ContactImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

public class ContactImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportCaseContacts()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		ContactFacadeEjb contactFacade = getBean(ContactFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto casePerson = creator.createPerson("John", "Smith");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			casePerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.CONFIRMED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// Successful import of 5 case contacts
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_success.csv").toURI());
		ContactImporter contactImporter = new ContactImporterExtension(csvFile, user, caze);
		ImportResultStatus importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, contactFacade.count(null));

		// Person Similarity: pick
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(new PersonSimilarityCriteria());
		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, caze) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {

				List<SimilarPersonDto> entries = new ArrayList<>();
				for (SimilarPersonDto person : persons) {
					if (PersonHelper
						.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
						entries.add(person);
					}
				}
				resultConsumer.accept((T) new ContactImportSimilarityResult(entries.get(0), null, ImportSimilarityResultOption.PICK));
			}
		};
		importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, contactFacade.count(null));
		assertEquals(6, getPersonFacade().getAllUuids().size());

		// Person Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, caze) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {
				resultConsumer.accept((T) new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, contactFacade.count(null));
		assertEquals(6, getPersonFacade().getAllUuids().size());

		// Person Similarity: create
		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, caze);
		importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(7, contactFacade.count(null));
		assertEquals(7, getPersonFacade().getAllUuids().size());

		// Test import contacts from a commented CSV file
		// Successful import of 5 case contacts
		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_comment_success.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, caze);
		importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(12, contactFacade.count(null));
	}

	@Test
	public void testImportContacts() throws IOException, InvalidColumnException, InterruptedException, CsvException, URISyntaxException {

		ContactFacadeEjb contactFacade = getBean(ContactFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// Try to import 3 contacts. 
		// 2 of them belong to a case that does not exist.
		// 1 of those 2 still has enough details to be imported
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_contact_import_test.csv").toURI());
		ContactImporter contactImporter = new ContactImporterExtension(csvFile, user, null);
		ImportResultStatus importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(2, contactFacade.count(null));

		PersonDto casePerson = creator.createPerson("John", "Smith");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			casePerson.toReference(),
			Disease.EVD,
			CaseClassification.CONFIRMED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			createdCase -> createdCase.setUuid("ABCDEF-GHIJKL-MNOPQR"));

		csvFile = new File(getClass().getClassLoader().getResource("sormas_contact_import_test.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, null);
		importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, contactFacade.count(null));

		// 2 associated to the case
		ContactCriteria contactCriteria = new ContactCriteria().caze(caze.toReference());
		assertEquals(2, contactFacade.count(contactCriteria));
		// should have the disease of the case
		contactCriteria = new ContactCriteria().disease(Disease.EVD);
		assertEquals(2, contactFacade.count(contactCriteria));

		// the others have their defined disease
		contactCriteria = new ContactCriteria().disease(Disease.CORONAVIRUS);
		assertEquals(3, contactFacade.count(contactCriteria));

		// Test import contacts from a commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_contact_import_test_comment.csv").toURI());
		contactImporter = new ContactImporterExtension(csvFile, user, null);
		importResult = contactImporter.runImport();

		InputStream errorStream =
			new ByteArrayInputStream(((ContactImporterExtension) contactImporter).stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
		List<String[]> errorRows = CSVUtils.createBomCsvReader(errorStream).readAll();
		if (errorRows.size() > 2) {
			assertThat("Error during import: " + StringUtils.join(errorRows.get(2), ", "), errorRows, hasSize(0));
		}

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(8, contactFacade.count(null));
	}

	@Test
	public void testImportCaseContactsDifferentAddressTypes()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		ContactFacadeEjb contactFacade = getBean(ContactFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto casePerson = creator.createPerson("John", "Smith");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			casePerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.CONFIRMED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// import of 3 contacts with different address types
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_address_types.csv").toURI());
		ContactImporter contactImporter = new ContactImporterExtension(csvFile, user, caze);
		ImportResultStatus importResult = contactImporter.runImport();

		List<ContactDto> contacts = getContactFacade().getAllAfter(null);

		assertEquals(3, contacts.size());

		boolean foundOtto = false;
		boolean foundOskar = false;
		boolean foundOona = false;

		for (ContactDto contact : contacts) {
			PersonDto person = getPersonFacade().getByUuid(contact.getPerson().getUuid());
			if ("Otto".equals(person.getFirstName())) {
				foundOtto = true;
				assertTrue(CollectionUtils.isEmpty(person.getAddresses()));
				assertEquals("131", person.getAddress().getHouseNumber());
			}
			if ("Oskar".equals(person.getFirstName())) {
				foundOskar = true;
				assertTrue(CollectionUtils.isEmpty(person.getAddresses()));
				assertEquals("132", person.getAddress().getHouseNumber());
			}
			if ("Oona".equals(person.getFirstName())) {
				foundOona = true;
				assertTrue(LocationHelper.checkIsEmptyLocation(person.getAddress()));
				assertEquals(1, person.getAddresses().size());
				assertEquals("133", person.getAddresses().get(0).getHouseNumber());
			}
		}
		assertTrue(foundOtto && foundOskar && foundOona, "Not all contacts found.");
	}

	@Test
	@Disabled("Remove ignore once we have replaced H2, and feature properties can be changed by code")
	public void testImportContactsWithVaccinations()
		throws IOException, InterruptedException, CsvValidationException, InvalidColumnException, URISyntaxException {
		var rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_contact_import_test_vaccinations.csv").toURI());
		ContactImporterExtension contactImporter = new ContactImporterExtension(csvFile, user, null);
		ImportResultStatus importResult = contactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, contactImporter.stringBuilder.toString());

		List<ContactDto> contacts = getContactFacade().getAllAfter(null);
		assertEquals(3, contacts.size());

		ContactDto contact1 = contacts.stream().filter(c -> c.getCaseIdExternalSystem().equals("case1")).findFirst().get();
		ContactDto contact2 = contacts.stream().filter(c -> c.getCaseIdExternalSystem().equals("case2")).findFirst().get();
		ContactDto contact3 = contacts.stream().filter(c -> c.getCaseIdExternalSystem().equals("case3")).findFirst().get();

		List<VaccinationDto> case1Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(contact1.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(0, case1Vaccinations.size());

		List<VaccinationDto> case2Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(contact2.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(1, case2Vaccinations.size());
		assertEquals(Vaccine.COMIRNATY, case2Vaccinations.get(0).getVaccineName());
		assertNull(case2Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());

		List<VaccinationDto> case3Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(contact3.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(2, case3Vaccinations.size());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(0).getVaccineName());
		assertEquals(YesNoUnknown.YES, case3Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(1).getVaccineName());
		assertNull(case3Vaccinations.get(1).getHealthConditions().getChronicPulmonaryDisease());
	}

	public static class ContactImporterExtension extends ContactImporter {

		public StringBuilder stringBuilder = new StringBuilder();
		private StringBuilderWriter writer = new StringBuilderWriter(stringBuilder);

		public ContactImporterExtension(File inputFile, UserDto currentUser, CaseDataDto caze) throws IOException {
			super(inputFile, currentUser, caze, ValueSeparator.DEFAULT);
		}

		public ContactImporterExtension(File inputFile, UserDto currentUser) throws IOException {
			super(inputFile, currentUser, null, ValueSeparator.DEFAULT);
		}

		@Override
		protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
			PersonDto newPerson,
			Consumer<T> resultConsumer,
			BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
			String infoText,
			UI currentUI) {
			resultConsumer.accept((T) new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
		}

		@Override
		protected void handleContactSimilarity(
			ContactDto newContact,
			PersonReferenceDto newPerson,
			Consumer<ContactImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
		}

		@Override
		protected Writer createErrorReportWriter() {
			return writer;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
