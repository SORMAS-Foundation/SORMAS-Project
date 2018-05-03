package de.symeda.sormas.app.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.SelectOrCreatePersonDialogBuilder;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.EventParticipantValidator;

public class EventParticipantNewActivity extends AppCompatActivity {

    private String eventUuid;

    private EventParticipantNewPersonForm eventParticipantNewPersonForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        Bundle params = getIntent().getExtras();
        eventUuid = params.getString(EventEditActivity.KEY_EVENT_UUID);

        setContentView(R.layout.sormas_default_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_eventParticipant));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        eventParticipantNewPersonForm = new EventParticipantNewPersonForm();
        params.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.EVENTPARTICIPANT_CREATE);
        eventParticipantNewPersonForm.setArguments(params);
        ft.add(R.id.fragment_frame, eventParticipantNewPersonForm).commit();
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

            case android.R.id.home:
                //Home/back button
                NavUtils.navigateUpFromSameTask(this);

                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                android.app.AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
                final EventParticipant eventParticipant = eventParticipantNewPersonForm.getData();

                // Validation
                EventParticipantNewFragmentLayoutBinding binding = eventParticipantNewPersonForm.getBinding();
                EventParticipantValidator.clearErrorsForNewEventParticipant(binding);
                if (!EventParticipantValidator.validateNewEvent(eventParticipant, binding)) {
                    return true;
                }

                try {
                    List<PersonNameDto> existingPersons = DatabaseHelper.getPersonDao().getPersonNameDtos();
                    List<Person> similarPersons = new ArrayList<>();
                    for (PersonNameDto existingPerson : existingPersons) {
                        if (PersonHelper.areNamesSimilar(eventParticipant.getPerson().getFirstName() + " " + eventParticipant.getPerson().getLastName(),
                                existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                            Person person = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                            similarPersons.add(person);
                        }
                    }

                    if (similarPersons.size() > 0) {
                        AlertDialog.Builder dialogBuilder = new SelectOrCreatePersonDialogBuilder(this, eventParticipant.getPerson(), existingPersons, similarPersons, new Consumer() {
                            @Override
                            public void accept(Object parameter) {
                                if (parameter instanceof Person) {
                                    try {
                                        eventParticipant.setPerson((Person) parameter);
                                        savePersonAndEventParticipant(eventParticipant);
                                    } catch (DaoException e) {
                                        Log.e(getClass().getName(), "Error while trying to build event person", e);
                                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                                    }
                                }
                            }
                        });
                        AlertDialog newPersonDialog = dialogBuilder.create();
                        newPersonDialog.show();
                        ((SelectOrCreatePersonDialogBuilder) dialogBuilder).setButtonListeners(newPersonDialog, this);

                    } else {
                        savePersonAndEventParticipant(eventParticipant);
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to build event person", e);
                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void savePersonAndEventParticipant(final EventParticipant eventParticipant) throws DaoException {

        // save the person
        PersonDao personDao = DatabaseHelper.getPersonDao();
        personDao.saveAndSnapshot(eventParticipant.getPerson());
        // set the given event
        final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
        eventParticipant.setEvent(event);
        // save the contact
        EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
        eventParticipantDao.saveAndSnapshot(eventParticipant);

        if (RetroProvider.isConnected()) {
            SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, this, new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {
                    if (syncFailed) {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
                    }
                    showEventParticipantEditView(eventParticipant);
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_event_person)), Snackbar.LENGTH_LONG).show();
            finish();
            showEventParticipantEditView(eventParticipant);
        }
    }

    private void showEventParticipantEditView(EventParticipant eventParticipant) {
        Intent intent = new Intent(this, EventParticipantEditActivity.class);
        intent.putExtra(EventParticipant.UUID, eventParticipant.getUuid());
        startActivity(intent);
    }

}
