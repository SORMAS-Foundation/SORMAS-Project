package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade.CASE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class CaseSimilarityMatchTest extends AbstractBeanTest {

	private static final String HEADER = "| CaseData.disease | CaseData.reportDate | CaseData.responsibleRegion | CaseData.responsibleDistrict "
		+ "| CaseData.facilityType | CaseData.healthFacility | Person.person.firstName | Person.person.lastName | Person.person.sex "
		+ "| Person.person.birthdateDD | Person.person.birthdateMM | Person.person.birthdateYYYY | Sample.fieldSampleID | Sample.sampleDateTime "
		+ "| Sample.sampleMaterial | Sample.lab |";

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
	public void oneSimilarCaseIsAttachedBySimilarity() {
		CaseDataDto caze = caseFor(jeanMuller(), Disease.TUBERCULOSIS);
		ImportSheet sheet = SheetFixtures.table(HEADER, row("Jean", "Muller"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);

		assertEquals(MatchReason.BY_SIMILARITY, root(preview).getMatchReason(), preview.allIssues().toString());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());
		assertEquals(1, getSampleFacade().getByCaseUuids(List.of(caze.getUuid())).size());
	}

	@Test
	public void twoSimilarCasesFailTheBlock() {
		PersonDto person = jeanMuller();
		caseFor(person, Disease.TUBERCULOSIS);
		caseFor(person, Disease.TUBERCULOSIS);

		assertEquals(1, facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(HEADER, row("Jean", "Muller"))).countBlocks(BlockStatus.ERROR));
	}

	@Test
	public void oneSimilarPersonWithoutASimilarCaseGetsANewCase() {
		PersonDto person = jeanMuller();
		caseFor(person, Disease.MEASLES);
		ImportSheet sheet = SheetFixtures.table(HEADER, row("Jean", "Muller"));

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(MatchReason.NEW_FOR_EXISTING_PERSON, root(preview).getMatchReason(), preview.allIssues().toString());
		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(2, getCaseFacade().getByPersonUuids(List.of(person.getUuid())).size());
		assertEquals(2, getCaseFacade().getAllActiveUuids().size());
	}

	@Test
	public void personCellsWrittenOntoAnExistingPersonAreShownBeforeSaving() {
		PersonDto person = jeanMuller();
		caseFor(person, Disease.MEASLES);
		ImportSheet sheet = SheetFixtures.table(HEADER.replace("| Person.person.sex |", "| Person.person.sex | Person.person.nickname |"),
			row("Jean", "Muller").replace("| MALE |", "| MALE | JJ |"));

		RecordOperationDto root = root(facade.checkImport(CASE_TEMPLATE, sheet));

		assertEquals(MatchReason.NEW_FOR_EXISTING_PERSON, root.getMatchReason());
		assertEquals(1, root.getChanges().size(), root.getChanges().toString());
		assertEquals("Person.person.nickname", root.getChanges().get(0).getField());
		assertEquals("", root.getChanges().get(0).getOldValue());
		assertEquals("JJ", root.getChanges().get(0).getNewValue());
	}

	@Test
	public void aPersonAppearingAfterThePreviewLeavesTheBlockAlone() {
		ImportSheet sheet = SheetFixtures.table(HEADER, row("Jean", "Muller"));
		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(MatchReason.NEW, root(preview).getMatchReason());
		caseFor(jeanMuller(), Disease.MEASLES);

		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.CHANGED_SINCE_PREVIEW, report.getResults().get(0).getOutcome());
	}

	@Test
	public void anExistingPersonEditedAfterThePreviewLeavesTheBlockAlone() throws InterruptedException {
		PersonDto person = jeanMuller();
		caseFor(person, Disease.MEASLES);
		ImportSheet sheet = SheetFixtures.table(HEADER, row("Jean", "Muller"));
		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		assertEquals(MatchReason.NEW_FOR_EXISTING_PERSON, root(preview).getMatchReason());
		Thread.sleep(1);
		PersonDto stored = getPersonFacade().getByUuid(person.getUuid());
		stored.setNickname("JJ");
		getPersonFacade().save(stored);

		ImportReportDto report = facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		assertEquals(BlockOutcome.CHANGED_SINCE_PREVIEW, report.getResults().get(0).getOutcome());
	}

	@Test
	public void twoSimilarPersonsFailTheBlock() {
		caseFor(jeanMuller(), Disease.MEASLES);
		caseFor(jeanMuller(), Disease.MEASLES);

		assertEquals(1, facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(HEADER, row("Jean", "Muller"))).countBlocks(BlockStatus.ERROR));
	}

	@Test
	public void nothingSimilarCreatesCaseAndPerson() {
		caseFor(jeanMuller(), Disease.TUBERCULOSIS);

		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, SheetFixtures.table(HEADER, row("Ana", "Novak")));

		assertEquals(MatchReason.NEW, root(preview).getMatchReason());
	}

	@Test
	public void aBlockWithAnIdNeverRunsSimilarity() {
		caseFor(jeanMuller(), Disease.MEASLES);
		caseFor(jeanMuller(), Disease.MEASLES);
		creator.createCase(nationalAdmin.toReference(), rdcf, c -> c.setEpidNumber("AAA-BBB-CCC-26-009"));

		ImportPreviewDto preview = facade.checkImport(
			CASE_TEMPLATE,
			SheetFixtures.table("| CaseData.epidNumber | Person.person.firstName | Person.person.lastName |", "| AAA-BBB-CCC-26-009 | Jean | Muller |"));

		assertEquals(MatchReason.BY_ID, root(preview).getMatchReason());
	}

	private PersonDto jeanMuller() {
		return creator.createPerson("Jean", "Muller", Sex.MALE, 1980, 5, 4);
	}

	private CaseDataDto caseFor(PersonDto person, Disease disease) {
		return creator.createCase(
			nationalAdmin.toReference(),
			person.toReference(),
			disease,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			reportDate(),
			rdcf);
	}

	private static Date reportDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2026, Calendar.SEPTEMBER, 1);
		return calendar.getTime();
	}

	private static String row(String first, String last) {
		return "| TUBERCULOSIS | 9/1/2026 | Region | District | HOSPITAL | Facility | " + first + " | " + last
			+ " | MALE | 4 | 5 | 1980 | S1 | 9/2/2026 | BLOOD | Central Lab |";
	}

	private static RecordOperationDto root(ImportPreviewDto preview) {
		return preview.getBlocks().get(0).getOperations().get(0);
	}
}
