package de.symeda.sormas.app.event;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.contact.ContactEditTabs;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.task.TaskEditActivity;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;


public class EventEditActivity extends AbstractEditActivity {

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_event) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                updateActionBarGroups(menu, false, true, false, true);
                break;

            // person tab
            case EVENT_PERSONS:
                updateActionBarGroups(menu, false, true, true, false);
                break;

            // tasks tab
            case EVENT_TASKS:
                updateActionBarGroups(menu, false, true, false, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        EventEditTabs tab = EventEditTabs.values()[currentTab];

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

            // Report problem button
            case R.id.action_report:
                Event event = (Event) adapter.getData(EventEditTabs.EVENT_DATA.ordinal());

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), event.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:

                switch(tab) {
                    // contact data tab
                    case EVENT_DATA:
                        event = (Event) adapter.getData(EventEditTabs.EVENT_DATA.ordinal());

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
                                if (event.getEventLocation() != null) {
                                    DatabaseHelper.getLocationDao().save(event.getEventLocation());
                                }

                                DatabaseHelper.getEventDao().save(event);
                                Toast.makeText(this, "event " + DataHelper.getShortUuid(event.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                                if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
                                    SyncEventsTask.syncEventsWithProgressDialog(this, new SyncCallback() {
                                        @Override
                                        public void call(boolean syncFailed) {
                                            if (syncFailed) {
                                                Toast.makeText(getApplicationContext(), "The alert has been saved, but could not be transferred to the server. An error report has been automatically sent and the synchronization will be repeated later.", Toast.LENGTH_LONG).show();
                                            }
                                            finish();
                                        }
                                    });
                                } else {
                                    finish();
                                }
                            } catch (DaoException e) {
                                Log.e(getClass().getName(), "Error while trying to save alert", e);
                                Toast.makeText(this, "Alert could not be saved because of an internal error.", Toast.LENGTH_LONG).show();
                                ErrorReportingHelper.sendCaughtException(tracker, e, event, true);
                            }

                        }
                        else {
                            if(eventTypeReq) {
                                Toast.makeText(this, "Not saved. Please specify the event type.", Toast.LENGTH_LONG).show();
                            }
                            else if(eventDescReq) {
                                Toast.makeText(this, "Not saved. Please specify the event description.", Toast.LENGTH_LONG).show();
                            }
                            else if(eventDateReq) {
                                Toast.makeText(this, "Not saved. Please specify the event date.", Toast.LENGTH_LONG).show();
                            }
                            else if(typeOfPlaceReq) {
                                Toast.makeText(this, "Not saved. Please specify the type of place.", Toast.LENGTH_LONG).show();
                            }
                            else if(typeOfPlaceTextReq) {
                                Toast.makeText(this, "Not saved. Please specify the other type of place.", Toast.LENGTH_LONG).show();
                            }
                            else if(eventSrcFirstNameReq) {
                                Toast.makeText(this, "Not saved. Please specify the the source first name.", Toast.LENGTH_LONG).show();
                            }
                            else if(eventSrcLastNameReq) {
                                Toast.makeText(this, "Not saved. Please specify the the source last name.", Toast.LENGTH_LONG).show();
                            }
                            else if(eventSrcTelNoReq) {
                                Toast.makeText(this, "Not saved. Please specify the the source telephone no.", Toast.LENGTH_LONG).show();
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
//                            locLocationDao.save(person.getAddress());
//                        }
//
//                        DatabaseHelper.getPersonDao().save(person);
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
