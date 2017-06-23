package de.symeda.sormas.app.event;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;


public class EventEditActivity extends AbstractEditTabActivity {

    public static final String NEW_EVENT = "newEvent";
    public static final String KEY_EVENT_UUID = "eventUuid";
    public static final String KEY_PAGE = "page";

    private EventEditPagerAdapter adapter;
    private String eventUuid;
    private String taskUuid;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        // This makes sure that the given amount of tabs is kept in memory, which means that
        // Android doesn't call onResume when the tab has no focus which would otherwise lead
        // to certain spinners not displaying their values
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(EventEditTabs.values().length);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_event) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        Bundle params = getIntent().getExtras();
        if(params!=null) {

            // setting title
            if (params.containsKey(NEW_EVENT)) {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_event) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
            }
            else {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_event) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
            }

            if (params.containsKey(KEY_EVENT_UUID)) {
                eventUuid = params.getString(KEY_EVENT_UUID);
                Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
                DatabaseHelper.getEventDao().markAsRead(event);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }
        adapter = new EventEditPagerAdapter(getSupportFragmentManager(), eventUuid);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if (params.containsKey(KEY_EVENT_UUID)) {
                outState.putString(KEY_EVENT_UUID, eventUuid);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                outState.putString(TaskForm.KEY_TASK_UUID, taskUuid);
            }
            if (params.containsKey(KEY_PAGE)) {
                outState.putInt(KEY_PAGE, currentTab);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        EventEditTabs tab = EventEditTabs.values()[currentTab];
        switch(tab) {
            // contact data tab
            case EVENT_DATA:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            // person tab
            case EVENT_PERSONS:
                updateActionBarGroups(menu, false, true, true, true, false);
                break;

            // tasks tab
            case EVENT_TASKS:
                updateActionBarGroups(menu, false, true, true, false, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        EventEditTabs tab = EventEditTabs.values()[currentTab];
        Event event = (Event) adapter.getData(EventEditTabs.EVENT_DATA.ordinal());

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (taskUuid != null) {
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;

            // Help button
            case R.id.action_help:
                // @TODO help for contact edit tabs
                return true;

            case R.id.action_markAllAsRead:
                switch (tab) {
                    case EVENT_PERSONS:
                        EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();
                        List<EventParticipant> eventParticipants = eventParticipantDao.getByEvent(event);
                        for (EventParticipant participantToMark : eventParticipants) {
                            eventParticipantDao.markAsRead(participantToMark);
                            personDao.markAsRead(participantToMark.getPerson());
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof EventParticipantsListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                    case EVENT_TASKS:
                        TaskDao taskDao = DatabaseHelper.getTaskDao();
                        List<Task> tasks = taskDao.queryByEvent(event);
                        for (Task taskToMark : tasks) {
                            taskDao.markAsRead(taskToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof TasksListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), event.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:

                switch(tab) {
                    // contact data tab
                    case EVENT_DATA:
                        // check required fields
                        boolean eventTypeReq = event.getEventType()==null;
                        boolean eventDescReq = event.getEventDesc()==null || event.getEventDesc().isEmpty();
                        boolean eventDateReq = event.getEventDate()==null;
                        boolean typeOfPlaceReq = event.getTypeOfPlace()==null;
                        boolean typeOfPlaceTextReq = !typeOfPlaceReq && event.getTypeOfPlace().equals(TypeOfPlace.OTHER);
                        boolean eventSrcFirstNameReq = event.getSrcFirstName()==null || event.getSrcFirstName().isEmpty();
                        boolean eventSrcLastNameReq = event.getSrcLastName()==null || event.getSrcLastName().isEmpty();
                        boolean eventSrcTelNoReq = event.getSrcTelNo()==null || event.getSrcTelNo().isEmpty();

                        boolean validData = !eventTypeReq
                                && !eventDescReq
                                && !eventDateReq
                                && !typeOfPlaceReq
                                && !typeOfPlaceTextReq
                                && !eventSrcFirstNameReq
                                && !eventSrcLastNameReq
                                && !eventSrcTelNoReq;

                        if(validData) {
                            try {

                                EventDao eventDao = DatabaseHelper.getEventDao();
                                eventDao.saveAndSnapshot(event);
                                eventDao.markAsRead(event);

                                if (RetroProvider.isConnected()) {
                                    SyncEventsTask.syncEventsWithProgressDialog(this, new SyncCallback() {
                                        @Override
                                        public void call(boolean syncFailed) {
                                            if (syncFailed) {
                                                Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                            } else {
                                                Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                            }
                                            finish();
                                        }
                                    });
                                } else {
                                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (DaoException e) {
                                Log.e(getClass().getName(), "Error while trying to save alert", e);
                                Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                ErrorReportingHelper.sendCaughtException(tracker, e, event, true);
                            }

                        } else {
                            if(eventTypeReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_type, Snackbar.LENGTH_LONG).show();
                            } else if (eventDescReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_description, Snackbar.LENGTH_LONG).show();
                            } else if (eventDateReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_date, Snackbar.LENGTH_LONG).show();
                            } else if (typeOfPlaceReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_place, Snackbar.LENGTH_LONG).show();
                            } else if (typeOfPlaceTextReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_place_text, Snackbar.LENGTH_LONG).show();
                            } else if (eventSrcFirstNameReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_firstName, Snackbar.LENGTH_LONG).show();
                            } else if (eventSrcLastNameReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_lastName, Snackbar.LENGTH_LONG).show();
                            } else if (eventSrcTelNoReq) {
                                Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_alert_telNo, Snackbar.LENGTH_LONG).show();
                            }
                        }

                        break;
//                    case EVENT_PERSONS:
//                        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
//                        PersonDao personDao = DatabaseHelper.getPersonDao();
//
//                        Person person = (Person)adapter.getData(1);
//
//                        if(person.getAddress()!=null) {
//                            locLocationDao.saveAndSnapshot(person.getAddress());
//                        }
//
//                        DatabaseHelper.getPersonDao().saveAndSnapshot(person);
//                        Toast.makeText(this, "person "+ DataHelper.getShortUuid(person.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
//                        break;

                }

//                onResume();
//                pager.setCurrentItem(currentTab);

                return true;

            // Add button
            case R.id.action_add:
                switch(tab) {
                    case EVENT_PERSONS:
                        Bundle eventParticipantCreateBundle = new Bundle();
                        eventParticipantCreateBundle.putString(KEY_EVENT_UUID,eventUuid);
                        Intent intentEventParticipantCreateBundleNew = new Intent(this, EventParticipantNewActivity.class);
                        intentEventParticipantCreateBundleNew.putExtras(eventParticipantCreateBundle);
                        startActivity(intentEventParticipantCreateBundleNew);
                        break;
                }

                return true;


        }
        return super.onOptionsItemSelected(item);
    }


}
