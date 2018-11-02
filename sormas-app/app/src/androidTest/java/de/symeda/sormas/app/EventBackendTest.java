package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class EventBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void shouldCreateEvent() {
        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(0));
        TestEntityCreator.createEvent();

        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEventParticipant(){
        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(0));

        Event event = TestEntityCreator.createEvent();
        TestEntityCreator.createEventParticipant(event);

        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(1));
    }

    /**
     * This tests merging of events and event participants.
     */
    @Test
    public void shouldMergeAsExpected() throws DaoException {
        Event event = TestEntityCreator.createEvent();
        EventParticipant eventParticipant = TestEntityCreator.createEventParticipant(event);

        event.setEventDesc("AppEventDescription");
        eventParticipant.setInvolvementDescription("AppInvolvementDescription");

        DatabaseHelper.getEventDao().saveAndSnapshot(event);
        DatabaseHelper.getEventDao().accept(event);
        DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
        DatabaseHelper.getEventParticipantDao().accept(eventParticipant);

        Event mergeEvent = (Event) event.clone();
        mergeEvent.setEventLocation((Location) event.getEventLocation().clone());
        mergeEvent.setId(null);
        mergeEvent.getEventLocation().setId(null);
        mergeEvent.setEventDesc("ServerEventDescription");

        EventParticipant mergeEventParticipant = (EventParticipant) eventParticipant.clone();
        mergeEventParticipant.setPerson((Person) eventParticipant.getPerson().clone());
        mergeEventParticipant.getPerson().setAddress((Location) eventParticipant.getPerson().getAddress().clone());
        mergeEventParticipant.setId(null);
        mergeEventParticipant.getPerson().getAddress().setId(null);
        mergeEventParticipant.setInvolvementDescription("ServerInvolvementDescription");

        DatabaseHelper.getEventDao().mergeOrCreate(mergeEvent);
        DatabaseHelper.getEventParticipantDao().mergeOrCreate(mergeEventParticipant);

        Event updatedEvent = DatabaseHelper.getEventDao().queryUuid(event.getUuid());
        assertThat(updatedEvent.getEventDesc(), is("ServerEventDescription"));
        EventParticipant updatedEventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipant.getUuid());
        assertThat(updatedEventParticipant.getInvolvementDescription(), is("ServerInvolvementDescription"));
    }

    @Test
    public void shouldAcceptAsExpected() throws DaoException {
        Event event = TestEntityCreator.createEvent();
        assertThat(event.isModified(), is(false));

        event.setEventDesc("NewEventDescription");

        DatabaseHelper.getEventDao().saveAndSnapshot(event);
        event = DatabaseHelper.getEventDao().queryUuid(event.getUuid());

        assertThat(event.isModified(), is(true));
        assertNotNull(DatabaseHelper.getEventDao().querySnapshotByUuid(event.getUuid()));

        DatabaseHelper.getEventDao().accept(event);
        event = DatabaseHelper.getEventDao().queryUuid(event.getUuid());

        assertNull(DatabaseHelper.getEventDao().querySnapshotByUuid(event.getUuid()));
        assertThat(event.isModified(), is(false));
    }

    @Test
    public void shouldDeleteWithDependingEntities() throws DaoException, SQLException {
        // Assure that there are no events or depending entities in the app to start with
        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(0));

        Event event = TestEntityCreator.createEvent();
        TestEntityCreator.createEventParticipant(event);
        TestEntityCreator.createEventTask(event, TaskStatus.PENDING, event.getReportingUser());

        // Assure that the event and depending entities have been successfully created
        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(1));

        DatabaseHelper.getEventDao().deleteEventAndAllDependingEntities(event.getUuid());

        // Assure that there are no events or depending entities in the app after the deletion
        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(0));
    }

}
