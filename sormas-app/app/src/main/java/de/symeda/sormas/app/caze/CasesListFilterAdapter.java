package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ListView;

import de.symeda.sormas.api.caze.InvestigationStatus;


/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CasesListFilterAdapter extends FragmentStatePagerAdapter {

    private InvestigationStatus statusFilters[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CasesListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new InvestigationStatus[] { InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED };
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        CasesListFragment casesListFragment = new CasesListFragment();
        Bundle arguments = new Bundle();
        InvestigationStatus investigationStatus = statusFilters[position];
        if (investigationStatus != null) {
            arguments.putSerializable(CasesListFragment.ARG_FILTER_STATUS, investigationStatus);
        }
        else {
            arguments.remove(CasesListFragment.ARG_FILTER_STATUS);
        }
        casesListFragment.setArguments(arguments);
        return casesListFragment;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        InvestigationStatus status = statusFilters[position];
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
