package de.symeda.sormas.app.event.edit.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.ISaveableWithCallback;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 27/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventNewPersonsInvolvedActivity extends BaseEditActivity<EventParticipant> {

    public static final String TAG = EventNewPersonsInvolvedActivity.class.getSimpleName();

    private EventStatus pageStatus = null;
    private String recordUuid = null;
    private String eventUuid = null;
    private BaseEditActivityFragment activeFragment = null;
    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        saveEventUuidState(outState, eventUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        eventUuid = getEventUuidArg(arguments);
    }

    @Override
    protected EventParticipant getActivityRootData(String recordUuid) {
        return null;
    }

    @Override
    protected EventParticipant getActivityRootDataIfRecordUuidNull() {
        Person person = DatabaseHelper.getPersonDao().build();
        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setPerson(person);

        return eventParticipant;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(EventParticipant activityRootData) {
        if (activeFragment == null) {
            EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(EventNewPersonsInvolvedActivity.this,
                    recordUuid, pageStatus).setEventUuid(eventUuid);
            activeFragment = EventNewPersonsInvolvedShortFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_person_involved_new;
    }

    @Override
    public void saveData()  {
        ISaveableWithCallback fragment = (ISaveableWithCallback)activeFragment;

        if (fragment == null)
            return;

        //TODO: Fix bad code
        if (activeFragment instanceof EventNewPersonsInvolvedShortFragment) {
            fragment.save(this, new Callback.IAction() {
                @Override
                public void call(Object result) {
                    goToNewEventParticipantFullView();
                }
            });
        } else if (activeFragment instanceof EventNewPersonsInvolvedFullFragment) {
            fragment.save(this, new Callback.IAction() {
                @Override
                public void call(Object result) {
                    EventNewPersonsInvolvedActivity.this.finish();
                }
            });
        }
    }

    private void goToNewEventParticipantFullView() {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventNewPersonsInvolvedActivity.this,
                recordUuid, pageStatus).setEventUuid(eventUuid);
        EventNewPersonsInvolvedFullFragment fragment = EventNewPersonsInvolvedFullFragment.newInstance(this, dataCapsule, getStoredActivityRootData());
        replaceFragment(fragment);
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventNewPersonsInvolvedActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);*/
    }
}
