package de.symeda.sormas.app.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Mate Strysewske on 20.01.2017.
 */

public class ContactsListFilterAdapter extends FragmentStatePagerAdapter {

    private FollowUpStatus statusFilters[];

    public ContactsListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new FollowUpStatus[] { FollowUpStatus.FOLLOW_UP, FollowUpStatus.COMPLETED, FollowUpStatus.CANCELED, FollowUpStatus.LOST,FollowUpStatus.NO_FOLLOW_UP };
    }

    @Override
    public Fragment getItem(int position) {
        ContactsListFragment contactsListFragment = new ContactsListFragment();
        Bundle arguments = new Bundle();
        FollowUpStatus followUpStatus = statusFilters[position];
        if(followUpStatus != null) {
            arguments.putSerializable(ContactsListFragment.ARG_FILTER_STATUS, followUpStatus);
        } else {
            arguments.remove(ContactsListFragment.ARG_FILTER_STATUS);
        }
        contactsListFragment.setArguments(arguments);
        return contactsListFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        FollowUpStatus status = statusFilters[position];
        if(status != null) {
            return status.toString();
        }
        return "All";
    }

    @Override
    public int getCount() {
        return statusFilters.length;
    }

}
