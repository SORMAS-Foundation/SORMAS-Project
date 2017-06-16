package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.ContactsActivity;
import de.symeda.sormas.app.contact.SyncContactsTask;
import de.symeda.sormas.app.rest.TestEnvironmentInterceptor;
import de.symeda.sormas.app.util.SyncCallback;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class ContactsTest {

    private static Contact contact;

    @Rule
    public final ActivityTestRule<ContactsActivity> contactsActivityRule = new ActivityTestRule<>(ContactsActivity.class, false, true);

    @Test
    public void shouldCreateContact() {
        TestHelper.destroyTestEnvironment();
        TestHelper.initTestEnvironment();

        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(0));

        contact = TestEntityCreator.createContact();

        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldMergeContactsAsExpected() {
        TestEnvironmentInterceptor.setContactMergeTest(true);
        Contact mergeContact = DatabaseHelper.getContactDao().queryUuid(contact.getUuid());

        mergeContact.setDescription("AppDescription");
        try {
            DatabaseHelper.getContactDao().saveAndSnapshot(mergeContact);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        SyncContactsTask.syncContacts(contactsActivityRule.getActivity(), new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                assertThat(DatabaseHelper.getContactDao().queryUuid(contact.getUuid()).getDescription(), is("ServerDescription"));
            }
        });

        TestEnvironmentInterceptor.setContactMergeTest(false);
    }

    @Test
    public void shouldCreateVisit() {
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(0));

        TestEntityCreator.createVisit(contact);

        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(1));
    }

}
