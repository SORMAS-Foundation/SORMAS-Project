package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;
import de.symeda.sormas.api.importexport.hierarchical.ImportTemplateDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class LabImportDefinitionTest extends AbstractBeanTest {

	private static final String LAB = HierarchicalImportFacade.LAB_CASES_SAMPLES_TESTS;
	private static final String NEW_CASE_HEADER =
		"| case.externalID | case.disease | case.reportDate | case.responsibleRegion | case.responsibleDistrict | case.facilityType | case.healthFacility "
			+ "| person.firstName | person.lastName | person.sex | sample.fieldSampleID | sample.sampleDateTime | sample.sampleMaterial | sample.lab "
			+ "| test.testType | test.testDateTime | test.testResult |";
	private static final String NEW_CASE_ROW =
		"| LAB-1 | TUBERCULOSIS | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE | S1 | 9/2/2026 | BLOOD | Central Lab "
			+ "| CULTURE | 9/3/2026 | POSITIVE |";
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

		ImportPreviewDto preview = facade.dryRun(LAB, sheet);
		assertEquals(List.of(), preview.allIssues());
		assertEquals(1, preview.countBlocks(BlockStatus.READY));
		assertEquals(2, preview.countOperations(LabImportSchema.TEST, OperationType.CREATE));

		ImportReportDto report = facade.commit(LAB, sheet, preview.readyFingerprints());
		assertEquals(1, report.count(BlockOutcome.COMMITTED));

		List<Case> cases = getBean(CaseService.class).getByExternalId("LAB-1");
		assertEquals(1, cases.size());
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(List.of(cases.get(0).getUuid()));
		assertEquals(1, samples.size());
		assertEquals("S1", samples.get(0).getFieldSampleID());
		assertEquals(2, getPathogenTestFacade().getAllBySample(samples.get(0).toReference()).size());

		assertEquals(1, facade.dryRun(LAB, sheet).countBlocks(BlockStatus.UNCHANGED));
	}

	@Test
	public void attachesByUuidAndShowsTheSampleChangeBeforeSaving() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf);
		SampleDto existing = creator.createSample(caze.toReference(), nationalAdmin.toReference(), centralLab());
		existing.setFieldSampleID("S1");
		existing.setSampleMaterial(SampleMaterial.BLOOD);
		getSampleFacade().saveSample(existing);
		ImportSheet sheet =
			SheetFixtures.table("| case.uuid | sample.fieldSampleID | sample.sampleMaterial |", "| " + caze.getUuid() + " | S1 | SERA |");

		ImportPreviewDto preview = facade.dryRun(LAB, sheet);
		RecordOperationDto sampleOperation = preview.getBlocks().get(0).getOperations().get(1);
		assertEquals(OperationType.UPDATE, sampleOperation.getType());
		assertEquals("sample.sampleMaterial", sampleOperation.getChanges().get(0).getField());

		facade.commit(LAB, sheet, preview.readyFingerprints());

		List<SampleDto> samples = getSampleFacade().getByCaseUuids(List.of(caze.getUuid()));
		assertEquals(1, samples.size());
		assertEquals(SampleMaterial.SERA, samples.get(0).getSampleMaterial());
	}

	@Test
	public void attachesByExternalIdWithoutChangingTheCase() {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setExternalID("LAB-7"));
		ImportSheet sheet = SheetFixtures.table(
			"| case.externalID | sample.fieldSampleID | sample.sampleDateTime | sample.sampleMaterial | sample.lab |",
			"| LAB-7 | S9 | 9/2/2026 | BLOOD | Central Lab |");

		ImportPreviewDto preview = facade.dryRun(LAB, sheet);
		assertEquals(OperationType.UNCHANGED, preview.getBlocks().get(0).getOperations().get(0).getType());

		facade.commit(LAB, sheet, preview.readyFingerprints());

		assertEquals(1, getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).size());
	}

	@Test
	public void anUnknownUuidOrAnAmbiguousExternalIdFailsItsBlock() {
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setExternalID("DUP"));
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setExternalID("DUP"));
		ImportSheet sheet = SheetFixtures.table("| case.uuid | case.externalID | sample.fieldSampleID |", "| NOSUCHUUID | | S1 |", "| | DUP | S2 |");

		ImportPreviewDto preview = facade.dryRun(LAB, sheet);

		assertEquals(2, preview.countBlocks(BlockStatus.ERROR));
		assertEquals("case.uuid", preview.getBlocks().get(0).getIssues().get(0).getColumn());
		assertEquals("case.externalID", preview.getBlocks().get(1).getIssues().get(0).getColumn());
	}

	@Test
	public void aCaseEditedAfterThePreviewIsLeftAlone() throws InterruptedException {
		CaseDataDto caze = creator.createCase(nationalAdmin.toReference(), creator.createPerson().toReference(), rdcf);
		ImportSheet sheet = SheetFixtures.table("| case.uuid | person.firstName |", "| " + caze.getUuid() + " | Anna |");
		ImportPreviewDto preview = facade.dryRun(LAB, sheet);
		Thread.sleep(5);
		PersonDto person = getPersonFacade().getByUuid(caze.getPerson().getUuid());
		person.setLastName("Edited");
		getPersonFacade().save(person);

		ImportReportDto report = facade.commit(LAB, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.CHANGED_SINCE_PREVIEW, report.getResults().get(0).getOutcome());
		assertEquals(person.getFirstName(), getPersonFacade().getByUuid(person.getUuid()).getFirstName());
	}

	@Test
	public void aUserWithoutTheCaseImportRightCannotCreateCases() {
		UserRoleReferenceDto sampleOnly = creator
			.createUserRole("SampleOnly", JurisdictionLevel.NATION, UserRight.SAMPLE_CREATE, UserRight.SAMPLE_VIEW, UserRight.CASE_VIEW, UserRight.PERSON_VIEW);
		loginWith(creator.createUser(rdcf, sampleOnly));

		ImportPreviewDto preview = facade.dryRun(LAB, SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW));

		List<String> messages = new ArrayList<>();
		preview.getBlocks().get(0).getIssues().stream().map(ImportIssueDto::getMessage).forEach(messages::add);
		assertTrue(messages.contains(I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_IMPORT)));
	}

	@Test
	public void aFilledTemplateDryRunsWithoutIssues() {
		ImportTemplateDto template = facade.getTemplate(LAB);
		ImportSheet sample = SheetFixtures.table(NEW_CASE_HEADER, NEW_CASE_ROW);
		Map<String, String> values = new HashMap<>();
		for (int i = 0; i < sample.getHeaders().size(); i++) {
			values.put(sample.getHeaders().get(i), sample.getRows().get(0).cell(i));
		}
		List<String> row = new ArrayList<>();
		template.getHeaders().forEach(header -> row.add(values.getOrDefault(header, "")));

		ImportPreviewDto preview = facade.dryRun(LAB, new ImportSheet(template.getHeaders(), List.of(new ImportSheetRow(2, row))));

		assertEquals(List.of(), preview.allIssues());
		assertEquals(1, preview.countBlocks(BlockStatus.READY));
	}

	private de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto centralLab() {
		return getFacilityFacade().getLaboratoriesByName("Central Lab", false).get(0);
	}
}
