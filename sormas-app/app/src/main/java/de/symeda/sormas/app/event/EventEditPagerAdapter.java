package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class EventEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle eventEditBundle; // this bundle contains the uuids
    private List<EventEditTabs> visibleTabs;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public EventEditPagerAdapter(FragmentManager fm, String eventUuid, List<EventEditTabs> visibleTabs) {
        super(fm);
        eventEditBundle = new Bundle();
        eventEditBundle.putString(Event.UUID, eventUuid);
        this.visibleTabs = visibleTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        EventEditTabs tab = visibleTabs.get(position);
        switch (tab) {
            case EVENT_DATA:
                frag = new EventEditDataForm();
                eventEditBundle.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.EVENT_EDIT);
                frag.setArguments(eventEditBundle);
                break;

            case EVENT_PERSONS:
                EventParticipantsListFragment eventParticipantsListTab = new EventParticipantsListFragment();
                eventEditBundle.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.EVENT_EDIT);
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
        return visibleTabs.get(position).toString();
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        if (eventEditBundle != null && eventEditBundle.get(Event.UUID) != null) {
            return visibleTabs.size();
        } else {
            return 1; // this is a hotfix to make sure that the event persons tab is not displayed when creating a new event and should be replaced asap
        }
    }

    public EventEditTabs getTabForPosition(int position) {
        return visibleTabs.get(position);
    }

    public int getPositionOfTab(EventEditTabs tab) {
        return visibleTabs.indexOf(tab);
    }
}
