/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.caze.importer;

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
import java.util.Collections;
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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.importer.CaseImportSimilarityInput;
import de.symeda.sormas.ui.importer.CaseImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

public class CaseImporterTest extends AbstractUiBeanTest {

	/**
	 * This should be split into multiple tests. See #11618
	 */
	@Test
	public void testImportAllCases() throws IOException, InvalidColumnException, InterruptedException, CsvException, URISyntaxException {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_success.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());
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
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

		// Similarity: pick
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(new PersonSimilarityCriteria());
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
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

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
		assertEquals("ABC-DEF-GHI-19-5", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

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
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

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
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

		// Change epid number of the case in database to pass creation test
		CaseDataDto caze = getCaseFacade().getAllAfter(null).get(4);
		caze.setEpidNumber("ABC-DEF-GHI-19-99");
		getCaseFacade().save(caze);
		assertEquals("ABC-DEF-GHI-19-99", getCaseFacade().getAllAfter(null).get(4).getEpidNumber());

		// Similarity: create -> pass
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_similarities.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, getCaseFacade().count(null));
		assertEquals("ABC-DEF-GHI-19-10", getCaseFacade().getAllAfter(null).get(5).getEpidNumber());

		// Successful import of a case with different infrastructure combinations
		creator.createRDCF("R1", "D1", "C1", "F1");
		creator.createRDCF("R2", "D2", "C2", "F2");
		creator.createRDCF("R3", "D3", "C3", "F3");

		csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_different_infrastructure.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		InputStream errorStream = new ByteArrayInputStream(
			((CaseImporterTest.CaseImporterExtension) caseImporter).stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
		List<String[]> errorRows = CSVUtils.createBomCsvReader(errorStream).readAll();
		if (errorRows.size() > 2) {
			assertThat("Error during import: " + StringUtils.join(errorRows.get(2), ", "), errorRows, hasSize(0));
		}

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(7, getCaseFacade().count(null));

		// Successful import of 5 cases from a commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_comment_success.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, true, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());
		assertEquals(12, getCaseFacade().count(null));
	}

	@Test
	public void testLineListingImport() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Abia", "Bende", "Bende Ward", "Bende Maternity Home");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_line_listing.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, false, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());
		assertEquals(5, getCaseFacade().count(null));

