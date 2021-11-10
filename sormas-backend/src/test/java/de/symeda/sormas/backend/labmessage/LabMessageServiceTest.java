package de.symeda.sormas.backend.labmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class LabMessageServiceTest extends AbstractBeanTest {

	@Test
	public void testGetForSample() throws InterruptedException {

		LabMessageService sut = getLabMessageService();

		// Generally, all objects named ...1. are related to lab messages that shall be returned.

		// Test with one result
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		Date sampleDate = new Date(1624952153848L);
		SampleDto sample1 = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleReferenceDto sampleReference1 = sample1.toReference();

		LabMessageDto labMessage1a = creator.createLabMessage(null);
		labMessage1a.setSample(sampleReference1);
		labMessage1a.setCreationDate(new Date(4L));
		labMessage1a.setChangeDate(new Date());
		getLabMessageFacade().save(labMessage1a);

		LabMessageDto labMessage1Deleted = creator.createLabMessage(null);
		labMessage1Deleted.setSample(sampleReference1);
		labMessage1Deleted.setChangeDate(new Date());
		getLabMessageFacade().save(labMessage1Deleted);
		getLabMessageFacade().deleteLabMessage(labMessage1Deleted.getUuid());

		SampleDto sample2 = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleReferenceDto sampleReference2 = sample2.toReference();

		LabMessageDto labMessage2 = creator.createLabMessage(null);
		labMessage2.setSample(sampleReference2);
		labMessage2.setChangeDate(new Date());
		getLabMessageFacade().save(labMessage2);

		List<LabMessage> result = sut.getForSample(sampleReference1);
		assertThat(result, Matchers.hasSize(1));
		assertEquals(labMessage1a.getUuid(), result.get(0).getUuid());

		// Test with multiple results
		Thread.sleep(1); // delay to ignore rounding issue with sorting
		LabMessageDto labMessage1b = creator.createLabMessage(null);
		labMessage1b.setSample(sampleReference1);
		labMessage1b.setCreationDate(new Date(1L));
		labMessage1b.setChangeDate(new Date());
		getLabMessageFacade().save(labMessage1b);

		Thread.sleep(1);
		LabMessageDto labMessage1c = creator.createLabMessage(null);
		labMessage1c.setSample(sampleReference1);
		labMessage1c.setCreationDate(new Date(2L));
		labMessage1c.setChangeDate(new Date());
		getLabMessageFacade().save(labMessage1c);

		result = sut.getForSample(sampleReference1);
		assertThat(result, Matchers.hasSize(3));
		assertEquals(labMessage1a.getUuid(), result.get(2).getUuid());
		assertEquals(labMessage1b.getUuid(), result.get(1).getUuid());
		assertEquals(labMessage1c.getUuid(), result.get(0).getUuid());

	}
}
