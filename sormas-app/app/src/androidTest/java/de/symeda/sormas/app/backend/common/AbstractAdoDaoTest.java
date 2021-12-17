package de.symeda.sormas.app.backend.common;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.app.TestBackendActivity;
import de.symeda.sormas.app.TestEntityCreator;
import de.symeda.sormas.app.TestHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.visit.Visit;

import junit.framework.TestCase;

@RunWith(AndroidJUnit4.class)
public class AbstractAdoDaoTest extends TestCase {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void testQueryForNew() throws DaoException {

		CaseDao caseDao = DatabaseHelper.getCaseDao();

		// Case with change date 0 should be included
		Case caseChangeDate0 = TestEntityCreator.createCase();
		List<Case> newCases = caseDao.queryForNew();
		assertEquals(1, newCases.size());

		// Case with newer change date should not be included
		Case caseNewChangeDate = TestEntityCreator.createCase();
		caseNewChangeDate.setChangeDate(new Date());
		caseDao.saveAndSnapshot(caseNewChangeDate);
		newCases = caseDao.queryForNew();
		assertEquals(1, newCases.size());

		// Additional tests for other entities
		Contact contact = TestEntityCreator.createContact(caseChangeDate0);
		Visit visit = TestEntityCreator.createVisit(contact);
		Sample sample = TestEntityCreator.createSample(caseChangeDate0);
		Event event = TestEntityCreator.createEvent();
		EventParticipant eventParticipant = TestEntityCreator.createEventParticipant(event);

		assertEquals(1, DatabaseHelper.getContactDao().queryForNew().size());
		assertEquals(1, DatabaseHelper.getVisitDao().queryForNew().size());
		assertEquals(1, DatabaseHelper.getSampleDao().queryForNew().size());
		assertEquals(1, DatabaseHelper.getEventDao().queryForNew().size());
		assertEquals(1, DatabaseHelper.getEventParticipantDao().queryForNew().size());
	}

	@Test
	public void testQueryForModified() throws DaoException {

		CaseDao caseDao = DatabaseHelper.getCaseDao();

		// Modified cases with change date 0 should be included
		Case caseChangeDate0 = TestEntityCreator.createCase();
		// Modification is necessary because TestEntityCreator automatically accepts the case
		caseChangeDate0.setAdditionalDetails("...");
		caseDao.saveAndSnapshot(caseChangeDate0);
		List<Case> newCases = caseDao.queryForModified();
		assertEquals(1, newCases.size());

		// Modified cases with newer change date should also be included
		Case caseNewChangeDate = TestEntityCreator.createCase();
		caseNewChangeDate.setChangeDate(new Date());
		caseDao.saveAndSnapshot(caseNewChangeDate);
		newCases = caseDao.queryForModified();
		assertEquals(2, newCases.size());
	}
}
