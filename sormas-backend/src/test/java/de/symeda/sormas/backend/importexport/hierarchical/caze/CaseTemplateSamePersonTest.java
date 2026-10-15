package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade.CASE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class CaseTemplateSamePersonTest extends AbstractBeanTest {

	private static final String HEADER = "| CaseData.disease | CaseData.reportDate | CaseData.responsibleRegion | CaseData.responsibleDistrict "
		+ "| CaseData.facilityType | CaseData.healthFacility | Person.person.firstName | Person.person.lastName | Person.person.sex "
		+ "| Person.person.nickname | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab |";

	private HierarchicalImportFacade facade;

	@Override
	public void init() {
		super.init();
		useNationalAdminLogin();
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		creator.createFacility("Central Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);
		facade = getBean(HierarchicalImportFacadeEjbLocal.class);
	}

	@Test
	public void twoBlocksForTheSameNewPersonShareOnePerson() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("TUBERCULOSIS", "", "S1", "BLOOD"), row("MEASLES", "", "S2", "BLOOD"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		RecordOperationDto linked = root(preview, 1);
		assertEquals(MatchReason.NEW_FOR_PERSON_IN_FILE, linked.getMatchReason(), preview.allIssues().toString());
		assertEquals(2, linked.getLinkedLine());
		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());
		assertEquals(2, report.count(BlockOutcome.COMMITTED), report.getResults().toString());
		List<String> personUuids = getCaseFacade().getByUuids(getCaseFacade().getAllActiveUuids())
			.stream()
			.map(caze -> caze.getPerson().getUuid())
			.distinct()
			.collect(Collectors.toList());
		assertEquals(1, personUuids.size());
	}

	@Test
	public void aLinkedBlockSavedWithoutItsPersonBlockIsNotImported() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("TUBERCULOSIS", "", "S1", "BLOOD"), row("MEASLES", "", "S2", "BLOOD"));
		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		Map<Integer, String> fingerprints = preview.readyFingerprints();

		facade.saveImport(CASE_TEMPLATE, sheet, Map.of(0, fingerprints.get(0)));
		ImportReportDto second = facade.saveImport(CASE_TEMPLATE, sheet, Map.of(1, fingerprints.get(1)));

		assertNotEquals(BlockOutcome.COMMITTED, second.getResults().get(0).getOutcome());
		assertEquals(1, getCaseFacade().getAllActiveUuids().size());
	}

	@Test
	public void aLinkedBlockFailsWithThePersonBlockItDependsOn() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("TUBERCULOSIS", "", "S1", "NOT_A_MATERIAL"), row("MEASLES", "", "S2", "BLOOD"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(0).getStatus());
		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(1).getStatus());
		assertTrue(
			messages(preview, 1).contains(I18nProperties.getValidationError(Validations.importHierarchicalLinkedPersonNotImported, 2)),
			messages(preview, 1).toString());
	}

	@Test
	public void personCellsThatDisagreeFailTheLinkedBlock() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("TUBERCULOSIS", "JJ", "S1", "BLOOD"), row("MEASLES", "KK", "S2", "BLOOD"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(BlockStatus.READY, preview.getBlocks().get(0).getStatus());
		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(1).getStatus());
		assertTrue(
			messages(preview, 1).contains(I18nProperties.getValidationError(Validations.importHierarchicalPersonDiffersInFile, 2)),
			messages(preview, 1).toString());
	}

	@Test
	public void differentPeopleInOneFileGetTheirOwnPersons() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("TUBERCULOSIS", "", "S1", "BLOOD"), row("MEASLES", "", "S2", "BLOOD").replace("Jean", "Ana"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(MatchReason.NEW, root(preview, 1).getMatchReason());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());
		List<CaseDataDto> cases = getCaseFacade().getByUuids(getCaseFacade().getAllActiveUuids());
		assertNotEquals(cases.get(0).getPerson().getUuid(), cases.get(1).getPerson().getUuid());
	}

	private static String row(String disease, String nickname, String sampleId, String material) {
		return "| " + disease + " | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE | " + nickname + " | " + sampleId
			+ " | 9/2/2026 | " + material + " | Central Lab |";
	}

	private static RecordOperationDto root(ImportPreviewDto preview, int block) {
		return preview.getBlocks().get(block).getOperations().get(0);
	}

	private static List<String> messages(ImportPreviewDto preview, int block) {
		return preview.getBlocks().get(block).getIssues().stream().map(ImportIssueDto::getMessage).collect(Collectors.toList());
	}
}
