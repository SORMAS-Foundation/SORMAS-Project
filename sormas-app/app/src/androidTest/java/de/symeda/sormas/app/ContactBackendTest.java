package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class ContactBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment();
    }

    @Test
    public void shouldCreateContact() {
        TestHelper.initTestEnvironment();

        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(0));
        TestEntityCreator.createContact();

        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateVisit() {
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(0));

        Contact contact = TestEntityCreator.createContact();
        TestEntityCreator.createVisit(contact);

        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(1));
    }

    /**
     * This tests merging of contacts and visits.
     */
    @Test
    public void shouldMergeAsExpected() throws DaoException {
        Contact contact = TestEntityCreator.createContact();
        Visit visit = TestEntityCreator.createVisit(contact);

        contact.setDescription("AppDescription");
        visit.setVisitStatus(VisitStatus.UNCOOPERATIVE);

        DatabaseHelper.getContactDao().saveAndSnapshot(contact);
        DatabaseHelper.getContactDao().accept(contact);
        DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
        DatabaseHelper.getVisitDao().accept(visit);

        Contact mergeContact = (Contact) contact.clone();
        mergeContact.setPerson((Person) contact.getPerson().clone());
        mergeContact.setCaze((Case) contact.getCaze().clone());
        mergeContact.setId(null);
        mergeContact.getPerson().setId(null);
        mergeContact.getPerson().getAddress().setId(null);
        mergeContact.getCaze().setId(null);

        mergeContact.setDescription("ServerDescription");

        Visit mergeVisit = (Visit) visit.clone();
        mergeVisit.setPerson((Person) visit.getPerson().clone());
        mergeVisit.getPerson().setAddress((Location) visit.getPerson().getAddress().clone());
        mergeVisit.setSymptoms((Symptoms) visit.getSymptoms().clone());
        mergeVisit.getSymptoms().setIllLocation((Location) visit.getSymptoms().getIllLocation().clone());
        mergeVisit.setId(null);
        mergeVisit.getPerson().setId(null);
        mergeVisit.getPerson().getAddress().setId(null);
        mergeVisit.getSymptoms().setId(null);
        mergeVisit.getSymptoms().getIllLocation().setId(null);

        mergeVisit.setVisitStatus(VisitStatus.COOPERATIVE);

        DatabaseHelper.getContactDao().mergeOrCreate(mergeContact);
        DatabaseHelper.getVisitDao().mergeOrCreate(mergeVisit);

        Contact updatedContact = DatabaseHelper.getContactDao().queryUuid(contact.getUuid());
        assertThat(updatedContact.getDescription(), is("ServerDescription"));
        Visit updatedVisit = DatabaseHelper.getVisitDao().queryUuid(visit.getUuid());
        assertThat(updatedVisit.getVisitStatus(), is(VisitStatus.COOPERATIVE));
    }

}
