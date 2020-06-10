package de.symeda.sormas.backend.visit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.contact.Contact;

/**
 * The class VisitFacadeEjbTest.
 */
public class VisitFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testCreateExternalVisit() {

		TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ext", "Vis", UserRole.REST_EXTERNAL_VISITS_USER);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		final ExternalVisitDto externalVisitDto = new ExternalVisitDto();
		externalVisitDto.setPersonUuid(contactPerson.getUuid());
		externalVisitDto.setDisease(contact.getDisease());
		externalVisitDto.setVisitDateTime(new Date());
		externalVisitDto.setVisitStatus(VisitStatus.COOPERATIVE);
		final String visitRemarks = "Everything good";
		externalVisitDto.setVisitRemarks(visitRemarks);

		final VisitFacade visitFacade = getVisitFacade();
		visitFacade.saveExternalVisit(externalVisitDto);

		final VisitCriteria visitCriteria = new VisitCriteria();
		final List<VisitIndexDto> visitIndexList =
			visitFacade.getIndexList(visitCriteria.contact(new ContactReferenceDto(contact.getUuid())), 0, 100, null);
		assertNotNull(visitIndexList);
		assertEquals(1, visitIndexList.size());
		VisitIndexDto visitIndexDto = visitIndexList.get(0);
		assertNotNull(visitIndexDto.getVisitDateTime());
		assertEquals(VisitStatus.COOPERATIVE, visitIndexDto.getVisitStatus());
		assertEquals(visitRemarks, visitIndexDto.getVisitRemarks());
	}

	@Test
	public void testExportVisit() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit);
		VisitDto visit2 = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
		visit2.getSymptoms().setAgitation(SymptomState.YES);
		getVisitFacade().saveVisit(visit2);

		final ContactReferenceDto contactReferenceDto = new ContactReferenceDto(contact.getUuid());
		final VisitCriteria visitCriteria = new VisitCriteria();
		visitCriteria.contact(contactReferenceDto);
		final List<VisitExportDto> visitsExportList =
			getVisitFacade().getVisitsExportList(visitCriteria, VisitExportType.CONTACT_VISITS, 0, 10, null);

		assertNotNull(visitsExportList);
		assertEquals(2, visitsExportList.size());

		final VisitExportDto visitExportDto1 = visitsExportList.get(1);
		assertEquals(visit.getUuid(), visitExportDto1.getUuid());
		assertEquals("Contact", visitExportDto1.getFirstName());
		assertEquals("Person", visitExportDto1.getLastName());
		assertEquals("EVD", visitExportDto1.getDiseaseFormatted());
		assertEquals(VisitStatus.COOPERATIVE, visitExportDto1.getVisitStatus());
		assertEquals(SymptomState.YES, visitExportDto1.getSymptoms().getAbdominalPain());

		final VisitExportDto visitExportDto2 = visitsExportList.get(0);
		assertEquals(visit2.getUuid(), visitExportDto2.getUuid());
		assertEquals("Contact", visitExportDto2.getFirstName());
		assertEquals("Person", visitExportDto2.getLastName());
		assertEquals("EVD", visitExportDto2.getDiseaseFormatted());
		assertEquals(VisitStatus.COOPERATIVE, visitExportDto2.getVisitStatus());
		assertEquals(SymptomState.YES, visitExportDto2.getSymptoms().getAgitation());
	}

	@Test
	public void testUpdateContactVisitAssociations() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		VisitDto visit = creator.createVisit(contact.getDisease(), person.toReference());
		Visit visitEntity = getVisitService().getByUuid(visit.getUuid());
		Contact contactEntity = getContactService().getByUuid(contact.getUuid());

		// Saved visit should have contact association
		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(1));

		// Updating the visit but not changing the visit date time should not alter the contact associations
		visit.setVisitStatus(VisitStatus.UNCOOPERATIVE);
		getVisitFacade().saveVisit(visit);

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(1));

		// Changing the visit date time to a value beyond the threshold should remove the contact association
		visit.setVisitDateTime(DateHelper.addDays(contact.getFollowUpUntil(), ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getVisitFacade().saveVisit(visit);

		assertThat(getContactService().getAllByVisit(visitEntity), empty());

		// Changing the visit date time back to a value in the threshold should re-add the contact association
		visit.setVisitDateTime(new Date());
		getVisitFacade().saveVisit(visit);

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(1));

		// Adding another visit that matches the contact person, disease and time frame should increase the collection size
		creator.createVisit(contact.getDisease(), person.toReference());

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(2));

		// Adding another visit with the same person and disease, but an incompatible time frame should not increase the collection size
		creator.createVisit(
			contact.getDisease(),
			person.toReference(),
			DateHelper.subtractDays(contact.getReportDateTime(), ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(2));

		// Adding another visit that is compatible to the time frame, but has a different person and/or disease should not increase the collection size
		PersonDto person2 = creator.createPerson();
		creator.createVisit(contact.getDisease(), person2.toReference());
		creator.createVisit(Disease.CSM, person.toReference());

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(2));
	}

	@Test
	public void testGetAllActiveUuids() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto person = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createContact(user.toReference(), person.toReference());
		ContactDto deletedContact = creator.createContact(user.toReference(), person2.toReference());
		getContactFacade().deleteContact(deletedContact.getUuid());

		creator.createVisit(person.toReference());
		creator.createVisit(person.toReference());
		VisitDto visitOfDeletedContact = creator.createVisit(person2.toReference());

		List<String> visitUuids = getVisitFacade().getAllActiveUuids();
		assertThat(visitUuids, hasSize(2));
		assertThat(visitUuids, not(contains(visitOfDeletedContact.getUuid())));
	}

	@Test
	public void testGetAllActiveVisitsAfter() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		Date date = new Date();

		PersonDto person = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createContact(user.toReference(), person.toReference());
		ContactDto deletedContact = creator.createContact(user.toReference(), person2.toReference());
		getContactFacade().deleteContact(deletedContact.getUuid());

		creator.createVisit(person.toReference());
		VisitDto visitWithChanges = creator.createVisit(person.toReference());
		VisitDto visitOfDeletedContact = creator.createVisit(person2.toReference());

		List<VisitDto> visits = getVisitFacade().getAllActiveVisitsAfter(date);
		assertThat(visits, hasSize(2));
		assertThat(visits, not(contains(visitOfDeletedContact)));

		date = new Date();

		visitWithChanges.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visitWithChanges);

		visits = getVisitFacade().getAllActiveVisitsAfter(date);
		assertThat(visits, hasSize(1));
		assertThat(visits, contains(visitWithChanges));
	}

	@Test
	public void testGetLastVisitByContact() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		VisitDto visit = creator.createVisit(Disease.EVD, person.toReference(), new Date());
		creator.createVisit(Disease.EVD, person.toReference(), DateHelper.subtractDays(new Date(), 1));

		assertThat(getVisitFacade().getLastVisitByContact(contact.toReference()), is(visit));
	}

	@Test
	public void testGetIndexList() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto person = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		creator.createContact(user.toReference(), person2.toReference());

		creator.createVisit(person.toReference());
		creator.createVisit(person.toReference());
		VisitDto otherVisit = creator.createVisit(person2.toReference());

		List<VisitIndexDto> indexVisits = getVisitFacade().getIndexList(new VisitCriteria().contact(contact.toReference()), null, null, null);

		assertThat(indexVisits, hasSize(2));
		assertThat(indexVisits.stream().map(v -> v.getUuid()).collect(Collectors.toList()), not(contains(otherVisit.getUuid())));
	}
}
