package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade.CASE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.caseimport.CaseImportEntities;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb.CaseImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class CaseTemplateImportDefinitionTest extends AbstractBeanTest {

	private static final String NEW_CASE_HEADER = "| CaseData.disease | CaseData.epidNumber | CaseData.reportDate | CaseData.responsibleRegion "
		+ "| CaseData.responsibleDistrict | CaseData.facilityType | CaseData.healthFacility | Person.person.firstName | Person.person.lastName "
		+ "| Person.person.sex | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab | PathogenTest.testType "
		+ "| PathogenTest.testDateTime | PathogenTest.testResult |";
	private static final String NEW_CASE_ROW = "| TUBERCULOSIS | AAA-BBB-CCC-26-001 | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE "
		+ "| S1 | 9/2/2026 | BLOOD | Central Lab | CULTURE | 9/3/2026 | POSITIVE |";
	private static final String SECOND_TEST_ROW = "| | | | | | | | | | | | | | | PCR_RT_PCR | 9/3/2026 | NEGATIVE |";

	private TestDataCreator.RDCF rdcf;
	private HierarchicalImportFacade facade;

	@Override
	public void init() {
		super.init();
		useNationalAdminLogin();
		rdcf = creator.createRDCF();
		creator.createFacility("Central Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);
		facade = getBean(HierarchicalImportFacadeEjbLocal.class);
	}

	@Test
	public void createsACaseWithItsSampleAndTestsAndAReuploadIsUnchanged() {
		ImportSheet sheet = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW, SECOND_TEST_ROW);

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(List.of(), preview.allIssues());
		assertEquals(1, preview.countBlocks(BlockStatus.READY));
		assertEquals(2, preview.countOperations(CaseTemplateSchema.TEST, OperationType.CREATE));

		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());
		assertEquals(1, report.count(BlockOutcome.COMMITTED));

		List<Case> cases = getBean(CaseService.class).getUndeletedByAttributeValues(Case.EPID_NUMBER, List.of("AAA-BBB-CCC-26-001"));
		assertEquals(1, cases.size());
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(List.of(cases.get(0).getUuid()));
		assertEquals(1, samples.size());
		assertEquals("S1", samples.get(0).getFieldSampleID());
		assertEquals(2, getPathogenTestFacade().getAllBySample(samples.get(0).toReference()).size());

		assertEquals(1, facade.checkImport(CASE_TEMPLATE, sheet).countBlocks(BlockStatus.UNCHANGED));
	}

	@Test
	public void attachesByExternalTokenAndShowsTheSampleChangeBeforeSaving() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setExternalToken("TOK-1"));
		SampleDto existing = creator.createSample(caze.toReference(), nationalAdmin.toReference(), centralLab());
		existing.setFieldSampleID("S1");
		existing.setSampleMaterial(SampleMaterial.BLOOD);
		getSampleFacade().saveSample(existing);
		ImportSheet sheet = SheetFixtures.table("| CaseData.externalToken | Sample.fieldSampleID | Sample.sampleMaterial |", "| TOK-1 | S1 | SERA |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		RecordOperationDto sampleOperation = preview.getBlocks().get(0).getOperations().get(1);
		assertEquals(OperationType.UPDATE, sampleOperation.getType());
		assertEquals("Sample.sampleMaterial", sampleOperation.getChanges().get(0).getField());
		assertEquals(MatchReason.BY_ID, preview.getBlocks().get(0).getOperations().get(0).getMatchReason());

		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		List<SampleDto> samples = getSampleFacade().getByCaseUuids(List.of(caze.getUuid()));
		assertEquals(1, samples.size());
		assertEquals(SampleMaterial.SERA, samples.get(0).getSampleMaterial());
	}

	@Test
	public void attachesByEpidNumberWithoutChangingTheCase() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-007"));
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.epidNumber | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab |",
			"| AAA-BBB-CCC-26-007 | S9 | 9/2/2026 | BLOOD | Central Lab |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(OperationType.UNCHANGED, preview.getBlocks().get(0).getOperations().get(0).getType());

		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(1, getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).size());
	}

	@Test
	public void attachesByCaseReferenceNumberWhenSelfReportingIsOn() {
		createFeatureConfiguration(FeatureType.SELF_REPORTING, true);
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setCaseReferenceNumber("REF-1"));
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.caseReferenceNumber | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab |",
			"| REF-1 | S9 | 9/2/2026 | BLOOD | Central Lab |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(MatchReason.BY_ID, preview.getBlocks().get(0).getOperations().get(0).getMatchReason());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(1, getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).size());
	}

	@Test
	public void anUnknownTokenOrAnAmbiguousEpidNumberFailsItsBlock() {
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-090"));
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-090"));
		ImportSheet sheet = SheetFixtures
			.table("| CaseData.externalToken | CaseData.epidNumber | Sample.fieldSampleID |", "| NOSUCHTOKEN | | S1 |", "| | AAA-BBB-CCC-26-090 | S2 |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(2, preview.countBlocks(BlockStatus.ERROR));
		assertEquals(CaseTemplateSchema.EXTERNAL_TOKEN, preview.getBlocks().get(0).getIssues().get(0).getColumn());
		assertEquals(CaseTemplateSchema.EPID_NUMBER, preview.getBlocks().get(1).getIssues().get(0).getColumn());
	}

	@Test
	public void anIdOnlyRowForAMissingCaseSaysSoOnItsColumn() {
		ImportSheet sheet = SheetFixtures.table("| CaseData.epidNumber | Sample.fieldSampleID |", "| AAA-BBB-CCC-26-099 | S1 |");

		List<ImportIssueDto> issues = facade.checkImport(CASE_TEMPLATE, sheet).allIssues();

		assertEquals(CaseTemplateSchema.EPID_NUMBER, issues.get(0).getColumn(), issues.toString());
		assertEquals(
			I18nProperties.getValidationError(Validations.importHierarchicalCaseIdNotFound, "AAA-BBB-CCC-26-099", CaseTemplateSchema.EPID_NUMBER),
			issues.get(0).getMessage());
		assertTrue(
			issues.stream().noneMatch(issue -> issue.getColumn() != null && issue.getColumn().startsWith(CaseTemplateSchema.PERSON + ".")),
			issues.toString());
		assertEquals(1, issues.stream().filter(issue -> !String.valueOf(issue.getColumn()).startsWith("Sample.") && issue.getColumn() != null).count());
	}

	@Test
	public void twoIdsPointingAtDifferentCasesFailTheBlock() {
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-011"));
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setExternalToken("T-2"));
		ImportSheet sheet = SheetFixtures.table("| CaseData.epidNumber | CaseData.externalToken |", "| AAA-BBB-CCC-26-011 | T-2 |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(1, preview.countBlocks(BlockStatus.ERROR));
		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalCaseIdsDisagree)));
	}

	@Test
	public void aCaseEditedAfterThePreviewIsLeftAlone() throws InterruptedException {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-013"));
		ImportSheet sheet = SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName |", "| AAA-BBB-CCC-26-013 | Anna |");
		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		Thread.sleep(5);
		PersonDto person = getPersonFacade().getByUuid(caze.getPerson().getUuid());
		person.setLastName("Edited");
		getPersonFacade().save(person);

		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.CHANGED_SINCE_PREVIEW, report.getResults().get(0).getOutcome());
		assertEquals(person.getFirstName(), getPersonFacade().getByUuid(person.getUuid()).getFirstName());
	}

	@Test
	public void aUserWithoutTheCaseImportRightCannotCreateCases() {
		UserRoleReferenceDto sampleOnly = creator
			.createUserRole("SampleOnly", JurisdictionLevel.NATION, UserRight.SAMPLE_CREATE, UserRight.SAMPLE_VIEW, UserRight.CASE_VIEW, UserRight.PERSON_VIEW);
		loginWith(creator.createUser(rdcf, sampleOnly));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW));

		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_IMPORT)));
	}

	@Test
	public void anImporterWithoutTheCaseCreateRightSeesItBeforeSaving() {
		UserRoleReferenceDto importOnly = creator.createUserRole(
			"ImportOnly",
			JurisdictionLevel.NATION,
			UserRight.CASE_IMPORT,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW,
			UserRight.SAMPLE_VIEW,
			UserRight.SAMPLE_CREATE);
		loginWith(creator.createUser(rdcf, importOnly));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW));

		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_CREATE)));
	}

	@Test
	public void anUnknownIdWithCaseDataCreatesTheCaseWithThatId() {
		ImportSheet sheet = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW);

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(List.of(), preview.allIssues());
		assertEquals(MatchReason.NEW, preview.getBlocks().get(0).getOperations().get(0).getMatchReason());
	}

	@Test
	public void aFilledTemplateDryRunsWithoutIssues() {
		List<ImportColumn> columns =
			getBean(ImportFacadeEjbLocal.class).caseImportColumns(getFeatureConfigurationFacade().getActiveServerFeatureConfigurations());
		List<String> headers = new ArrayList<>();
		columns.forEach(column -> headers.add(CaseTemplateLayout.join(column.getEntityName(), column.getColumnName())));
		ImportSheet filled = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW);
		Map<String, String> values = new HashMap<>();
		for (int i = 0; i < filled.getHeaders().size(); i++) {
			values.put(filled.getHeaders().get(i), filled.getRows().get(0).cell(i));
		}
		List<String> row = new ArrayList<>();
		headers.forEach(header -> row.add(values.getOrDefault(header, "")));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, new ImportSheet(headers, List.of(new ImportSheetRow(5, row))));

		assertEquals(List.of(), preview.allIssues());
		assertEquals(List.of(), preview.getIgnoredColumns());
		assertEquals(1, preview.countBlocks(BlockStatus.READY));
	}

	@Test
	public void aCaseReachedByTokenAndByEpidNumberInTwoBlocksFailsTheLaterBlock() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> {
			c.setExternalToken("ALIAS-T");
			c.setEpidNumber("AAA-BBB-CCC-26-020");
		});
		String disease = caze.getDisease().name();
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.disease | CaseData.externalToken | CaseData.epidNumber | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial "
				+ "| Sample.lab |",
			"| " + disease + " | ALIAS-T | | S1 | 9/2/2026 | BLOOD | Central Lab |",
			"| " + disease + " | | AAA-BBB-CCC-26-020 | S2 | 9/2/2026 | BLOOD | Central Lab |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(BlockStatus.READY, preview.getBlocks().get(0).getStatus());
		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(1).getStatus());
		String message = preview.getBlocks().get(1).getIssues().get(0).getMessage();
		assertTrue(message.startsWith("AAA-BBB-CCC-26-020 "), message);
	}

	@Test
	public void anIgnoredCaseColumnOnAContinuationRowKeepsTheRowUnderTheCaseAbove() {
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.colour " + NEW_CASE_HEADER,
			"| " + NEW_CASE_ROW,
			"| blue " + SECOND_TEST_ROW);

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(1, preview.getBlocks().size(), messages(preview).toString());
		assertEquals(BlockStatus.READY, preview.getBlocks().get(0).getStatus(), messages(preview).toString());
	}

	@Test
	public void aDeletedCaseIsNeverMatchedByEpidNumber() {
		CaseDataDto caze =
			creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-019"));
		getCaseFacade().delete(caze.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test"));

		ImportPreviewDto preview =
			facade.checkImport(CASE_TEMPLATE, SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName |", "| AAA-BBB-CCC-26-019 | Anna |"));

		RecordOperationDto root = preview.getBlocks().get(0).getOperations().get(0);
		assertEquals(OperationType.CREATE, root.getType());
		assertEquals(MatchReason.NEW, root.getMatchReason());
	}

	@Test
	public void twoStoredSamplesWithTheSameFieldSampleIdFailTheBlock() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-014"));
		for (int i = 0; i < 2; i++) {
			SampleDto sample = creator.createSample(caze.toReference(), nationalAdmin.toReference(), centralLab());
			sample.setFieldSampleID("S1");
			getSampleFacade().saveSample(sample);
		}

		ImportPreviewDto preview =
			facade.checkImport(CASE_TEMPLATE, SheetFixtures.table("| CaseData.epidNumber | Sample.fieldSampleID | Sample.sampleMaterial |", "| AAA-BBB-CCC-26-014 | S1 | SERA |"));

		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(0).getStatus());
		assertEquals(CaseTemplateSchema.FIELD_SAMPLE_ID, preview.getBlocks().get(0).getIssues().get(0).getColumn());
	}

	@Test
	public void aUserWithoutCaseEditCannotUpdateACase() {
		creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-015"));
		UserRoleReferenceDto noEdit = creator.createUserRole(
			"NoCaseEdit",
			JurisdictionLevel.NATION,
			UserRight.CASE_VIEW,
			UserRight.CASE_IMPORT,
			UserRight.PERSON_VIEW,
			UserRight.SAMPLE_VIEW,
			UserRight.SAMPLE_CREATE,
			UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION);
		loginWith(creator.createUser(rdcf, noEdit));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName |", "| AAA-BBB-CCC-26-015 | Anna |"));

		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_EDIT)));
	}

	@Test
	public void aCaseOutsideTheUsersJurisdictionCannotBeChanged() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-016"));
		TestDataCreator.RDCF elsewhere = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		UserRoleReferenceDto districtImporter = creator.createUserRole(
			"DistrictImporter",
			JurisdictionLevel.DISTRICT,
			UserRight.CASE_VIEW,
			UserRight.CASE_EDIT,
			UserRight.CASE_IMPORT,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT,
			UserRight.SAMPLE_VIEW,
			UserRight.SAMPLE_CREATE,
			UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION);
		loginWith(creator.createUser(elsewhere, districtImporter));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName |", "| AAA-BBB-CCC-26-016 | Anna |"));

		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(0).getStatus(), messages(preview).toString());
		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalCaseNotEditable, caze.getUuid())));
	}

	@Test
	public void aRecordTheUserSeesMaskedCannotBeChanged() {
		creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-017"));
		UserRoleReferenceDto masked = creator.createUserRole(
			"NoPersonalData",
			JurisdictionLevel.NATION,
			UserRight.CASE_VIEW,
			UserRight.CASE_EDIT,
			UserRight.CASE_IMPORT,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT,
			UserRight.SAMPLE_VIEW,
			UserRight.SAMPLE_CREATE);
		loginWith(creator.createUser(rdcf, masked));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName |", "| AAA-BBB-CCC-26-017 | Anna |"));

		assertTrue(messages(preview).contains(I18nProperties.getValidationError(Validations.importHierarchicalMaskedRecord)), messages(preview).toString());
	}

	@Test
	public void aSampleDateTimeKeepsItsTime() {
		ImportSheet sheet = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW.replace("| S1 | 9/2/2026 |", "| S1 | 9/2/2026 14:30 |"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(List.of(), preview.allIssues());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		Calendar sampleTime = Calendar.getInstance();
		sampleTime.setTime(getSampleFacade().getByCaseUuids(List.of(onlyCase().getUuid())).get(0).getSampleDateTime());
		assertEquals(14, sampleTime.get(Calendar.HOUR_OF_DAY));
		assertEquals(30, sampleTime.get(Calendar.MINUTE));
	}

	@Test
	public void aClassicCellAndANestedCellGiveTheSameCase() throws InvalidColumnException {
		String header = "| CaseData.disease | CaseData.epidNumber | CaseData.reportDate | CaseData.responsibleRegion | CaseData.responsibleDistrict "
			+ "| CaseData.facilityType | CaseData.healthFacility | Person.person.firstName | Person.person.lastName | Person.person.sex |";
		String caseRow = "| TUBERCULOSIS | AAA-BBB-CCC-26-001 | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE |";
		List<String> headers = SheetFixtures.table(header, caseRow).getHeaders();
		String[] entityClasses = headers.stream().map(CaseTemplateLayout::entityOf).toArray(String[]::new);
		String[] entityProperties = headers.stream().map(CaseTemplateLayout::propertyOf).toArray(String[]::new);
		String[][] entityPropertyPaths = Arrays.stream(entityProperties).map(property -> property.split("\\.")).toArray(String[][]::new);
		String[] classicValues = SheetFixtures.table(header, caseRow.replace("| Muller |", "| Classic |").replace("| AAA-BBB-CCC-26-001 |", "| AAA-BBB-CCC-26-002 |"))
			.getRows()
			.get(0)
			.getCells()
			.stream()
			.map(String::strip)
			.toArray(String[]::new);

		ImportLineResultDto<CaseImportEntities> classic =
			getBean(CaseImportFacadeEjbLocal.class).importCaseData(classicValues, entityClasses, entityProperties, entityPropertyPaths, false);
		assertTrue(classic.isSuccess(), classic.getMessage());
		ImportSheet sheet = SheetFixtures.table(header, caseRow);
		facade.saveImport(CASE_TEMPLATE, sheet, facade.checkImport(CASE_TEMPLATE, sheet).readyFingerprints());

		CaseDataDto classicCase = getCaseFacade().getByUuid(caseWithEpidNumber("AAA-BBB-CCC-26-002").getUuid());
		CaseDataDto nestedCase = getCaseFacade().getByUuid(caseWithEpidNumber("AAA-BBB-CCC-26-001").getUuid());
		for (Function<CaseDataDto, Object> field : List.<Function<CaseDataDto, Object>> of(
			CaseDataDto::getDisease,
			CaseDataDto::getReportDate,
			CaseDataDto::getResponsibleRegion,
			CaseDataDto::getResponsibleDistrict,
			CaseDataDto::getFacilityType,
			CaseDataDto::getHealthFacility)) {
			assertEquals(field.apply(classicCase), field.apply(nestedCase));
		}
		assertEquals(
			getPersonFacade().getByUuid(classicCase.getPerson().getUuid()).getSex(),
			getPersonFacade().getByUuid(nestedCase.getPerson().getUuid()).getSex());
	}

	@Test
	public void anEpidNumberOfAnotherDiseasesCaseIsNotMatched() {
		CaseDataDto covid = creator.createCase(nationalAdmin.toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.setEpidNumber("AAA-BBB-CCC-26-001");
		});

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW));

		assertEquals(List.of(), preview.allIssues());
		assertEquals(MatchReason.NEW, preview.getBlocks().get(0).getOperations().get(0).getMatchReason());
		assertEquals(Disease.CORONAVIRUS, getCaseFacade().getByUuid(covid.getUuid()).getDisease());
	}

	@Test
	public void aNewCaseWithAnEpidNumberThatAlreadyExistsIsRejectedLikeTheClassicImport() {
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> {
			c.setDisease(Disease.TUBERCULOSIS);
			c.setEpidNumber("AAA-BBB-CCC-26-001");
		});

		ImportPreviewDto preview = facade
			.checkImport(CASE_TEMPLATE, SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW.replace("| AAA-BBB-CCC-26-001 |", "| AAA-BBB-CCC-26-1 |")));

		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(0).getStatus());
		assertEquals(CaseTemplateSchema.EPID_NUMBER, preview.getBlocks().get(0).getIssues().get(0).getColumn());
		assertEquals(I18nProperties.getString(Strings.messageEpidNumberWarning), preview.getBlocks().get(0).getIssues().get(0).getMessage());
	}

	@Test
	public void aNonHomeAddressIsMovedToTheOtherAddressesLikeTheClassicImport() {
		ImportSheet sheet = SheetFixtures.table(
			NEW_CASE_HEADER.replace("| Person.person.sex |", "| Person.person.sex | Location.person.address.addressType | Location.person.address.city |"),
			NEW_CASE_ROW.replace("| MALE |", "| MALE | PLACE_OF_WORK | Esch |"));

		facade.saveImport(CASE_TEMPLATE, sheet, facade.checkImport(CASE_TEMPLATE, sheet).readyFingerprints());

		PersonDto person = getPersonFacade().getByUuid(onlyCase().getPerson().getUuid());
		assertNull(person.getAddress().getCity());
		assertEquals(1, person.getAddresses().size());
		assertEquals("Esch", person.getAddresses().get(0).getCity());
	}

	@Test
	public void aFileWithoutSymptomsColumnsCreatesACaseWithEmptySymptoms() {
		ImportSheet sheet = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW);
		facade.saveImport(CASE_TEMPLATE, sheet, facade.checkImport(CASE_TEMPLATE, sheet).readyFingerprints());

		assertNotNull(onlyCase().getSymptoms());
		assertNull(onlyCase().getSymptoms().getFever());
	}

	@Test
	public void aBlankCellLeavesTheStoredValueAlone() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> {
			c.setEpidNumber("AAA-BBB-CCC-26-018");
			c.setDiseaseDetails("kept");
		});
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.epidNumber | CaseData.diseaseDetails | CaseData.additionalDetails |",
			"| AAA-BBB-CCC-26-018 | | changed |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(OperationType.UPDATE, preview.getBlocks().get(0).getOperations().get(0).getType(), preview.allIssues().toString());
		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.COMMITTED, report.getResults().get(0).getOutcome(), report.getResults().get(0).getIssues().toString());
		CaseDataDto stored = getCaseFacade().getByUuid(caze.getUuid());
		assertEquals("changed", stored.getAdditionalDetails());
		assertEquals("kept", stored.getDiseaseDetails());
	}

	@Test
	public void aCaseStoredLongBeforeTheImportIsUpdatedByItsId() throws InterruptedException {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-019"));
		Thread.sleep(1100);
		ImportSheet sheet =
			SheetFixtures.table("| CaseData.epidNumber | CaseData.additionalDetails | Person.person.nickname |", "| AAA-BBB-CCC-26-019 | changed | JJ |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.COMMITTED, report.getResults().get(0).getOutcome(), report.getResults().get(0).getIssues().toString());
		assertEquals("changed", getCaseFacade().getByUuid(caze.getUuid()).getAdditionalDetails());
		assertEquals("JJ", getPersonFacade().getByUuid(caze.getPerson().getUuid()).getNickname());
	}

	@Test
	public void aColumnHiddenFromTheTemplateIsStillImported() {
		ImportSheet sheet = SheetFixtures.table(
			NEW_CASE_HEADER.replace("| CaseData.disease |", "| CaseData.externalID | CaseData.disease |"),
			NEW_CASE_ROW.replace("| TUBERCULOSIS |", "| EXT-1 | TUBERCULOSIS |"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(List.of(), preview.allIssues());
		assertEquals(List.of(), preview.getIgnoredColumns());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals("EXT-1", onlyCase().getExternalID());
	}

	@Test
	public void aMadeUpColumnIsIgnoredAndImportStaysPossible() {
		ImportSheet sheet = SheetFixtures.table(
			NEW_CASE_HEADER.replace("| CaseData.disease |", "| CaseData.colour | CaseData.disease |"),
			NEW_CASE_ROW.replace("| TUBERCULOSIS |", "| blue | TUBERCULOSIS |"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(List.of("CaseData.colour"), preview.getIgnoredColumns());
		assertTrue(preview.canImport());
	}

	@Test
	public void aFilledVaccinationCellIsAFileIssue() {
		ImportSheet sheet = SheetFixtures.table(
			NEW_CASE_HEADER.replace("| CaseData.disease |", "| Vaccination.vaccineName | CaseData.disease |"),
			NEW_CASE_ROW.replace("| TUBERCULOSIS |", "| COMIRNATY | TUBERCULOSIS |"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(1, preview.getFileIssues().size(), preview.getFileIssues().toString());
		assertEquals(
			I18nProperties.getValidationError(Validations.importHierarchicalVaccinationNotNested),
			preview.getFileIssues().get(0).getMessage());
	}

	private CaseDataDto onlyCase() {
		List<String> uuids = getCaseFacade().getAllActiveUuids();
		assertEquals(1, uuids.size());
		return getCaseFacade().getByUuid(uuids.get(0));
	}

	private Case caseWithEpidNumber(String epidNumber) {
		return getBean(CaseService.class).getUndeletedByAttributeValues(Case.EPID_NUMBER, List.of(epidNumber)).get(0);
	}

	private static List<String> messages(ImportPreviewDto preview) {
		List<String> messages = new ArrayList<>();
		preview.getBlocks().forEach(block -> block.getIssues().forEach(issue -> messages.add(issue.getMessage())));
		return messages;
	}

	private FacilityReferenceDto centralLab() {
		return getFacilityFacade().getLaboratoriesByName("Central Lab", false).get(0);
	}
}
