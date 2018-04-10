package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.component.SelectOrCreatePersonDialogBuilder;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.ContactNewFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.ContactValidator;


/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactNewActivity extends AppCompatActivity {

    private String caseUuid;
    private ContactNewForm contactNewForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(CaseEditActivity.KEY_CASE_UUID);

        setContentView(R.layout.sormas_default_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_contact));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.CONTACT_CREATE);
        contactNewForm = new ContactNewForm();
        contactNewForm.setArguments(arguments);

        ft.add(R.id.fragment_frame, contactNewForm).commit();
    }

    @Override
    protected void onResume() {
        LocationService.instance().requestFreshLocation(this);

        super.onResume();
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

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                android.app.AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
                final Contact contact = contactNewForm.getData();

                contact.setContactClassification(ContactClassification.UNCONFIRMED);
                contact.setContactStatus(ContactStatus.ACTIVE);
                contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
                contact.setReportingUser(ConfigProvider.getUser());
                contact.setReportDateTime(new Date());
                contact.setCaze(DatabaseHelper.getCaseDao().queryUuid(caseUuid));

                // Validation
                ContactNewFragmentLayoutBinding binding = contactNewForm.getBinding();
                ContactValidator.clearErrorsForNewContact(binding);
                if (!ContactValidator.validateNewContact(contact, binding)) {
                    return true;
                }

                try {
                    List<Person> existingPersons = DatabaseHelper.getPersonDao().queryForAll();
                    List<Person> similarPersons = new ArrayList<>();
                    for (Person existingPerson : existingPersons) {
                        if (PersonHelper.areNamesSimilar(contact.getPerson().getFirstName() + " " + contact.getPerson().getLastName(),
                                existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                            similarPersons.add(existingPerson);
                        }
                    }
                    if (similarPersons.size() > 0) {
                        AlertDialog.Builder dialogBuilder = new SelectOrCreatePersonDialogBuilder(this, contact.getPerson(), existingPersons, similarPersons, new Consumer() {
                            @Override
                            public void accept(Object parameter) {
                                if (parameter instanceof Person) {
                                    try {
                                        contact.setPerson((Person) parameter);
                                        savePersonAndContact(contact);
                                    } catch (DaoException e) {
                                        Log.e(getClass().getName(), "Error while trying to build contact", e);
                                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                                    }
                                }
                            }
                        });
                        AlertDialog newPersonDialog = dialogBuilder.create();
                        newPersonDialog.show();
                        ((SelectOrCreatePersonDialogBuilder) dialogBuilder).setButtonListeners(newPersonDialog, this);
                    } else {
                        savePersonAndContact(contact);
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to build contact", e);
                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void navBackToCaseContacts() {
        Intent intentCaseContacts = new Intent(this, CaseEditActivity.class);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_PAGE, 5);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_CASE_UUID, caseUuid);
        startActivity(intentCaseContacts);
    }

    private void savePersonAndContact(Contact contact) throws DaoException {
        Person person = contact.getPerson();

        if(contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && person.getAddress().isEmptyLocation()) {
            person.getAddress().setRegion(contact.getCaze().getRegion());
            person.getAddress().setDistrict(contact.getCaze().getDistrict());
            person.getAddress().setCommunity(contact.getCaze().getCommunity());
        }

        // save
        PersonDao personDao = DatabaseHelper.getPersonDao();
        ContactDao contactDao = DatabaseHelper.getContactDao();
        personDao.saveAndSnapshot(person);
        contactDao.saveAndSnapshot(contact);

        if (RetroProvider.isConnected()) {
            SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {
                    if (syncFailed) {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    }
                    navBackToCaseContacts();
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
            navBackToCaseContacts();
        }

    }



}
