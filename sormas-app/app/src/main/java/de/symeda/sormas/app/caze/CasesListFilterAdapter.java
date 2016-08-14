package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ListView;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CasesListFilterAdapter extends FragmentStatePagerAdapter {

    private CaseStatus statusFilters[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CasesListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new CaseStatus[] { CaseStatus.POSSIBLE, null };
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        CasesListFragment casesListFragment = new CasesListFragment();
        Bundle arguments = new Bundle();
        CaseStatus caseStatus = statusFilters[position];
        if (caseStatus != null) {
            arguments.putSerializable(CasesListFragment.ARG_FILTER_STATUS, caseStatus);
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
        CaseStatus caseStatus = statusFilters[position];
        if (caseStatus != null) {
            return caseStatus.toString();
        }
        return "All";
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return statusFilters.length;
    }
}
