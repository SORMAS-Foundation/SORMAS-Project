package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.visit.VisitEditActivity;
import de.symeda.sormas.app.visit.VisitEditDataTab;


public class ContactEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PARENT_TASK_UUID = "taskUuid";



    private ContactEditPagerAdapter adapter;
    private String caseUuid;
    private String contactUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_contact));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
            }
            if (params.containsKey(KEY_CONTACT_UUID)) {
                contactUuid = params.getString(KEY_CONTACT_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }
        adapter = new ContactEditPagerAdapter(getSupportFragmentManager(), contactUuid);
        createTabViews(adapter);


        pager.setCurrentItem(currentTab);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if(params.containsKey(KEY_CASE_UUID)) {
                outState.putString(KEY_CASE_UUID, caseUuid);
            }
            if (params.containsKey(KEY_CONTACT_UUID)) {
                outState.putString(KEY_CONTACT_UUID,contactUuid);
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
        ContactEditTabs tab = ContactEditTabs.values()[currentTab];
        switch(tab) {
            // contact data tab
            case CONTACT_DATA:
                updateActionBarGroups(menu, false, false, true);
                break;

            // person tab
            case PERSON:
                updateActionBarGroups(menu, true, false, true);
                break;

            // tasks tab
            case VISITS:
                updateActionBarGroups(menu, false, true, false);
                break;

            // tasks tab
            case TASKS:
                updateActionBarGroups(menu, false, false, false);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        ContactEditTabs tab = ContactEditTabs.values()[currentTab];

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
                    case CONTACT_DATA:
                        ContactDao contactDao = DatabaseHelper.getContactDao();
                        Contact contact = (Contact) adapter.getData(0);

                        contactDao.save(contact);
                        Toast.makeText(this, "contact "+ DataHelper.getShortUuid(contact.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
                        break;
                    case PERSON:
                        Person person = (Person)adapter.getData(1);

                        if(person.getAddress()!=null) {
                            DatabaseHelper.getLocationDao().save(person.getAddress());
                        }

                        DatabaseHelper.getPersonDao().save(person);
                        Toast.makeText(this, "person "+ DataHelper.getShortUuid(person.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
                        break;

                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

            // Add button
            case R.id.action_add:
                switch(tab) {
                    case VISITS:
                        // only contact officer is allowd to create visits
//                        if(UserRole.CONTACT_OFFICER.equals(ConfigProvider.getUser().getUserRole())) {
                            Bundle visitBundle = new Bundle();
                            visitBundle.putString(KEY_CONTACT_UUID, contactUuid);
                            visitBundle.putBoolean(VisitEditDataTab.NEW_VISIT, true);
                            Intent intentVisitEdit = new Intent(this, VisitEditActivity.class);
                            intentVisitEdit.putExtras(visitBundle);
                            startActivity(intentVisitEdit);
//                        }
                        break;
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
