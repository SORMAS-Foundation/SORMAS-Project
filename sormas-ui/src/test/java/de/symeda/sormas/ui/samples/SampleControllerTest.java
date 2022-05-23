package de.symeda.sormas.ui.samples;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;

public class SampleControllerTest extends AbstractBeanTest {

	@Test
	public void testGetDiseaseOf() {

		// basic setup
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto person = creator.createPerson();
		SampleController sut = new SampleController();

		// case
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		assertThat(sut.getDiseaseOf(sample), equalTo(Disease.EVD));

		// contact
		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS, rdcf);
		sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility.toReference(), null);

		assertThat(sut.getDiseaseOf(sample), equalTo(Disease.CORONAVIRUS));

		// event participant
		EventDto event = creator.createEvent(user.toReference(), Disease.CHOLERA);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		sample = creator.createSample(
			eventParticipant.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility.toReference());

		assertThat(sut.getDiseaseOf(sample), equalTo(Disease.CHOLERA));
	}
}
