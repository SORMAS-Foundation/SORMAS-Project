package de.symeda.sormas.app.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.visit.VisitsListFragment;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class ContactEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle contactEditBundle; // this contactEditBundle contains the uuids
    private ContactEditDataForm contactEditDataForm;
    private PersonEditForm personEditForm;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ContactEditPagerAdapter(FragmentManager fm, String contactUuid) {
        super(fm);
        contactEditBundle = new Bundle();
        contactEditBundle.putString(Contact.UUID, contactUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        ContactEditTabs tab = ContactEditTabs.values()[position];
        switch (tab) {
            case CONTACT_DATA:
                contactEditDataForm = new ContactEditDataForm();
                contactEditDataForm.setArguments(contactEditBundle);
                frag = contactEditDataForm;
                break;

            case PERSON:
                personEditForm = new PersonEditForm();

                Bundle personEditBundle = new Bundle();
                Contact contact = DatabaseHelper.getContactDao().queryUuid(contactEditBundle.getString(Contact.UUID));
                personEditBundle.putString(Person.UUID, contact.getPerson().getUuid());
                personEditBundle.putSerializable(Case.DISEASE, contact.getCaze().getDisease());

                personEditForm.setArguments(personEditBundle);
                frag = personEditForm;
                break;

            case VISITS:
                VisitsListFragment visitListTab = new VisitsListFragment();
                visitListTab.setArguments(contactEditBundle);
                frag = visitListTab;
                break;
            case TASKS:
                TasksListFragment tasksListTab = new TasksListFragment();
                Bundle tasksListBundle = new Bundle();
                tasksListBundle.putString(TasksListFragment.KEY_CONTACT_UUID, (String)contactEditBundle.get(Contact.UUID));
                tasksListTab.setArguments(tasksListBundle);
                frag = tasksListTab;
                break;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return ContactEditTabs.values()[position].toString();
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return ContactEditTabs.values().length;
    }

    public AbstractDomainObject getData(int position) {
        ContactEditTabs tab = ContactEditTabs.values()[position];
        AbstractDomainObject ado = null;
        switch (tab) {
            case CONTACT_DATA:
                ado= contactEditDataForm.getData();
                break;
            case PERSON:
                ado = personEditForm.getData();
                break;
            case VISITS:
                ado = null;
                break;
        }
        return ado;
    }
}
