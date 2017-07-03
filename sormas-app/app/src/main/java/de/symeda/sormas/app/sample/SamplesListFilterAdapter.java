package de.symeda.sormas.app.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.event.EventsListFragment;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SamplesListFilterAdapter extends FragmentStatePagerAdapter {

    private ShipmentStatus statusFilters[];

    public SamplesListFilterAdapter(FragmentManager fm) {
        super(fm);
        this.statusFilters = new ShipmentStatus[] {
                ShipmentStatus.NOT_SHIPPED, ShipmentStatus.SHIPPED,
                ShipmentStatus.RECEIVED, ShipmentStatus.REFERRED_OTHER_LAB
        };
    }

    @Override
    public Fragment getItem(int position) {
        SamplesListFragment samplesListFragment = new SamplesListFragment();
        Bundle arguments = new Bundle();
        ShipmentStatus status = statusFilters[position];
        if (status != null) {
            arguments.putSerializable(SamplesListFragment.ARG_FILTER_STATUS, status);
        } else {
            arguments.remove(SamplesListFragment.ARG_FILTER_STATUS);
        }
        samplesListFragment.setArguments(arguments);
        return samplesListFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ShipmentStatus status = statusFilters[position];
        if (status != null) {
            return status.toString();
        }
        return "All";
    }

    public int getCount() {
        return statusFilters.length;
    }

}
