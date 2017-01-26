package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.contact.ContactsListFragment;
import de.symeda.sormas.app.person.PersonEditTab;
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
        AbstractDomainObject ado = null;
        switch (position) {
            case 0:
                System.out.println(caseEditDataTab != null);
                ado= caseEditDataTab.getData();
                break;
            case 1:
                ado = personEditTab.getData();
                break;
            case 2:
                ado = symptomsEditTab.getData();
                break;
        }
        return ado;
    }

    public FormTab getTabByPosition(int position) {
        CaseEditTabs tab = CaseEditTabs.values()[position];
        switch(tab) {
            case CASE_DATA:
                return caseEditDataTab;
            case PATIENT:
                return personEditTab;
            case SYMPTOMS:
                return symptomsEditTab;
        }
        return null;
    }
}
