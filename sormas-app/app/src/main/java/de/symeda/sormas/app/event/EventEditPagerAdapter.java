package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.task.TasksListFragment;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class EventEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle eventEditBundle; // this bundle contains the uuids
    private EventEditDataForm eventEditDataForm;
    private PersonEditForm personEditForm;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public EventEditPagerAdapter(FragmentManager fm, String eventUuid) {
        super(fm);
        eventEditBundle = new Bundle();
        eventEditBundle.putString(Event.UUID, eventUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        EventEditTabs tab = EventEditTabs.values()[position];
        switch (tab) {
            case EVENT_DATA:
                eventEditDataForm = new EventEditDataForm();
                eventEditDataForm.setArguments(eventEditBundle);
                frag = eventEditDataForm;
                break;

            case EVENT_PERSONS:
                EventParticipantsListFragment eventParticipantsListTab = new EventParticipantsListFragment();
                eventParticipantsListTab.setArguments(eventEditBundle);
                frag = eventParticipantsListTab;
                break;

            case EVENT_TASKS:
                TasksListFragment tasksListFragment = new TasksListFragment();
                Bundle tasksListBundle = new Bundle();
                tasksListBundle.putString(TasksListFragment.KEY_EVENT_UUID, (String) eventEditBundle.get(Event.UUID));
                tasksListFragment.setArguments(tasksListBundle);
                frag = tasksListFragment;
                break;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return EventEditTabs.values()[position].toString();
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        if (eventEditBundle != null && eventEditBundle.get(Event.UUID) != null) {
            return EventEditTabs.values().length;
        } else {
            return 1; // this is a hotfix to make sure that the event persons tab is not displayed when creating a new event and should be replaced asap
        }
    }

    public AbstractDomainObject getData(int position) {
        EventEditTabs tab = EventEditTabs.values()[position];
        AbstractDomainObject ado = null;
        switch (tab) {
            case EVENT_DATA:
                ado= eventEditDataForm.getData();
                break;
//            case EVENT_PERSONS:
//                ado = personEditForm.getData();
//                break;
        }
        return ado;
    }
}
