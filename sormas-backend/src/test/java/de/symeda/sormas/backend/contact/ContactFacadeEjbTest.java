package de.symeda.sormas.backend.contact;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.lang3.time.DateUtils;
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
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import info.novatec.beantest.api.BaseBeanTest;

public class ContactFacadeEjbTest extends BaseBeanTest  {

	/**
	 * Setzt die Mocks auf den initialen Stand zurück, damit Mock-Konfigurationen nicht zwischen Tests geteilt werden.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	@Test
	public void testUpdateFollowUpUntil() {
 
		// TODO create provider for facades (probably best to add a SormasBeanTest class)
		UserFacade userFacade = getBean(UserFacadeEjbLocal.class);
		ContactFacade contactFacade = getBean(ContactFacadeEjb.class);
		PersonFacade personFacade = getBean(PersonFacadeEjb.class);
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		VisitFacade visitFacade = getBean(VisitFacadeEjb.class);
		
		// TODO handle user creation at a central place
    	UserDto user = new UserDto();
    	user.setUuid(DataHelper.createUuid());
		user.setFirstName("Admin");
		user.setLastName("Symeda");
		user.setUserName("AdminSymeda");
		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(UserRole.SURVEILLANCE_SUPERVISOR)));
		user = userFacade.saveUser(user);
		
		// TODO add create method to PersonFacde
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

		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		// TODO VisitController.createNewVisit should be moved to the facade or to a helper
		VisitDto visit = new VisitDto();
		visit.setDisease(caze.getDisease());
		visit.setPerson(contactPerson);
		visit.setVisitDateTime(DateUtils.addDays(new Date(), 21));
		visit.setVisitStatus(VisitStatus.UNAVAILABLE);
		visit = visitFacade.saveVisit(visit);

		// should now be one day more
		contact = contactFacade.getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21+1), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		visit.setVisitStatus(VisitStatus.COOPERATIVE);
		visit = visitFacade.saveVisit(visit);
		
		// and now the old date again - and done
		contact = contactFacade.getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.COMPLETED, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
	}
}
