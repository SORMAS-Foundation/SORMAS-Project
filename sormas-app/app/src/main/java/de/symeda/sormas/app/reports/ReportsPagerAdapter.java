package de.symeda.sormas.app.reports;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.util.FormTab;

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
                Bundle params = new Bundle();
                params.putSerializable(FormTab.EDIT_OR_CREATE_USER_RIGHT, UserRight.WEEKLYREPORT_EDIT);
                frag.setArguments(params);
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
