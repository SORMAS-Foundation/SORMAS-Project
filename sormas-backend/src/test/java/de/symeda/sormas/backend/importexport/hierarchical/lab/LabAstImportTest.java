package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.therapy.SusceptibilityMethod;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class LabAstImportTest extends AbstractBeanTest {

	private static final String LAB = HierarchicalImportFacade.LAB_CASES_SAMPLES_TESTS;
	private static final String HEADER =
		"| case.externalID | case.disease | case.reportDate | case.responsibleRegion | case.responsibleDistrict | case.facilityType | case.healthFacility "
			+ "| person.firstName | person.lastName | person.sex | sample.fieldSampleID | sample.sampleDateTime | sample.sampleMaterial | sample.lab "
			+ "| test.testType | test.testDateTime | test.testResult | ast.drug | ast.value | ast.method | ast.interpretation |";
	private static final String CASE_CELLS =
		"| TB-1 | TUBERCULOSIS | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE | S1 | 9/2/2026 | SPUTUM | Central Lab ";
	private static final String BLANK_CASE_CELLS = "| | | | | | | | | | | | | | ";

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
	public void writesEachDrugRowOntoTheTestSusceptibility() {
		ImportSheet sheet = SheetFixtures.table(
			HEADER,
			CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | AMIKACIN | 0.5 | ETEST | SUSCEPTIBLE |",
			BLANK_CASE_CELLS + "| | | | RIFAMPICIN | 22 mm | DISK_DIFFUSION | RESISTANT |");

		ImportPreviewDto preview = facade.dryRun(LAB, sheet);
		assertEquals(List.of(), preview.allIssues());
		facade.commit(LAB, sheet, preview.readyFingerprints());

		PathogenTestDto test = onlyTest();
		assertEquals("0.5", test.getDrugSusceptibility().getAmikacinMic());
		assertEquals(SusceptibilityMethod.ETEST, test.getDrugSusceptibility().getAmikacinMethod());
		assertEquals(DrugSusceptibilityType.SUSCEPTIBLE, test.getDrugSusceptibility().getAmikacinSusceptibility());
		assertEquals(DrugSusceptibilityType.RESISTANT, test.getDrugSusceptibility().getRifampicinSusceptibility());
	}

	@Test
	public void rejectsADrugThatDoesNotApplyToTheDisease() {
		assertBlockFails(CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | MEROPENEM | 1 | ETEST | SUSCEPTIBLE |");
	}

	@Test
	public void rejectsTheSameDrugTwice() {
		assertBlockFails(
			CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | AMIKACIN | 0.5 | ETEST | SUSCEPTIBLE |",
			BLANK_CASE_CELLS + "| | | | AMIKACIN | 1 | ETEST | RESISTANT |");
	}

	@Test
	public void rejectsAstRowsUnderATestTypeWithoutAst() {
		assertBlockFails(CASE_CELLS + "| CULTURE | 9/3/2026 | POSITIVE | AMIKACIN | 0.5 | ETEST | SUSCEPTIBLE |");
	}

	@Test
	public void rejectsAnUnknownDrug() {
		assertBlockFails(CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | UNOBTAINIUM | 0.5 | ETEST | SUSCEPTIBLE |");
	}

	@Test
	public void showsFollowUpAstOnAnExistingTestAsAChange() {
		ImportSheet first =
			SheetFixtures.table(HEADER, CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | AMIKACIN | 0.5 | ETEST | SUSCEPTIBLE |");
		facade.commit(LAB, first, facade.dryRun(LAB, first).readyFingerprints());
		ImportSheet followUp = SheetFixtures.table(
			HEADER,
			CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | AMIKACIN | 0.5 | ETEST | SUSCEPTIBLE |",
			BLANK_CASE_CELLS + "| | | | RIFAMPICIN | 22 mm | DISK_DIFFUSION | RESISTANT |");

		ImportPreviewDto preview = facade.dryRun(LAB, followUp);

		assertEquals(BlockStatus.READY, preview.getBlocks().get(0).getStatus());
		assertEquals(OperationType.UPDATE, preview.getBlocks().get(0).getOperations().get(2).getType());
		assertEquals(3, preview.getBlocks().get(0).getOperations().get(2).getChanges().size());
		facade.commit(LAB, followUp, preview.readyFingerprints());
		assertEquals(DrugSusceptibilityType.RESISTANT, onlyTest().getDrugSusceptibility().getRifampicinSusceptibility());
		assertEquals(DrugSusceptibilityType.SUSCEPTIBLE, onlyTest().getDrugSusceptibility().getAmikacinSusceptibility());
	}

	private void assertBlockFails(String... rows) {
		String[] lines = new String[rows.length + 1];
		lines[0] = HEADER;
		System.arraycopy(rows, 0, lines, 1, rows.length);

		ImportPreviewDto preview = facade.dryRun(LAB, SheetFixtures.table(lines));

		assertEquals(BlockStatus.ERROR, preview.getBlocks().get(0).getStatus());
		assertTrue(
			preview.getBlocks().get(0).getIssues().stream().anyMatch(issue -> LabImportSchema.AST_DRUG.equals(issue.getColumn())),
			preview.getBlocks().get(0).getIssues().toString());
	}

	private PathogenTestDto onlyTest() {
		Case caze = getBean(CaseService.class).getByExternalId("TB-1").get(0);
		SampleDto sample = getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).get(0);
		return getPathogenTestFacade().getAllBySample(sample.toReference()).get(0);
	}
}
