package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.Callback;


/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactNewActivity extends AppCompatActivity {

    private String caseUuid;


    private ContactNewTab contactNewTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(CaseEditActivity.KEY_CASE_UUID);

        setContentView(R.layout.sormas_root_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_contact));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        contactNewTab = new ContactNewTab();
        ft.add(R.id.fragment_frame, contactNewTab).commit();



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
//                NavUtils.navigateUpFromSameTask(this);


                navBackToCaseContacts();

                return true;

            case R.id.action_save:
                try {
                    final Contact contact = contactNewTab.getData();
                    contact.setContactClassification(ContactClassification.POSSIBLE);
                    contact.setReportingUser(ConfigProvider.getUser());
                    contact.setReportDateTime(new Date());
                    contact.setCaze(DatabaseHelper.getCaseDao().queryUuid(caseUuid));

                    boolean validData = true;

                    if(contact.getLastContactDate()==null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime()) {
                        validData = false;
                        Toast.makeText(this, "Please make sure contact date is set and not in the future.", Toast.LENGTH_SHORT).show();
                    }

                    if(contact.getPerson().getFirstName().isEmpty() || contact.getPerson().getLastName().isEmpty() ) {
                        validData = false;
                        Toast.makeText(this, "Please select a person.", Toast.LENGTH_SHORT).show();
                    }

                    if(contact.getRelationToCase() == null) {
                        validData = false;
                        Toast.makeText(this, "Please select a relationship with the case.", Toast.LENGTH_SHORT).show();
                    }

                    if(validData) {
                        List<Person> existingPersons = DatabaseHelper.getPersonDao().getAllByName(contact.getPerson().getFirstName(), contact.getPerson().getLastName());
                        if(existingPersons.size()>0) {
                            contactNewTab.selectOrCreatePersonDialog(contact.getPerson(), existingPersons, new Callback() {
                                @Override
                                public void call() {
                                    contact.setPerson(contactNewTab.getSelectedPersonFromDialog());
                                    savePersonAndContact(contact);
                                    navBackToCaseContacts();
                                }
                            });
                        }
                        else {
                            savePersonAndContact(contact);
                            navBackToCaseContacts();
                        }
                    }

                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, "Error while saving the contact. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    private void navBackToCaseContacts() {
        Intent intentCaseContacts = new Intent(this, CaseEditActivity.class);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_PAGE, 3);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_CASE_UUID, caseUuid);
        startActivity(intentCaseContacts);
    }

    private void savePersonAndContact(Contact contact) {
        // save the person
        DatabaseHelper.getPersonDao().save(contact.getPerson());
        new SyncPersonsTask().execute();

        // save the contact
        DatabaseHelper.getContactDao().save(contact);
        new SyncContactsTask().execute();

        Toast.makeText(this, "Contact to " + contact.getPerson().toString() + " saved", Toast.LENGTH_SHORT).show();
    }


}
