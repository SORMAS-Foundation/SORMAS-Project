package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.contact.ContactsListFragment;
import de.symeda.sormas.app.person.PersonEditTab;
import de.symeda.sormas.app.task.TasksListFragment;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CaseEditPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private Bundle caseEditBundle; // this bundle contains the uuids
    private CaseEditDataTab caseEditDataTab;
    private PersonEditTab personEditTab;
    private CaseEditSymptomsTab caseEditSymptomsTab;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CaseEditPagerAdapter(FragmentManager fm, CharSequence mTitles[], String caseUuid) {
        super(fm);
        this.titles = mTitles;
        caseEditBundle = new Bundle();
        caseEditBundle.putString(Case.UUID, caseUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                caseEditDataTab = new CaseEditDataTab();
                caseEditDataTab.setArguments(caseEditBundle);
                frag = caseEditDataTab;
                break;
            case 1:
                personEditTab = new PersonEditTab();

                Bundle personEditBundle = new Bundle();
                Case caze = DatabaseHelper.getCaseDao().queryUuid(caseEditBundle.getString(Case.UUID));
                personEditBundle.putString(Person.UUID, caze.getPerson().getUuid());

                personEditTab.setArguments(personEditBundle);
                frag = personEditTab;
                break;
            case 2:
                caseEditSymptomsTab = new CaseEditSymptomsTab();
                caseEditSymptomsTab.setArguments(caseEditBundle);
                frag = caseEditSymptomsTab;
                break;
            case 3:
                ContactsListFragment contactsListTab = new ContactsListFragment();
                contactsListTab.setArguments(caseEditBundle);
                frag = contactsListTab;
                break;
            case 4:
                TasksListFragment tasksListTab = new TasksListFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("caseUuid", (String)caseEditBundle.get(Case.UUID));
                tasksListTab.setArguments(arguments);
                frag = tasksListTab;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return titles.length;
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
                ado = caseEditSymptomsTab.getData();
                break;
        }
        return ado;
    }
}
