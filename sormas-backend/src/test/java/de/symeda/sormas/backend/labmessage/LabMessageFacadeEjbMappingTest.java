package de.symeda.sormas.backend.labmessage;

import java.sql.Timestamp;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import junit.framework.TestCase;

public class LabMessageFacadeEjbMappingTest extends TestCase {

	public void testFromDto() {
		LabMessageFacadeEjb sut = new LabMessageFacadeEjb();

		LabMessageDto source = new LabMessageDto();

		source.setCreationDate(new Date());
		source.setChangeDate(new Date());
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setSampleDateTime(new Date());
		source.setSampleReceivedDate(new Date());
		source.setLabSampleId("Lab Sample Id");
		source.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalId("Test Lab External Id");
		source.setTestLabPostalCode("Test Lab Postal Code");
		source.setTestLabCity("Test Lab City");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setTestType(PathogenTestType.PCR_RT_PCR);
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setTestDateTime(new Date());
		source.setTestResult(PathogenTestResultType.NEGATIVE);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setLabMessageDetails("Lab Message Details");

		LabMessage result = sut.fromDto(source, null, true);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertNotSame(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalId(), result.getTestLabExternalId());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getLabMessageDetails(), result.getLabMessageDetails());

	}

	public void testToDto() {

		LabMessageFacadeEjb sut = new LabMessageFacadeEjb();

		LabMessage source = new LabMessage();

		source.setCreationDate(new Timestamp(new Date().getTime()));
		source.setChangeDate(new Timestamp(new Date().getTime()));
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setSampleDateTime(new Date());
		source.setSampleReceivedDate(new Date());
		source.setLabSampleId("Lab Sample Id");
		source.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalId("Test Lab External Id");
		source.setTestLabPostalCode("Test Lab Postal Code");
		source.setTestLabCity("Test Lab City");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setTestType(PathogenTestType.PCR_RT_PCR);
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setTestDateTime(new Date());
		source.setTestResult(PathogenTestResultType.NEGATIVE);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setLabMessageDetails("Lab Message Details");

		LabMessageDto result = sut.toDto(source);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertEquals(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalId(), result.getTestLabExternalId());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getLabMessageDetails(), result.getLabMessageDetails());
	}

}
