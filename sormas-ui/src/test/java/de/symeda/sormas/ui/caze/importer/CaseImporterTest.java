package de.symeda.sormas.ui.caze.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.importer.CaseImportSimilarityInput;
import de.symeda.sormas.ui.importer.CaseImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

@RunWith(MockitoJUnitRunner.class)
public class CaseImporterTest extends AbstractBeanTest {

	@Test
	public void testImportAllCases() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		TestDataCreator creator = new TestDataCreator();

		TestDataCreator.RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_success.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(caseImporter.stringBuilder.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));

		// Failed import of 5 cases because of errors
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_errors.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importResult);
		assertEquals(5, getCaseFacade().count(null));

		// Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {
				resultConsumer.accept((T) new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: pick
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(user.toReference(), new PersonSimilarityCriteria());
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user) {

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
				resultConsumer.accept((T) new CaseImportSimilarityResult(entries.get(0), null, ImportSimilarityResultOption.PICK));
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
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {
				resultConsumer.accept((T) new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CANCEL));
			}
		};
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED, importResult);
		assertEquals(5, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Similarity: override
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user) {

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
				resultConsumer.accept((T) new CaseImportSimilarityResult(entries.get(0), null, ImportSimilarityResultOption.PICK));
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
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user) {

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
				resultConsumer.accept((T) new CaseImportSimilarityResult(entries.get(0), null, ImportSimilarityResultOption.PICK));
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
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllActiveCasesAfter(null).get(0).getEpidNumber());

		// Successful import of a case with different infrastructure combinations
		creator.createRDCF("R1", "D1", "C1", "F1");
		creator.createRDCF("R2", "D2", "C2", "F2");
		creator.createRDCF("R3", "D3", "C3", "F3");

		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_different_infrastructure.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(7, getCaseFacade().count(null));

		// Successful import of 5 cases from a commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_comment_success.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(caseImporter.stringBuilder.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(12, getCaseFacade().count(null));
	}

	@Test
	public void testLineListingImport() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Abia", "Bende", "Bende Ward", "Bende Maternity Home");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_line_listing.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, false, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(caseImporter.stringBuilder.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, getCaseFacade().count(null));

		// Successful import of 5 cases from commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_comment_line_listing.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, false, user);
		importResult = caseImporter.runImport();

		assertEquals(caseImporter.stringBuilder.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(10, getCaseFacade().count(null));
	}

	@Test
	public void testImportWithInvalidCsvContent()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// csv with missing header
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_one_data_line_missing_header.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED_WITH_ERRORS, importResult);

		// csv with wrong separator
		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_contact_import_test_success.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user, ValueSeparator.SEMICOLON);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED_WITH_ERRORS, importResult);

	}

	public static class CaseImporterExtension extends CaseImporter {

		public StringBuilder stringBuilder = new StringBuilder("");
		private StringBuilderWriter writer = new StringBuilderWriter(stringBuilder);

		public CaseImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) throws IOException {
			this(inputFile, hasEntityClassRow, currentUser, ValueSeparator.DEFAULT);
		}

		public CaseImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator valueSeparator)
			throws IOException {
			super(inputFile, hasEntityClassRow, currentUser, valueSeparator);
		}

		@Override
		protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
			PersonDto newPerson,
			Consumer<T> resultConsumer,
			BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
			String infoText,
			UI currentUI) {
			resultConsumer.accept((T) new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
		}

		@Override
		protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
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
