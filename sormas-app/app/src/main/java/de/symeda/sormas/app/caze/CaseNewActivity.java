package de.symeda.sormas.app.caze;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.SelectOrCreatePersonDialogBuilder;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.validation.CaseValidator;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseNewActivity extends AppCompatActivity {

    public static final String CONTACT = "contact";
    public static final String PERSON = "person";
    public static final String DISEASE = "disease";

    private CaseNewForm caseNewForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        setContentView(R.layout.sormas_default_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_case));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        caseNewForm = new CaseNewForm();

        Bundle params = getIntent().getExtras();
        Bundle arguments = new Bundle();
        if (params != null && params.containsKey(CONTACT)) {
            Contact contact = DatabaseHelper.getContactDao().queryUuid((String) params.get(CONTACT));
            arguments.putSerializable(PERSON, contact.getPerson());
            arguments.putSerializable(DISEASE, contact.getCaze().getDisease());
        }
        arguments.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.CASE_CREATE);
        caseNewForm.setArguments(arguments);
        ft.add(R.id.fragment_frame, caseNewForm).commit();
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
                finish();
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                android.app.AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
                final Case caze = caseNewForm.getData();

                // Validation
                CaseNewFragmentLayoutBinding binding = caseNewForm.getBinding();
                CaseValidator.clearErrorsForNewCase(binding);
                if (!CaseValidator.validateNewCase(caze, binding)) {
                    return true;
                }

                try {
                    Bundle params = getIntent().getExtras();
                    if (params != null && params.containsKey(CONTACT)) {
                        savePersonAndCase(caze);
                    } else {
                        List<PersonNameDto> existingPersons = DatabaseHelper.getPersonDao().getPersonNameDtos();
                        List<Person> similarPersons = new ArrayList<>();
                        for (PersonNameDto existingPerson : existingPersons) {
                            if (PersonHelper.areNamesSimilar(caze.getPerson().getFirstName() + " " + caze.getPerson().getLastName(),
                                    existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                                Person person = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                                similarPersons.add(person);
                            }
                        }
                        if (similarPersons.size() > 0) {
                            AlertDialog.Builder dialogBuilder = new SelectOrCreatePersonDialogBuilder(this, caze.getPerson(), existingPersons, similarPersons, new Consumer() {
                                @Override
                                public void accept(Object parameter) {
                                    if (parameter instanceof Person) {
                                        try {
                                            caze.setPerson((Person) parameter);
                                            savePersonAndCase(caze);
                                        } catch (DaoException e) {
                                            Log.e(getClass().getName(), "Error while trying to build case", e);
                                            Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
                                            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                                        }
                                    }
                                }
                            });
                            AlertDialog newPersonDialog = dialogBuilder.create();
                            newPersonDialog.show();
                            ((SelectOrCreatePersonDialogBuilder) dialogBuilder).setButtonListeners(newPersonDialog, this);
                        } else {
                            savePersonAndCase(caze);
                        }
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to build case", e);
                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCaseEditView(Case caze) {
        // open case edit view
        Intent intent = new Intent(this, CaseEditActivity.class);
        intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
        intent.putExtra(CaseEditActivity.KEY_PAGE, 1);
        startActivity(intent);
    }

    private void savePersonAndCase(final Case caze) throws DaoException {
        // save the person
        PersonDao personDao = DatabaseHelper.getPersonDao();
        personDao.saveAndSnapshot(caze.getPerson());

        caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
        caze.setInvestigationStatus(InvestigationStatus.PENDING);

        User user = ConfigProvider.getUser();
        caze.setReportingUser(user);
        if (user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
            caze.setSurveillanceOfficer(user);
        } else if (user.hasUserRole(UserRole.INFORMANT)) {
            caze.setSurveillanceOfficer(user.getAssociatedOfficer());
        }
        caze.setReportDate(new Date());

        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
        caze.setEpidNumber((caze.getRegion().getEpidCode() != null ? caze.getRegion().getEpidCode() : "")
                + "-" + (caze.getDistrict().getEpidCode() != null ? caze.getDistrict().getEpidCode() : "")
                + "-" + year + "-");

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        caseDao.saveAndSnapshot(caze);

        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
        showCaseEditView(caze);
    }

}
