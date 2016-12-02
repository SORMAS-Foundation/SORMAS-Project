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

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.util.DataUtils;


/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactNewActivity extends AppCompatActivity {


    private ContactNewTab contactNewTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                //Home/back button
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_save:
                try {
                    Case caze = contactNewTab.getData();

                    caze.setCaseStatus(CaseStatus.POSSIBLE);
                    User user = ConfigProvider.getUser();
                    caze.setReportingUser(user);
                    if (user.getUserRole() == UserRole.SURVEILLANCE_OFFICER) {
                        caze.setSurveillanceOfficer(user);
                    } else if (user.getUserRole() == UserRole.INFORMANT) {
                        caze.setSurveillanceOfficer(user.getAssociatedOfficer());
                    }
                    caze.setReportDate(new Date());

                    SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();
                    Symptoms symptoms = DataUtils.createNew(Symptoms.class);
                    symptomsDao.save(symptoms);

                    caze.setSymptoms(symptoms);

                    CaseDao caseDao = DatabaseHelper.getCaseDao();
                    caseDao.save(caze);

                    // set person's case uuid
                    PersonDao personDao = DatabaseHelper.getPersonDao();
                    Person person = personDao.queryForId(caze.getPerson().getId());
                    person.setCaseUuid(caze.getUuid());
                    DatabaseHelper.getPersonDao().save(person);

                    SyncCasesTask.syncCases(getSupportFragmentManager());

                    Toast.makeText(this, caze.getPerson().toString() + " saved", Toast.LENGTH_SHORT).show();

                    NavUtils.navigateUpFromSameTask(this);

                    // open case edit view
                    Intent intent = new Intent(this, CaseEditActivity.class);
                    intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
                    intent.putExtra(CaseEditActivity.KEY_PAGE, 1);
                    startActivity(intent);

                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, "Error while saving the case. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

        }
        return super.onOptionsItemSelected(item);
    }



}
