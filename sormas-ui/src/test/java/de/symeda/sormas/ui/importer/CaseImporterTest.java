package de.symeda.sormas.ui.importer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.util.CurrentInstance;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.login.LoginHelper;

@RunWith(MockitoJUnitRunner.class)
public class CaseImporterTest extends AbstractBeanTest {

	@Mock
	private VaadinServletRequest request;

	@Before
	public void initUI() throws Exception {
		creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);

		when(request.getUserPrincipal()).thenReturn(new Principal() {
			@Override
			public String getName() {
				return "admin";
			}
		});
		
		CurrentInstance.setInheritable(VaadinRequest.class, request);

		// TODO init UI
	}

	@Test
	public void testImportAllCases() throws IOException, InvalidColumnException, InterruptedException {
		UserReferenceDto user = LoginHelper.getCurrentUserAsReference();
		
		new TestDataCreator().createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_success.csv").getFile());
		CaseImporter caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		ImportResultStatus importResult = caseImporter.importAllCases((input, resultCallback) -> {
			
		}, (result) -> {
			
		});
		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
		assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());

		// Successful import of 4 cases, failed import of 1 case
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_errors.csv").getFile());
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		importResult = caseImporter.importAllCases((input, resultCallback) -> {
			
		}, (result) -> {
			
		});
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(5, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
		assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());

		// Failed import
		boolean exceptionWasThrown = false;

		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_failure.csv").getFile());
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		try {
			importResult = caseImporter.importAllCases((input, resultCallback) -> {
				
			}, (result) -> {
				
			});
		} catch (InvalidColumnException e) {
			exceptionWasThrown = true;
		}
		assertEquals(true, exceptionWasThrown);
		assertEquals(5, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
		assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());
		
		// Similarity: Merge person
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		importResult = caseImporter.importAllCases((input, resultCallback) -> {
			PersonIndexDto matchingPerson = FacadeProvider.getPersonFacade().getIndexDto(
					FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).get(0).getUuid());
			resultCallback.accept(new ImportSimilarityResult(matchingPerson, null, true, false, false, false));			
		}, (result) -> {
			assertEquals(CaseImportResult.SUCCESS, result);
			assertEquals(6, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
			assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());
		});
		
		// Similarity: Merge case
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		// TODO PROBLEM!
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		importResult = caseImporter.importAllCases((input, resultCallback) -> {
			PersonIndexDto matchingPerson = FacadeProvider.getPersonFacade().getIndexDto(
					FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).get(0).getUuid());
			CaseDataDto matchingCase = FacadeProvider.getCaseFacade().getLatestCaseByPerson(matchingPerson.getUuid(), user.getUuid());
			resultCallback.accept(new ImportSimilarityResult(matchingPerson, matchingCase, true, true, false, false));			
		}, (result) -> {
			assertEquals(CaseImportResult.SUCCESS, result);
			assertEquals(6, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
			assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());
		});
		
		// Similarity: Skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		importResult = caseImporter.importAllCases((input, resultCallback) -> {
			resultCallback.accept(new ImportSimilarityResult(null, null, false, false, true, false));			
		}, (result) -> {
			assertEquals(CaseImportResult.SKIPPED, result);
			assertEquals(6, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
			assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());
		});
		
		// Similarity: Cancel import
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").getFile());
		caseImporter = new CaseImporter(new FileReader(csvFile.getPath()), createPseudoOutputStream(), user);
		importResult = caseImporter.importAllCases((input, resultCallback) -> {
			resultCallback.accept(new ImportSimilarityResult(null, null, false, false, false, true));			
		}, (result) -> {
			assertEquals(6, FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid()).size());
			assertEquals(5, FacadeProvider.getPersonFacade().getPersonsAfter(null, user.getUuid()).size());
		});
	}
	
	private OutputStreamWriter createPseudoOutputStream() {
		return new OutputStreamWriter(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// Do nothing
			}
		});
	}

}
