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
import de.symeda.sormas.app.epidata.EpiDataTab;
import de.symeda.sormas.app.hospitalization.HospitalizationTab;
import de.symeda.sormas.app.person.PersonEditTab;
import de.symeda.sormas.app.sample.SamplesListFragment;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CaseEditPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private Bundle caseEditBundle; // this bundle contains the uuids
    private CaseEditDataTab caseEditDataTab;
    private PersonEditTab personEditTab;
    private SymptomsEditTab symptomsEditTab;
    private HospitalizationTab hospitalizationTab;
    private EpiDataTab epiDataTab;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CaseEditPagerAdapter(FragmentManager fm, String caseUuid) {
        super(fm);
        caseEditBundle = new Bundle();
        caseEditBundle.putString(Case.UUID, caseUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        CaseEditTabs tab = CaseEditTabs.values()[position];
        Case caze = null;
        switch (tab) {
            case CASE_DATA:
                caseEditDataTab = new CaseEditDataTab();
                caseEditDataTab.setArguments(caseEditBundle);
                frag = caseEditDataTab;
                break;
            case PATIENT:
                personEditTab = new PersonEditTab();

                Bundle personEditBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                personEditBundle.putString(Person.UUID, caze.getPerson().getUuid());

                personEditTab.setArguments(personEditBundle);
                frag = personEditTab;
                break;
            case SYMPTOMS:
                symptomsEditTab = new SymptomsEditTab();

                Bundle symptomsEditBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                symptomsEditBundle.putString(Symptoms.UUID, caze.getSymptoms().getUuid());
                symptomsEditBundle.putSerializable(Case.DISEASE, caze.getDisease());

                symptomsEditTab.setArguments(symptomsEditBundle);
                frag = symptomsEditTab;
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
                hospitalizationTab = new HospitalizationTab();

                Bundle hospitalizationBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                hospitalizationBundle.putString(HospitalizationTab.KEY_CASE_UUID, caze.getUuid());
                hospitalizationBundle.putString(Hospitalization.UUID, caze.getHospitalization().getUuid());

                hospitalizationTab.setArguments(hospitalizationBundle);
                frag = hospitalizationTab;
                break;
            case EPIDATA:
                epiDataTab = new EpiDataTab();

                Bundle epiDataBundle = new Bundle();
                caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                epiDataBundle.putSerializable(Case.DISEASE, caze.getDisease());
                epiDataBundle.putString(EpiData.UUID, caze.getEpiData().getUuid());

                epiDataTab.setArguments(epiDataBundle);
                frag = epiDataTab;
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

    public AbstractDomainObject getData(int position) {
        CaseEditTabs tab = CaseEditTabs.fromInt(position);
        switch (tab) {
            case CASE_DATA:
                return caseEditDataTab.getData();
            case PATIENT:
                return personEditTab.getData();
            case HOSPITALIZATION:
                return hospitalizationTab.getData();
            case SYMPTOMS:
                return symptomsEditTab.getData();
            case EPIDATA:
                return epiDataTab.getData();
        }
        return null;
    }

    public FormTab getTabByPosition(int position) {
        CaseEditTabs tab = CaseEditTabs.fromInt(position);
        switch(tab) {
            case CASE_DATA:
                return caseEditDataTab;
            case PATIENT:
                return personEditTab;
            case HOSPITALIZATION:
                return hospitalizationTab;
            case SYMPTOMS:
                return symptomsEditTab;
            case EPIDATA:
                return epiDataTab;
        }
        return null;
    }
}
