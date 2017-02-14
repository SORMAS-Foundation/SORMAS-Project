package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.person.PersonEditTab;


public class EventParticipantEditActivity extends AppCompatActivity {

    private EventParticipantPersonTab eventParticipantTab;
    private PersonEditTab personEditTab;

    private String eventParticipantUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_participant_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_eventParticipant));
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

        eventParticipantTab = new EventParticipantPersonTab();
        eventParticipantTab.setArguments(params);
        ft.add(R.id.eventParticipant_fragment, eventParticipantTab);

        personEditTab = new PersonEditTab();
        // load person from eventParticipant and give it to personEditTab
        Bundle personEditBundle = new Bundle();
        eventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
        personEditBundle.putString(Person.UUID, eventParticipant.getPerson().getUuid());

        personEditTab.setArguments(personEditBundle);
        ft.add(R.id.eventParticipant_person_fragment, personEditTab);

        ft.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();

        eventParticipantTab.onResume();
        personEditTab.onResume();
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
                Person person = (Person) personEditTab.getData();

                eventParticipant.setPerson(person);

//                Visit visit = (Visit) adapter.getData(VisitEditTabs.VISIT_DATA.ordinal());
//                Symptoms symptoms = (Symptoms)adapter.getData(VisitEditTabs.SYMPTOMS.ordinal());
//
//                SymptomsEditTab symptomsEditTab = (SymptomsEditTab) adapter.getTabByPosition(VisitEditTabs.SYMPTOMS.ordinal());
//
//                // method returns a String, null means that there is no error message and thus
//                // the data is valid
//                try {
//                    symptomsEditTab.validateVisitData(symptoms, visit.getVisitStatus() == VisitStatus.COOPERATIVE);
//
//                    if (symptoms != null) {
//                        visit.setSymptoms(symptoms);
//                        DatabaseHelper.getSymptomsDao().save(symptoms);
//                    }
//
//                    visit.setVisitUser(ConfigProvider.getUser());
//
//                    DatabaseHelper.getVisitDao().save(visit);
//                    Toast.makeText(this, "visit " + DataHelper.getShortUuid(visit.getUuid()) + " saved", Toast.LENGTH_SHORT).show();
//
//                    finish();
//
//                    return true;
//                } catch(ValidationFailedException e) {
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//
//                    // if any symptomsfield is required, change pager to symptoms tab
//                    if(currentTab!=VisitEditTabs.SYMPTOMS.ordinal()) {
//                        pager.setCurrentItem(VisitEditTabs.SYMPTOMS.ordinal());
//                    }
//                    return true;
//                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
