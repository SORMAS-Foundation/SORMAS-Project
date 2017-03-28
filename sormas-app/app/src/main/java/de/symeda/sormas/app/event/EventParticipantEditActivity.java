package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.util.Callback;


public class EventParticipantEditActivity extends AppCompatActivity {

    private EventParticipantDataForm eventParticipantTab;
    private PersonEditForm personEditForm;

    private String eventParticipantUuid;

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

            // Save button
            case R.id.action_save:


                EventParticipant eventParticipant = (EventParticipant) eventParticipantTab.getData();
                Person person = (Person) personEditForm.getData();

                if(person!=null) {
                    DatabaseHelper.getPersonDao().save(person);
                }

                if (eventParticipant != null) {
                    eventParticipant.setPerson(person);
                    DatabaseHelper.getEventParticipantDao().save(eventParticipant);
                }

                Toast.makeText(this, "event person " + DataHelper.getShortUuid(eventParticipant.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                SyncEventsTask.syncEventsWithProgressDialog(this, new Callback() {
                    @Override
                    public void call() {
                        // go back to the list
                        finish();
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
