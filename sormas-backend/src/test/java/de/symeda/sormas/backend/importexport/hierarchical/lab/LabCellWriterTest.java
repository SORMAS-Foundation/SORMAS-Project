package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriteException;

public class LabCellWriterTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private LabCellWriter writer;
	private Map<String, Object> targets;

	@Override
	public void init() {
		super.init();
		useNationalAdminLogin();
		rdcf = creator.createRDCF();
		creator.createFacility("Central Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);
		writer = getBean(LabCellWriter.class);
		PersonDto person = PersonDto.buildImportEntity();
		targets = new HashMap<>();
		targets.put(LabImportSchema.PERSON, person);
		targets.put(LabImportSchema.CASE, CaseDataDto.build(person.toReference(), null));
		targets.put(LabImportSchema.SAMPLE, SampleDto.build(nationalAdmin.toReference(), (de.symeda.sormas.api.caze.CaseReferenceDto) null));
	}

	@Test
	public void writesEnumsByName() throws CellWriteException {
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.DISEASE }, "case.disease", "TUBERCULOSIS");

		assertEquals(Disease.TUBERCULOSIS, caze().getDisease());
	}

	@Test
	public void resolvesTheDistrictInsideTheCaseRegion() throws CellWriteException {
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.RESPONSIBLE_REGION }, "case.responsibleRegion", "Region");
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.RESPONSIBLE_DISTRICT }, "case.responsibleDistrict", "District");

		assertEquals(rdcf.district, caze().getResponsibleDistrict());
	}

	@Test
	public void resolvesTheHealthFacilityByDistrictAndType() throws CellWriteException {
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.RESPONSIBLE_REGION }, "case.responsibleRegion", "Region");
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.RESPONSIBLE_DISTRICT }, "case.responsibleDistrict", "District");
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.FACILITY_TYPE }, "case.facilityType", "HOSPITAL");
		writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.HEALTH_FACILITY }, "case.healthFacility", "Facility");

		assertEquals(rdcf.facility, caze().getHealthFacility());
	}

	@Test
	public void resolvesALaboratoryByName() throws CellWriteException {
		writer.write(targets, LabImportSchema.SAMPLE, new String[] { SampleDto.LAB }, "sample.lab", "Central Lab");

		assertEquals("Central Lab", ((SampleDto) targets.get(LabImportSchema.SAMPLE)).getLab().getCaption());
	}

	@Test
	public void keepsTheTimeOfADateTimeCell() throws CellWriteException {
		writer.write(targets, LabImportSchema.SAMPLE, new String[] { SampleDto.SAMPLE_DATE_TIME }, "sample.sampleDateTime", "9/2/2026 2:30 PM");

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(((SampleDto) targets.get(LabImportSchema.SAMPLE)).getSampleDateTime());
		assertEquals(14, calendar.get(java.util.Calendar.HOUR_OF_DAY));
		assertEquals(30, calendar.get(java.util.Calendar.MINUTE));
	}

	@Test
	public void rejectsAnUnknownLaboratoryWithAPlainSentence() {
		CellWriteException error = assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.SAMPLE, new String[] { SampleDto.LAB }, "sample.lab", "Nowhere Lab"));

		assertEquals("Invalid value Nowhere Lab in column sample.lab. Entry does not exist in the database or is archived", error.getMessage());
	}

	@Test
	public void namesTheFileColumnWhenASharedParserRejectsAValue() {
		CellWriteException region = assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.RESPONSIBLE_REGION }, "case.responsibleRegion", "Atlantis"));
		CellWriteException disease = assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.DISEASE }, "case.disease", "FLU-X"));

		assertTrue(region.getMessage().contains("column case.responsibleRegion"), region.getMessage());
		assertTrue(disease.getMessage().contains("column case.disease"), disease.getMessage());
	}

	@Test
	public void reportsAnInvalidDateWithoutASemicolon() {
		CellWriteException error = assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.REPORT_DATE }, "case.reportDate", "31/31/2026"));

		assertFalse(error.getMessage().contains(";"), error.getMessage());
	}

	@Test
	public void rejectsAnInvalidDate() {
		assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.CASE, new String[] { CaseDataDto.REPORT_DATE }, "case.reportDate", "31/31/2026"));
	}

	private CaseDataDto caze() {
		return (CaseDataDto) targets.get(LabImportSchema.CASE);
	}
}
