package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.util.DateHelper8;
import info.novatec.beantest.api.BaseBeanTest;

public class CaseFacadeEjbTest extends BaseBeanTest {
	
	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	@Test
	public void testDiseaseChangeUpdatesContacts() {
		
		// TODO create provider for facades (probably best to add a SormasBeanTest class)
		UserFacade userFacade = getBean(UserFacadeEjbLocal.class);
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		PersonFacade personFacade = getBean(PersonFacadeEjb.class);
		ContactFacade contactFacade = getBean(ContactFacadeEjb.class);
		
		// TODO handle user creation at a central place
		UserDto user = new UserDto();
		user.setUuid(DataHelper.createUuid());
		user.setFirstName("Admin");
		user.setLastName("Symeda");
		user.setUserName("AdminSymeda");
		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(UserRole.SURVEILLANCE_SUPERVISOR)));
		user = userFacade.saveUser(user);
		
		// TODO add create method to PersonFacade
		PersonDto cazePerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		cazePerson.setFirstName("Tim");
		cazePerson.setLastName("Kunsen");
		cazePerson = personFacade.savePerson(cazePerson);
		
		// TODO add create method to CaseFacade that takes a person
		CaseDataDto caze = new CaseDataDto();
		caze.setPerson(cazePerson);
		caze.setReportDate(new Date());
		caze.setReportingUser(user);
		caze.setDisease(Disease.EVD);
		caze.setCaseClassification(CaseClassification.PROBABLE);
		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze = caseFacade.saveCase(caze);
		
		PersonDto contactPerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		contactPerson.setFirstName("Steff");
		contactPerson.setLastName("Hansen");
		contactPerson = personFacade.savePerson(contactPerson);
		
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());
		contact.setReportDateTime(new Date());
		contact.setReportingUser(user);
		contact.setContactOfficer(user);
		contact.setPerson(contactPerson);
		contact.setCaze(caze);
		contact.setLastContactDate(new Date());
		contact = contactFacade.saveContact(contact);
		
		// Follow-up status and duration should be set to the requirements for EVD
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		caze.setDisease(Disease.MEASLES);
		caze = caseFacade.saveCase(caze);
		
		// Follow-up status and duration should be set to no follow-up and null respectively because
		// Measles does not require a follow-up
		contact = contactFacade.getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.NO_FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(null, contact.getFollowUpUntil());
	}

}
