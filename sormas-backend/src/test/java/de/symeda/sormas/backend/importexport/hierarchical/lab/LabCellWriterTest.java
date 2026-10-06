package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	public void rejectsAnUnknownLaboratory() {
		assertThrows(
			CellWriteException.class,
			() -> writer.write(targets, LabImportSchema.SAMPLE, new String[] { SampleDto.LAB }, "sample.lab", "Nowhere Lab"));
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
