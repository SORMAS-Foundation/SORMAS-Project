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
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.component.AbstractEditActivity;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class ContactEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";

    private ContactEditPagerAdapter adapter;
    private CharSequence titles[];
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

        // Creating titles for the tabs
        titles = new CharSequence[]{
                getResources().getText(R.string.headline_contact_data),
                getResources().getText(R.string.headline_person_information)
        };
//              @TODO  getResources().getText(R.string.headline_visits)
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(KEY_CASE_UUID);
        contactUuid = params.getString(KEY_CONTACT_UUID);
        adapter = new ContactEditPagerAdapter(getSupportFragmentManager(), titles, contactUuid);
        createTabViews(adapter);

        if (params.containsKey(KEY_PAGE)) {
            pager.setCurrentItem(params.getInt(KEY_PAGE));
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
        switch(currentTab) {
            // contact data tab
            case 0:
                updateActionBarGroups(menu, false, false, true);
                break;

            // person tab
            case 1:
                updateActionBarGroups(menu, true, false, true);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();




        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Intent intentCaseContacts = new Intent(this, CaseEditActivity.class);
                intentCaseContacts.putExtra(CaseEditActivity.KEY_PAGE, 3);
                intentCaseContacts.putExtra(CaseEditActivity.KEY_CASE_UUID, caseUuid);
                startActivity(intentCaseContacts);

                //Home/back button
                return true;

            // Help button
            case R.id.action_help:
                // @TODO help for contact edit tabs
                return true;

            // Save button
            case R.id.action_save:

                switch(currentTab) {
                    // contact data tab
                    case 0:
                        ContactDao contactDao = DatabaseHelper.getContactDao();
                        Contact contact = (Contact) adapter.getData(0);

                        contactDao.save(contact);
                        Toast.makeText(this, "contact "+ DataHelper.getShortUuid(contact.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();

                        Person person = (Person)adapter.getData(1);

                        if(person.getAddress()!=null) {
                            locLocationDao.save(person.getAddress());
                        }

                        DatabaseHelper.getPersonDao().save(person);
                        Toast.makeText(this, "person "+ DataHelper.getShortUuid(person.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
                        break;

                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
