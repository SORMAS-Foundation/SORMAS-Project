package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.caze.CasesListFragment;


/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class EventsListFilterAdapter extends FragmentStatePagerAdapter {

    private EventStatus statusFilters[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public EventsListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new EventStatus[] { EventStatus.POSSIBLE , EventStatus.CONFIRMED, EventStatus.NO_EVENT };
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        EventsListFragment eventsListFragment = new EventsListFragment();
        Bundle arguments = new Bundle();
        EventStatus status = statusFilters[position];
        if (status != null) {
            arguments.putSerializable(EventsListFragment.ARG_FILTER_STATUS, status);
        }
        else {
            arguments.remove(EventsListFragment.ARG_FILTER_STATUS);
        }
        eventsListFragment.setArguments(arguments);
        return eventsListFragment;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        EventStatus status = statusFilters[position];
        if (status != null) {
            return status.toString();
        }
        return "All";
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return statusFilters.length;
    }
}
