package de.symeda.sormas.app.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.caze.CasesListFragment;

/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class TasksListFilterAdapter extends FragmentStatePagerAdapter {

    private TaskStatus statusFilters[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public TasksListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new TaskStatus[] { TaskStatus.PENDING, TaskStatus.DONE, TaskStatus.DISCARDED, null };
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        TasksListFragment tasksListFragment = new TasksListFragment();
        Bundle arguments = new Bundle();
        TaskStatus taskStatus = statusFilters[position];
        if (taskStatus != null) {
            arguments.putSerializable(TasksListFragment.ARG_FILTER_STATUS, taskStatus);
        }
        else {
            arguments.remove(CasesListFragment.ARG_FILTER_STATUS);
        }
        tasksListFragment.setArguments(arguments);
        return tasksListFragment;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        TaskStatus taskStatus = statusFilters[position];
        if (taskStatus != null) {
            return taskStatus.toString();
        }
        return "All";
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return statusFilters.length;
    }
}
