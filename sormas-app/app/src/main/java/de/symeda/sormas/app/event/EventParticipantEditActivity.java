package de.symeda.sormas.app.event;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;


public class EventParticipantEditActivity extends AppCompatActivity {

    private EventParticipantDataForm eventParticipantTab;
    private PersonEditForm personEditForm;

    private String eventParticipantUuid;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_participant_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_eventParticipant) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        EventParticipant eventParticipant = null;

        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if(params.containsKey(EventParticipant.UUID)) {
                eventParticipantUuid = params.getString(EventParticipant.UUID);
            }
        }

        eventParticipantTab = new EventParticipantDataForm();
        eventParticipantTab.setArguments(params);
        ft.add(R.id.eventParticipant_fragment, eventParticipantTab);

        personEditForm = new PersonEditForm();
        // load person from eventParticipant and give it to personEditForm
        Bundle personEditBundle = new Bundle();
        eventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
        personEditBundle.putString(Person.UUID, eventParticipant.getPerson().getUuid());

        personEditForm.setArguments(personEditBundle);
        ft.add(R.id.eventParticipant_person_fragment, personEditForm);

        ft.commit();

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventParticipantTab.onResume();
        personEditForm.onResume();
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

                try {
                    if (person != null) {
                        DatabaseHelper.getPersonDao().save(person);
                    }

                    if (eventParticipant != null) {
                        eventParticipant.setPerson(person);
                        DatabaseHelper.getEventParticipantDao().save(eventParticipant);
                    }

                    Toast.makeText(this, "Alert person " + DataHelper.getShortUuid(eventParticipant.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                    SyncEventsTask.syncEventsWithProgressDialog(this, new Callback() {
                        @Override
                        public void call() {
                            // go back to the list
                            finish();
                        }
                    });
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save alert person", e);
                    Toast.makeText(this, "Alert person could not be saved because of an internal error.", Toast.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, eventParticipant, true);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
