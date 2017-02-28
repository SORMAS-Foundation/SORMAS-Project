package de.symeda.sormas.app.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.AbstractEditActivity;


public class EventEditActivity extends AbstractEditActivity {

    public static final String NEW_EVENT = "newEvent";
    public static final String KEY_EVENT_UUID = "eventUuid";
    public static final String KEY_PAGE = "page";

    private EventEditPagerAdapter adapter;
    private String eventUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_event));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if(params!=null) {

            // setting title
            if (params.containsKey(NEW_EVENT)) {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_event));
            }
            else {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_event));
            }

            if (params.containsKey(KEY_EVENT_UUID)) {
                eventUuid = params.getString(KEY_EVENT_UUID);
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
                updateActionBarGroups(menu, false, false, true);
                break;

            // person tab
            case EVENT_PERSONS:
                updateActionBarGroups(menu, false, true, false);
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
                finish();
                return true;

            // Help button
            case R.id.action_help:
                // @TODO help for contact edit tabs
                return true;

            // Save button
            case R.id.action_save:

                switch(tab) {
                    // contact data tab
                    case EVENT_DATA:
                        Event event = (Event) adapter.getData(EventEditTabs.EVENT_DATA.ordinal());

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

                            if(event.getEventLocation()!=null) {
                                DatabaseHelper.getLocationDao().save(event.getEventLocation());
                            }

                            DatabaseHelper.getEventDao().save(event);
                            Toast.makeText(this, "event "+ DataHelper.getShortUuid(event.getUuid()) +" saved", Toast.LENGTH_SHORT).show();

                            // go back to the list
                            finish();
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
