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
import android.view.View;

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
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.EventValidator;


public class EventEditActivity extends AbstractEditTabActivity {

    public static final String KEY_EVENT_UUID = "eventUuid";
    public static final String KEY_PAGE = "page";

    private EventEditPagerAdapter adapter;
    private String eventUuid;
    private String taskUuid;

    @Override
    public boolean isEditing() {
        return true;
    }

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

        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if (params.containsKey(KEY_EVENT_UUID)) {
                eventUuid = params.getString(KEY_EVENT_UUID);
                Event initialEntity = DatabaseHelper.getEventDao().queryUuid(eventUuid);
                // If the event has been removed from the database in the meantime, redirect the user to the events overview
                if (initialEntity == null) {
                    Intent intent = new Intent(this, EventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                DatabaseHelper.getEventDao().markAsRead(initialEntity);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (eventUuid != null) {
            Event currentEntity = DatabaseHelper.getEventDao().queryUuid(eventUuid);
            // If the event has been removed from the database in the meantime, redirect the user to the events overview
            if (currentEntity == null) {
                Intent intent = new Intent(this, EventsActivity.class);
                startActivity(intent);
                finish();
            }

            if (currentEntity.isUnreadOrChildUnread()) {
                // Resetting the adapter will reload the form and therefore also override any unsaved changes
                DatabaseHelper.getEventDao().markAsRead(currentEntity);
                setAdapter();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_event)), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
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
        Event event = (Event) getData(EventEditTabs.EVENT_DATA.ordinal());

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
                        // Validation
                        EventDataFragmentLayoutBinding binding = ((EventEditDataForm)getTabByPosition(EventEditTabs.EVENT_DATA.ordinal())).getBinding();
                        EventValidator.clearErrorsForEventData(binding);

                        int validationErrorTab = -1;

                        if (!EventValidator.validateEventData(event, binding)) {
                            validationErrorTab = EventEditTabs.EVENT_DATA.ordinal();
                        }

                        if (validationErrorTab >= 0) {
                            pager.setCurrentItem(validationErrorTab);
                            return true;
                        }

                        try {
                            EventDao eventDao = DatabaseHelper.getEventDao();
                            eventDao.saveAndSnapshot(event);

                            if (RetroProvider.isConnected()) {
                                SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
                                    @Override
                                    public void call(boolean syncFailed, String syncFailedMessage) {
                                        if (syncFailed) {
                                            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_event)), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event)), Snackbar.LENGTH_LONG).show();
                                        }
                                        finish();
                                    }
                                });
                            } else {
                                Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event)), Snackbar.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (DaoException e) {
                            Log.e(getClass().getName(), "Error while trying to save event", e);
                            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event)), Snackbar.LENGTH_LONG).show();
                            ErrorReportingHelper.sendCaughtException(tracker, e, event, true);
                        }

                        break;
                }

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

    private void setAdapter() {
        adapter = new EventEditPagerAdapter(getSupportFragmentManager(), eventUuid);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }

}
