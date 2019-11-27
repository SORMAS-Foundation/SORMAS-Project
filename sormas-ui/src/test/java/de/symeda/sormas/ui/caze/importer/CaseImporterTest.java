package de.symeda.sormas.ui.caze.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.TestDataCreator.RDCF;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityInput;
import de.symeda.sormas.ui.importer.ImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;

@RunWith(MockitoJUnitRunner.class)
public class CaseImporterTest extends AbstractBeanTest {

	@Test
	public void testImportAllCases() throws IOException, InvalidColumnException, InterruptedException {

		RDCF rdcf = new TestDataCreator().createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid()
				,"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_success.csv").getFile());
		CaseImporter caseImporter = new CaseImporterExtension(csvFile, user.toReference());
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));

		// Failed import of 5 cases because of errors
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_errors.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference());
		importResult = caseImporter.runImport();
			
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));

		// Failed import
		boolean exceptionWasThrown = false;

		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_failure.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference());
		try {
			importResult = caseImporter.runImport();
		} catch (InvalidColumnException e) {
			exceptionWasThrown = true;
		}
		assertEquals(true, exceptionWasThrown);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		
		// Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference()) {
			@Override
			protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		assertEquals("ABC-DEF-GHI-19-5", FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).get(0).getEpidNumber());
		
		// Similarity: pick
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference()) {
			@Override
			protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ImportSimilarityResult(input.getSimilarCases().get(0), ImportSimilarityResultOption.PICK));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		assertEquals("ABC-DEF-GHI-19-5", FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).get(0).getEpidNumber());
		
		// Similarity: cancel
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference()) {
			@Override
			protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.CANCEL));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		assertEquals("ABC-DEF-GHI-19-5", FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).get(0).getEpidNumber());
		
		// Similarity: override
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference()) {
			@Override
			protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ImportSimilarityResult(input.getSimilarCases().get(0), ImportSimilarityResultOption.OVERRIDE));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		assertEquals("ABC-DEF-GHI-19-10", FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).get(0).getEpidNumber());
		
		// Similarity: create
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporterExtension(csvFile, user.toReference()) {
			@Override
			protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, FacadeProvider.getCaseFacade().count(null, user.getUuid()));
		assertEquals("ABC-DEF-GHI-19-10", FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).get(0).getEpidNumber());	
	}

	private static class CaseImporterExtension extends CaseImporter {
		private CaseImporterExtension(File inputFile, UserReferenceDto currentUser) {
			super(inputFile, currentUser);
		}
	
		protected void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
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
