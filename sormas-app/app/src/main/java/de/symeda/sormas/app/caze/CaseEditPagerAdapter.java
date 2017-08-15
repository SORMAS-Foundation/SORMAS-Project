package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.contact.ContactsListFragment;
import de.symeda.sormas.app.epidata.EpiDataForm;
import de.symeda.sormas.app.hospitalization.HospitalizationForm;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.sample.SamplesListFragment;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CaseEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle caseEditBundle; // this bundle contains the uuids

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CaseEditPagerAdapter(FragmentManager fm, String caseUuid) {
        super(fm);
        caseEditBundle = new Bundle();
        caseEditBundle.putString(Case.UUID, caseUuid);
    }

    // This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        CaseEditTabs tab = CaseEditTabs.values()[position];
        Case caze = null;
        switch (tab) {
            case CASE_DATA:
                frag = new CaseEditDataForm();
                frag.setArguments(caseEditBundle);
                break;
            case PATIENT:
                frag = new PersonEditForm();

                Bundle personEditBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                personEditBundle.putString(Person.UUID, caze.getPerson().getUuid());
                personEditBundle.putSerializable(Case.DISEASE, caze.getDisease());
                frag.setArguments(personEditBundle);
                break;
            case SYMPTOMS:
                frag = new SymptomsEditForm();

                Bundle symptomsEditBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                symptomsEditBundle.putString(Symptoms.UUID, caze.getSymptoms().getUuid());
                symptomsEditBundle.putSerializable(Case.DISEASE, caze.getDisease());
                frag.setArguments(symptomsEditBundle);
                break;
            case CONTACTS:
                ContactsListFragment contactsListTab = new ContactsListFragment();
                contactsListTab.setArguments(caseEditBundle);
                frag = contactsListTab;
                break;
            case TASKS:
                TasksListFragment tasksListTab = new TasksListFragment();
                Bundle tasksListBundle = new Bundle();
                tasksListBundle.putString(TasksListFragment.KEY_CASE_UUID, (String)caseEditBundle.get(Case.UUID));
                tasksListTab.setArguments(tasksListBundle);
                frag = tasksListTab;
                break;
            case SAMPLES:
                SamplesListFragment samplesListTab = new SamplesListFragment();
                Bundle samplesListBundle = new Bundle();
                samplesListBundle.putString(SamplesListFragment.KEY_CASE_UUID, (String) caseEditBundle.get(Case.UUID));
                samplesListTab.setArguments(samplesListBundle);
                frag = samplesListTab;
                break;
            case HOSPITALIZATION:
                frag = new HospitalizationForm();

                Bundle hospitalizationBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                hospitalizationBundle.putString(HospitalizationForm.KEY_CASE_UUID, caze.getUuid());
                hospitalizationBundle.putString(Hospitalization.UUID, caze.getHospitalization().getUuid());
                frag.setArguments(hospitalizationBundle);
                break;
            case EPIDATA:
                frag = new EpiDataForm();

                Bundle epiDataBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                epiDataBundle.putSerializable(Case.DISEASE, caze.getDisease());
                epiDataBundle.putString(EpiData.UUID, caze.getEpiData().getUuid());
                frag.setArguments(epiDataBundle);
                break;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return CaseEditTabs.values()[position].toString();
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return CaseEditTabs.values().length;
    }
}
