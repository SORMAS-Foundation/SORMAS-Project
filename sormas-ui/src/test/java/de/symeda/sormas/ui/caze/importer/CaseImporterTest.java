package de.symeda.sormas.ui.caze.importer;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.TestDataCreator.RDCF;
import de.symeda.sormas.ui.importer.CaseImportSimilarityInput;
import de.symeda.sormas.ui.importer.CaseImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseImporterTest extends AbstractBeanTest {

	@Test
	public void testImportAllCases() throws IOException, InvalidColumnException, InterruptedException {

		TestDataCreator creator = new TestDataCreator();

		RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_success.csv").getFile());
		CaseImporter caseImporter = new CaseImporterExtension(csvFile, true, user.toReference());
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));

		// Failed import of 5 cases because of errors
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_errors.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference());
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(5, getCaseFacade().count(null));

		// Failed import
		boolean exceptionWasThrown = false;

		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_failure.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference());
		try {
			importResult = caseImporter.runImport();
		} catch (InvalidColumnException e) {
			exceptionWasThrown = true;
		}
		assertTrue(exceptionWasThrown);
		assertEquals(5, getCaseFacade().count(null));

		// Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: pick
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(
					new CaseImportSimilarityResult(
						getPersonFacade().getSimilarPersonsByUuids(Collections.singletonList(getPersonFacade().getAllUuids().get(0))).get(0),
						null,
						ImportSimilarityResultOption.PICK));
			}

			@Override
			protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, input.getSimilarCases().get(0), ImportSimilarityResultOption.PICK));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: cancel
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CANCEL));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: override
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(
					new CaseImportSimilarityResult(
							getPersonFacade().getSimilarPersonsByUuids(Collections.singletonList(getPersonFacade().getAllUuids().get(0))).get(0),
						null,
						ImportSimilarityResultOption.PICK));
			}

			@Override
			protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, input.getSimilarCases().get(0), ImportSimilarityResultOption.OVERRIDE));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: create -> fail because of duplicate epid number
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(
					new CaseImportSimilarityResult(
							getPersonFacade().getSimilarPersonsByUuids(Collections.singletonList(getPersonFacade().getAllUuids().get(0))).get(0),
						null,
						ImportSimilarityResultOption.PICK));
			}

			@Override
			protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Change epid number of the case in database to pass creation test
		CaseDataDto caze = getCaseFacade().getAllActiveCasesAfter(null).get(0);
		caze.setEpidNumber("ABC-DEF-GHI-19-99");
		getCaseFacade().saveCase(caze);
		assertEquals("ABC-DEF-GHI-19-99", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: create -> pass
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference()) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}

			@Override
			protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Successful import of a case with different infrastructure combinations
		creator.createRDCF("R1", "D1", "C1", "F1");
		creator.createRDCF("R2", "D2", "C2", "F2");
		creator.createRDCF("R3", "D3", "C3", "F3");
		creator.createRDCF("R4", "D4", "C4", "F4");

		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_different_infrastructure.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, true, user.toReference());
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(7, getCaseFacade().count(null));
	}

	@Test
	public void testLineListingImport() throws IOException, InvalidColumnException, InterruptedException {
		RDCF rdcf = new TestDataCreator().createRDCF("Abia", "Bende", "Bende Ward", "Bende Maternity Home");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_line_listing.csv").getFile());
		CaseImporter caseImporter = new CaseImporterExtension(csvFile, false, user.toReference());
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));
	}

	private static class CaseImporterExtension extends CaseImporter {

		private CaseImporterExtension(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser) {
			super(inputFile, hasEntityClassRow, currentUser);
		}

		protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
		}

		protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
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
