package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.event.EventsActivity;
import de.symeda.sormas.app.event.SyncEventsTask;
import de.symeda.sormas.app.rest.TestEnvironmentInterceptor;
import de.symeda.sormas.app.util.SyncCallback;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class EventsTest {

    private static Event event;

    @Rule
    public final ActivityTestRule<EventsActivity> eventsActivityRule = new ActivityTestRule<>(EventsActivity.class, false, true);

    @Test
    public void shouldCreateEvent() {
        TestHelper.destroyTestEnvironment();
        TestHelper.initTestEnvironment();

        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(0));

        event = TestEntityCreator.createEvent();

        assertThat(DatabaseHelper.getEventDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldMergeEventsAsExpected() {
        TestEnvironmentInterceptor.setEventMergeTest(true);
        Event mergeEvent = DatabaseHelper.getEventDao().queryUuid(event.getUuid());

        mergeEvent.setEventDesc("AppEventDescription");
        try {
            DatabaseHelper.getEventDao().saveAndSnapshot(mergeEvent);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        SyncEventsTask.syncEventsWithCallback(eventsActivityRule.getActivity(), null, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                assertThat(DatabaseHelper.getEventDao().queryUuid(event.getUuid()).getEventDesc(), is("ServerEventDescription"));
            }
        });

        TestEnvironmentInterceptor.setEventMergeTest(false);
    }

    @Test
    public void shouldCreateEventParticipant(){
        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(0));

        TestEntityCreator.createEventParticipant(event);

        assertThat(DatabaseHelper.getEventParticipantDao().queryForAll().size(), is(1));
    }

}
