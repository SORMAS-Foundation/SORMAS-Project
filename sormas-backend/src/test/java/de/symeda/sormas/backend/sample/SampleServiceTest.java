package de.symeda.sormas.backend.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * @see SampleService
 */
public class SampleServiceTest extends AbstractBeanTest {

	@Test
	public void testSamplePermanentDeletion() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referralSample =
			creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> s.setReferredTo(sample.toReference()));
		creator.createPathogenTest(sample.toReference(), caze);
		creator.createAdditionalTest(sample.toReference());
		LabMessageDto labMessage = creator.createLabMessage(lm -> lm.setSample(sample.toReference()));

		getSampleFacade().deleteSample(sample.toReference());

		Sample sampleEntity = getSampleService().getByUuid(sample.getUuid());
		List<PathogenTest> pathogenTests = getPathogenTestService().getAll();
		assertEquals(2, getSampleService().count());
		assertTrue(sampleEntity.isDeleted());
		assertEquals(1, pathogenTests.size());
		assertTrue(pathogenTests.get(0).isDeleted());
		assertEquals(1, getAdditionalTestService().count());
		assertNull(getSampleService().getByUuid(referralSample.getUuid()).getReferredTo());
		assertNull(getLabMessageService().getByUuid(labMessage.getUuid()).getSample());

		getSampleService().deletePermanent(getEntityAttached(sampleEntity));

		assertEquals(1, getSampleService().count());
		assertEquals(0, getPathogenTestService().count());
		assertEquals(0, getAdditionalTestService().count());
		assertEquals(1, getLabMessageService().count());
	}
}
