package de.symeda.sormas.backend.visit;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.person.Person;

public class VisitServiceTest extends AbstractBeanTest {

	@Test
	public void testGetAllRelevantVisits() {

		Date startDate = DateHelper.subtractDays(new Date(), ContactLogic.ALLOWED_CONTACT_DATE_OFFSET * 2);
		Date endDate = DateHelper.addDays(startDate, 28);
		PersonDto visitPerson = creator.createPerson();
		Person visitPersonEntity = getPersonService().getByUuid(visitPerson.getUuid());

		// Visits with a visit date before the start date should not be included
		VisitDto visit = creator.createVisit(Disease.EVD, visitPerson.toReference());
		visit.setVisitDateTime(DateHelper.subtractDays(startDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getVisitFacade().saveVisit(visit);

		Set<Visit> visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, empty());

		// Visits with a visit date before the start date but within the offset should be included
		visit.setVisitDateTime(DateHelper.subtractDays(startDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		getVisitFacade().saveVisit(visit);

		visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, hasSize(1));

		// Visits with a visit date after the end date should not be included
		visit.setVisitDateTime(DateHelper.addDays(endDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getVisitFacade().saveVisit(visit);

		visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, empty());

		// Visits with a visit date after the end date but within the offset should be included
		visit.setVisitDateTime(DateHelper.addDays(endDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		getVisitFacade().saveVisit(visit);

		visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, hasSize(1));

		// Visits with a visit date between the start and end date should be included
		visit.setVisitDateTime(DateHelper.addDays(startDate, 14));
		getVisitFacade().saveVisit(visit);

		visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, hasSize(1));

		// Visits with a different person and/or disease should not be included
		PersonDto visitPerson2 = creator.createPerson();
		VisitDto visit2 = creator.createVisit(Disease.EVD, visitPerson2.toReference());
		VisitDto visit3 = creator.createVisit(Disease.CSM, visitPerson.toReference());
		visit2.setVisitDateTime(DateHelper.addDays(startDate, 14));
		visit3.setVisitDateTime(DateHelper.addDays(startDate, 14));
		getVisitFacade().saveVisit(visit2);
		getVisitFacade().saveVisit(visit3);

		visits = getVisitService().getAllRelevantVisits(visitPersonEntity, visit.getDisease(), startDate, endDate);
		assertThat(visits, hasSize(1));
	}
}
