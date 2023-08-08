package de.symeda.sormas.backend.externalmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ExternalMessageServiceTest extends AbstractBeanTest {

	@Test
	public void testGetForSample() throws InterruptedException {

		ExternalMessageService sut = getExternalMessageService();

		// Generally, all objects named ...1. are related to lab messages that shall be returned.

		// Test with one result
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		Date sampleDate = new Date(1624952153848L);
		SampleDto sample1 = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleReferenceDto sampleReference1 = sample1.toReference();

		ExternalMessageDto labMessage1a = creator.createLabMessageWithTestReport(sampleReference1);
		labMessage1a.setCreationDate(new Date(4L));
		labMessage1a.setChangeDate(new Date());
		getExternalMessageFacade().save(labMessage1a);

		ExternalMessageDto labMessage1Deleted = creator.createLabMessageWithTestReport(sampleReference1);
		labMessage1Deleted.setChangeDate(new Date());
		getExternalMessageFacade().save(labMessage1Deleted);
		getExternalMessageFacade().delete(labMessage1Deleted.getUuid());

		SampleDto sample2 = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleReferenceDto sampleReference2 = sample2.toReference();

		ExternalMessageDto labMessage2 = creator.createLabMessageWithTestReport(sampleReference2);
		labMessage2.setChangeDate(new Date());
		getExternalMessageFacade().save(labMessage2);

		List<ExternalMessage> result = sut.getForSample(sampleReference1);
		assertThat(result, Matchers.hasSize(1));
		assertEquals(labMessage1a.getUuid(), result.get(0).getUuid());

		// Test with multiple results
		Thread.sleep(1); // delay to ignore rounding issue with sorting
		ExternalMessageDto labMessage1b = creator.createLabMessageWithTestReport(sampleReference1);
		labMessage1b.setCreationDate(new Date(1L));
		labMessage1b.setChangeDate(new Date());
		getExternalMessageFacade().save(labMessage1b);

		Thread.sleep(1);
		ExternalMessageDto labMessage1c = creator.createLabMessageWithTestReport(sampleReference1);
		labMessage1c.setCreationDate(new Date(2L));
		labMessage1c.setChangeDate(new Date());
		getExternalMessageFacade().save(labMessage1c);

		result = sut.getForSample(sampleReference1);
		assertThat(result, Matchers.hasSize(3));
		assertEquals(labMessage1a.getUuid(), result.get(2).getUuid());
		assertEquals(labMessage1b.getUuid(), result.get(1).getUuid());
		assertEquals(labMessage1c.getUuid(), result.get(0).getUuid());
	}

	@Test
	public void testCountForCase() {

		ExternalMessageService sut = getExternalMessageService();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		// create noise
		CaseDataDto noiseCaze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createSample(noiseCaze.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		assertEquals(0L, sut.countForCase(caze.getUuid()));

		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		assertEquals(1L, sut.countForCase(caze.getUuid()));

		// create additional lab message matches
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample2.toReference());
		assertEquals(3L, sut.countForCase(caze.getUuid()));
		assertEquals(0L, sut.countForContact(caze.getUuid()));
		assertEquals(0L, sut.countForEventParticipant(caze.getUuid()));
	}

	@Test
	public void testCountForContact() {

		ExternalMessageService sut = getExternalMessageService();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		// create noise
		ContactDto noiseContact = creator.createContact(user.toReference(), person.toReference());
		creator.createSample(noiseContact.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		assertEquals(0L, sut.countForContact(contact.getUuid()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertEquals(1L, sut.countForContact(contact.getUuid()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertEquals(3L, sut.countForContact(contact.getUuid()));
		assertEquals(0L, sut.countForCase(contact.getUuid()));
		assertEquals(0L, sut.countForEventParticipant(contact.getUuid()));
	}

	@Test
	public void testCountForEventParticipant() {

		ExternalMessageService sut = getExternalMessageService();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		// create noise
		EventParticipantDto noiseEventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		creator.createSample(noiseEventParticipant.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		assertEquals(0L, sut.countForEventParticipant(eventParticipant.getUuid()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertEquals(1L, sut.countForEventParticipant(eventParticipant.getUuid()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertEquals(3L, sut.countForEventParticipant(eventParticipant.getUuid()));
		assertEquals(0L, sut.countForContact(eventParticipant.getUuid()));
		assertEquals(0L, sut.countForCase(eventParticipant.getUuid()));
	}

	@Test
	public void testLabMessagePermanentDeletion() {

		ExternalMessageDto labMessage = creator.createLabMessageWithTestReport(null);

		getExternalMessageFacade().delete(labMessage.getUuid());

		assertEquals(0, getExternalMessageService().count());
		assertEquals(0, getSampleReportService().count());
		assertEquals(0, getTestReportService().count());
	}
}
