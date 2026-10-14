package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade.CASE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class CaseTemplateAstImportTest extends AbstractBeanTest {

	private static final String EPID = "AAA-BBB-CCC-26-101";
	private static final String HEADER = "| CaseData.epidNumber | CaseData.disease | CaseData.reportDate | CaseData.responsibleRegion "
		+ "| CaseData.responsibleDistrict | CaseData.facilityType | CaseData.healthFacility | Person.person.firstName | Person.person.lastName "
		+ "| Person.person.sex | Sample.fieldSampleID | Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab | PathogenTest.testType "
		+ "| PathogenTest.testDateTime | PathogenTest.testResult | DrugSusceptibility.drugSusceptibility.amikacinMic "
		+ "| DrugSusceptibility.drugSusceptibility.amikacinSusceptibility | DrugSusceptibility.drugSusceptibility.meropenemSusceptibility |";
	private static final String CASE_CELLS =
		"| " + EPID + " | TUBERCULOSIS | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE | S1 | 9/2/2026 | SPUTUM | Central Lab ";

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
	public void savesDrugColumnsOnTheTest() {
		ImportSheet sheet = SheetFixtures.table(HEADER, CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | 0.5 | SUSCEPTIBLE | |");

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(List.of(), preview.allIssues());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		PathogenTestDto test = onlyTest();
		assertEquals("0.5", test.getDrugSusceptibility().getAmikacinMic());
		assertEquals(DrugSusceptibilityType.SUSCEPTIBLE, test.getDrugSusceptibility().getAmikacinSusceptibility());
	}

	@Test
	public void aDrugThatDoesNotApplyToTheDiseaseIsAnIssueOnItsColumn() {
		ImportSheet sheet = SheetFixtures.table(HEADER, CASE_CELLS + "| ANTIBIOTIC_SUSCEPTIBILITY | 9/3/2026 | POSITIVE | | | RESISTANT |");

		List<ImportIssueDto> issues = facade.checkImport(CASE_TEMPLATE, sheet).allIssues();

		assertEquals(1, issues.size(), issues.toString());
		assertEquals("DrugSusceptibility.drugSusceptibility.meropenemSusceptibility", issues.get(0).getColumn());
	}

	@Test
	public void aDrugColumnOnATestThatRecordsNoSusceptibilityIsAnIssue() {
		ImportSheet sheet = SheetFixtures.table(HEADER, CASE_CELLS + "| CULTURE | 9/3/2026 | POSITIVE | 0.5 | SUSCEPTIBLE | |");

		List<ImportIssueDto> issues = facade.checkImport(CASE_TEMPLATE, sheet).allIssues();

		assertEquals(1, issues.size(), issues.toString());
		assertEquals("DrugSusceptibility.drugSusceptibility.amikacinMic", issues.get(0).getColumn());
	}

	private PathogenTestDto onlyTest() {
		Case caze = getBean(CaseService.class).getUndeletedByAttributeValues(Case.EPID_NUMBER, List.of(EPID)).get(0);
		SampleDto sample = getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).get(0);
		return getPathogenTestFacade().getAllBySample(sample.toReference()).get(0);
	}
}
