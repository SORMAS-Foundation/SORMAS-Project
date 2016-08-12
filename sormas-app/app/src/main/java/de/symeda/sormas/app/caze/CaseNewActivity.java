package de.symeda.sormas.app.caze;

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

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.user.User;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseNewActivity extends AppCompatActivity {


    private CaseNewTab caseNewTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_case));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        caseNewTab = new CaseNewTab();
        ft.add(R.id.fragment_frame, caseNewTab).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_caze_action_bar, menu);
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
                Case caze = caseNewTab.getData();

                caze.setCaseStatus(CaseStatus.NEW);
                User user = ConfigProvider.getUser();
                caze.setReportingUser(user);
                if (user.getUserRole() == UserRole.SURVEILLANCE_OFFICER) {
                    caze.setSurveillanceOfficer(user);
                } else  if (user.getUserRole() == UserRole.INFORMANT) {
                    caze.setSurveillanceOfficer(user.getAssociatedOfficer());
                }

                CaseDao caseDao = DatabaseHelper.getCaseDao();
                caseDao.save(caze);

                // set person's case uuid
                PersonDao personDao = DatabaseHelper.getPersonDao();
                Person person = personDao.queryForId(caze.getPerson().getId());
                person.setCaseUuid(caze.getUuid());
                DatabaseHelper.getPersonDao().save(person);

                new SyncCasesTask().execute();

                Toast.makeText(this, caze.getPerson().toString() + " saved", Toast.LENGTH_SHORT).show();

                NavUtils.navigateUpFromSameTask(this);

                // open case edit view
                Intent intent = new Intent(this, CaseEditActivity.class);
                intent.putExtra(Case.UUID, caze.getUuid());
                startActivity(intent);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }



}
