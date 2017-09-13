package de.symeda.sormas.app.reports;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */

public class ReportsPagerAdapter extends FragmentStatePagerAdapter {

    public ReportsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        ReportTabs tab = ReportTabs.values()[position];
        switch (tab) {
            case WEEKLY_REPORT:
                frag = new WeeklyReportForm();
                break;
        }
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ReportTabs.values()[position].toString();
    }

    @Override
    public int getCount() {
        return ReportTabs.values().length;
    }

}
