package de.symeda.sormas.app.event;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.EventParticipantFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.EventParticipantValidator;
import de.symeda.sormas.app.validation.PersonValidator;


public class EventParticipantEditActivity extends AbstractSormasActivity {

    private EventParticipantDataForm eventParticipantTab;
    private PersonEditForm personEditForm;

    private String eventParticipantUuid;

    private Bundle params;

    @Override
    public boolean isEditing() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        setContentView(R.layout.event_participant_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_eventParticipant));
        }

        params = getIntent().getExtras();
        if(params!=null) {
            if(params.containsKey(EventParticipant.UUID)) {
                eventParticipantUuid = params.getString(EventParticipant.UUID);
                EventParticipant initialEntity = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
                // If the event participant (and thus probably the event) has been removed from the database in the meantime, redirect the user to the events overview
                if (initialEntity == null) {
                    Intent intent = new Intent(this, EventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                DatabaseHelper.getEventParticipantDao().markAsRead(initialEntity);
            }
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventParticipant currentEntity = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
        // If the event participant (and thus probably the event) has been removed from the database in the meantime, redirect the user to the events overview
        if (currentEntity == null) {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
            finish();
        }

        if (currentEntity.isUnreadOrChildUnread()) {
            // Resetting the adapter will reload the form and therefore also override any unsaved changes
            DatabaseHelper.getEventParticipantDao().markAsRead(currentEntity);
            setAdapter();
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_action_help,false);
        menu.setGroupVisible(R.id.group_action_add,false);
        menu.setGroupVisible(R.id.group_action_save,true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

            // Help button
            case R.id.action_help:

                return true;

            // Report problem button
            case R.id.action_report:
                EventParticipant eventParticipant = (EventParticipant) eventParticipantTab.getData();

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), eventParticipant.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                eventParticipant = (EventParticipant) eventParticipantTab.getData();
                Person person = (Person) personEditForm.getData();

                // Validation
                EventParticipantFragmentLayoutBinding eventParticipantBinding = eventParticipantTab.getBinding();
                PersonEditFragmentLayoutBinding personBinding = personEditForm.getBinding();

                EventParticipantValidator.clearErrorsForEventParticipantData(eventParticipantBinding);
                PersonValidator.clearErrors(personBinding);

                boolean validationError = false;

                if (!PersonValidator.validatePersonData(person, personBinding)) {
                    validationError = true;
                }
                if (!EventParticipantValidator.validateEventParticipantData(eventParticipant, eventParticipantBinding)) {
                    validationError = true;
                }

                if (validationError) {
                    return true;
                }

                try {
                    PersonDao personDao = DatabaseHelper.getPersonDao();
                    EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
                    personDao.saveAndSnapshot(person);
                    eventParticipant.setPerson(person);
                    eventParticipantDao.saveAndSnapshot(eventParticipant);

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                                }
                                finish();
                            }
                        });
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save event person", e);
                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, eventParticipant, true);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        eventParticipantTab = new EventParticipantDataForm();
        params.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.EVENTPARTICIPANT_EDIT);
        eventParticipantTab.setArguments(params);
        ft.replace(R.id.eventParticipant_fragment, eventParticipantTab);

        personEditForm = new PersonEditForm();
        // load person from eventParticipant and give it to personEditForm
        Bundle personEditBundle = new Bundle();
        EventParticipantDao dao = DatabaseHelper.getEventParticipantDao();
        EventParticipant eventParticipant = dao.queryUuid(eventParticipantUuid);

        personEditBundle.putString(Person.UUID, eventParticipant.getPerson().getUuid());
        personEditBundle.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.EVENTPARTICIPANT_EDIT);

        personEditForm.setArguments(personEditBundle);
        ft.replace(R.id.eventParticipant_person_fragment, personEditForm);

        ft.commit();
    }

}
