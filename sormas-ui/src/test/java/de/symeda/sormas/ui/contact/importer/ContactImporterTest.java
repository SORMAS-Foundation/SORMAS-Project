package de.symeda.sormas.ui.contact.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator.RDCF;
import de.symeda.sormas.ui.importer.ContactImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;

public class ContactImporterTest extends AbstractBeanTest {

	@Test
	public void testImportCaseContacts() throws IOException, InvalidColumnException, InterruptedException {

		ContactFacadeEjb contactFacade = getBean(ContactFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("John", "Smith");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), Disease.CORONAVIRUS,
				CaseClassification.CONFIRMED, InvestigationStatus.PENDING, new Date(), rdcf);

		// Successful import of 5 case contacts
		File csvFile = new File(
				getClass().getClassLoader().getResource("sormas_case_contact_import_test_success.csv").getFile());
		ContactImporter caseContactImporter = new CaseContactImporterExtension(csvFile, false, user.toReference(), caze);
		ImportResultStatus importResult = caseContactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, contactFacade.count(null, null));

		// Person Similarity: pick
		List<PersonNameDto> persons = FacadeProvider.getPersonFacade().getMatchingNameDtos(user.toReference(), new PersonSimilarityCriteria());
		csvFile = new File(
				getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").getFile());
		caseContactImporter = new CaseContactImporterExtension(csvFile, false, user.toReference(), caze) {
			@Override
			protected void handleSimilarity(PersonDto newPerson,
					Consumer<ContactImportSimilarityResult> resultConsumer) {

				List<PersonIndexDto> entries = new ArrayList<>();
				for (PersonNameDto person : persons) {
					if (PersonHelper.areNamesSimilar(newPerson.getFirstName() + " " + newPerson.getLastName(),
							person.getFirstName() + " " + person.getLastName())) {
						PersonIndexDto indexDto = FacadeProvider.getPersonFacade().getIndexDto(person.getUuid());
						entries.add(indexDto);
					}
				}
				resultConsumer.accept(new ContactImportSimilarityResult(
						entries.get(0),
						ImportSimilarityResultOption.PICK));
			}
		};
		importResult = caseContactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, contactFacade.count(null, null));
		assertEquals(6, getPersonFacade().getAllUuids(user.getUuid()).size());

		// Person Similarity: skip
		csvFile = new File(
				getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").getFile());
		caseContactImporter = new CaseContactImporterExtension(csvFile, false, user.toReference(), caze) {
			@Override
			protected void handleSimilarity(PersonDto newPerson,
					Consumer<ContactImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ContactImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = caseContactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, contactFacade.count(null, null));
		assertEquals(6, getPersonFacade().getAllUuids(user.getUuid()).size());

		// Person Similarity: create
		csvFile = new File(
				getClass().getClassLoader().getResource("sormas_case_contact_import_test_similarities.csv").getFile());
		caseContactImporter = new CaseContactImporterExtension(csvFile, false, user.toReference(), caze) {
			@Override
			protected void handleSimilarity(PersonDto newPerson,
					Consumer<ContactImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ContactImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = caseContactImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(7, contactFacade.count(null, null));
		assertEquals(7, getPersonFacade().getAllUuids(user.getUuid()).size());
	}

	private static class CaseContactImporterExtension extends ContactImporter {
		private CaseContactImporterExtension(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser,
				CaseDataDto caze) {
			super(inputFile, hasEntityClassRow, currentUser, caze);
		}

		protected void handleSimilarity(PersonDto newPerson, Consumer<ContactImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new ContactImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					// Do nothing
				}
			});
		}
	}
}