		// Successful import of 5 cases from commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_import_test_comment_line_listing.csv").toURI());
		caseImporter = new CaseImporterExtension(csvFile, false, user);
		importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());
		assertEquals(10, getCaseFacade().count(null));
	}

	@Test
	public void testImportWithInvalidCsvContent()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Abia", "Umuahia North", "Urban Ward 2", "Anelechi Hospital");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

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

	@Test
	public void testImportAddressTypes()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Saarland", "RV Saarbrücken", "Kleinblittersdorf", "Winterberg");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// import of 3 cases with different address types
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_address_type.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		PersonDto casePerson1 = getPersonFacade().getByUuid(getCaseFacade().getByExternalId("SL-DEF-GHI-19-1").get(0).getPerson().getUuid());
		PersonDto casePerson2 = getPersonFacade().getByUuid(getCaseFacade().getByExternalId("SL-DEF-GHI-19-2").get(0).getPerson().getUuid());
		PersonDto casePerson3 = getPersonFacade().getByUuid(getCaseFacade().getByExternalId("SL-DEF-GHI-19-3").get(0).getPerson().getUuid());

		assertTrue(CollectionUtils.isEmpty(casePerson1.getAddresses()));
		assertEquals("131", casePerson1.getAddress().getHouseNumber());

		assertTrue(CollectionUtils.isEmpty(casePerson2.getAddresses()));
		assertEquals("132", casePerson2.getAddress().getHouseNumber());

		assertTrue(LocationHelper.checkIsEmptyLocation(casePerson3.getAddress()));
		assertEquals(1, casePerson3.getAddresses().size());
		assertEquals("133", casePerson3.getAddresses().get(0).getHouseNumber());
	}

	@Test
	public void testImportWithSamples() throws IOException, InterruptedException, CsvValidationException, InvalidColumnException, URISyntaxException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		creator.createFacility("Lab", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// import of 3 cases with different number of samples
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_samples.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());

		CaseDataDto case1 = getCaseFacade().getByExternalId("case1").get(0);
		CaseDataDto case2 = getCaseFacade().getByExternalId("case2").get(0);
		CaseDataDto case3 = getCaseFacade().getByExternalId("case3").get(0);

		assertEquals(0, getSampleFacade().getByCaseUuids(Collections.singletonList(case1.getUuid())).size());
		List<SampleDto> case2Samples = getSampleFacade().getByCaseUuids(Collections.singletonList(case2.getUuid()));
		assertEquals(1, case2Samples.size());

		assertEquals(SampleMaterial.BLOOD, case2Samples.get(0).getSampleMaterial());

		List<SampleDto> case3Samples = getSampleFacade().getByCaseUuids(Collections.singletonList(case3.getUuid()));
		assertEquals(2, case3Samples.size());
		assertEquals(1, case3Samples.stream().filter(s -> s.getSampleMaterial() == SampleMaterial.BLOOD).count(), "Should have one blood sample");
		assertEquals(1, case3Samples.stream().filter(s -> s.getSampleMaterial() == SampleMaterial.STOOL).count(), "Should have one stool sample");

	}

	@Test
	public void testImportWithPathogenTests()
		throws IOException, InterruptedException, CsvValidationException, InvalidColumnException, URISyntaxException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		creator.createFacility("Lab", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// import of 3 cases with different number of samples and pathogen tests
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_pathogen_tests.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());

		CaseDataDto case1 = getCaseFacade().getByExternalId("case1").get(0);
		CaseDataDto case2 = getCaseFacade().getByExternalId("case2").get(0);
		CaseDataDto case3 = getCaseFacade().getByExternalId("case3").get(0);

		assertEquals(0, getSampleFacade().getByCaseUuids(Collections.singletonList(case1.getUuid())).size());
		List<SampleDto> case2Samples = getSampleFacade().getByCaseUuids(Collections.singletonList(case2.getUuid()));

		List<PathogenTestDto> case2Tests = FacadeProvider.getPathogenTestFacade().getAllBySample(case2Samples.get(0).toReference());
		assertEquals(1, case2Tests.size());
		assertEquals(PathogenTestType.ANTIBODY_DETECTION, case2Tests.get(0).getTestType());
		assertEquals(PathogenTestResultType.POSITIVE, case2Tests.get(0).getTestResult());

		List<SampleDto> case3Samples = getSampleFacade().getByCaseUuids(Collections.singletonList(case3.getUuid()));
		List<PathogenTestDto> case3Sample1Tests = FacadeProvider.getPathogenTestFacade().getAllBySample(case3Samples.get(0).toReference());

		assertEquals(1, case3Sample1Tests.size());
		assertEquals(PathogenTestType.ANTIBODY_DETECTION, case3Sample1Tests.get(0).getTestType());
		assertEquals(PathogenTestResultType.POSITIVE, case3Sample1Tests.get(0).getTestResult());

		List<PathogenTestDto> case3Sample2Tests = FacadeProvider.getPathogenTestFacade().getAllBySample(case3Samples.get(1).toReference());
		assertEquals(2, case3Sample2Tests.size());
		assertEquals(PathogenTestType.ANTIGEN_DETECTION, case3Sample2Tests.get(0).getTestType());
		assertEquals(PathogenTestResultType.PENDING, case3Sample2Tests.get(0).getTestResult());

		assertEquals(PathogenTestType.RAPID_TEST, case3Sample2Tests.get(1).getTestType());
		assertEquals(PathogenTestResultType.NEGATIVE, case3Sample2Tests.get(1).getTestResult());
	}

	@Test
	@Disabled("Remove ignore once we have replaced H2, and feature properties can be changed by code")
	public void testImportWithVaccinations()
		throws IOException, InterruptedException, CsvValidationException, InvalidColumnException, URISyntaxException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		creator.createFacility("Lab", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// import of 3 cases with different number of vaccinations
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_case_import_test_vaccinations.csv").toURI());
		CaseImporterExtension caseImporter = new CaseImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = caseImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, caseImporter.stringBuilder.toString());

		CaseDataDto case1 = getCaseFacade().getByExternalId("case1").get(0);
		CaseDataDto case2 = getCaseFacade().getByExternalId("case2").get(0);
		CaseDataDto case3 = getCaseFacade().getByExternalId("case3").get(0);

		List<VaccinationDto> case1Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(case1.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(0, case1Vaccinations.size());

		List<VaccinationDto> case2Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(case2.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(1, case2Vaccinations.size());
		assertEquals(Vaccine.COMIRNATY, case2Vaccinations.get(0).getVaccineName());
		assertNull(case2Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());

		List<VaccinationDto> case3Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(case3.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(2, case3Vaccinations.size());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(0).getVaccineName());
		assertEquals(YesNoUnknown.YES, case3Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(1).getVaccineName());
		assertNull(case3Vaccinations.get(1).getHealthConditions().getChronicPulmonaryDisease());
	}

	public static class CaseImporterExtension extends CaseImporter {

		public StringBuilder stringBuilder = new StringBuilder();
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
