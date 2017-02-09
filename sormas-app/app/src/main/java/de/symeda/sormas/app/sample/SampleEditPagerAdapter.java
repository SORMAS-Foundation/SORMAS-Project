package de.symeda.sormas.app.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public class SampleEditPagerAdapter extends FragmentStatePagerAdapter {

    public static final String CASE_UUID = "caseUuid";

    private SampleEditTab sampleEditTab;
    private Bundle sampleEditBundle;

    public SampleEditPagerAdapter(FragmentManager fm, String sampleUuid) {
        super(fm);
        sampleEditBundle = new Bundle();
        sampleEditBundle.putString(Sample.UUID, sampleUuid);
    }

    public SampleEditPagerAdapter(FragmentManager fm, String sampleUuid, String caseUuid) {
        super(fm);
        sampleEditBundle = new Bundle();
        sampleEditBundle.putString(Sample.UUID, sampleUuid);
        sampleEditBundle.putString(CASE_UUID, caseUuid);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        SampleEditTabs tab = SampleEditTabs.values()[position];
        Sample sample = null;
        switch(tab) {
            case SAMPLE_DATA:
                sampleEditTab = new SampleEditTab();
                sampleEditTab.setArguments(sampleEditBundle);
                frag = sampleEditTab;
                break;

        }
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return SampleEditTabs.values()[position].toString();
    }

    @Override
    public int getCount() {
        return SampleEditTabs.values().length;
    }

    public AbstractDomainObject getData(int position) {
        AbstractDomainObject ado = null;
        switch (position) {
            case 0:
                ado = sampleEditTab.getData();
                break;

        }
        return ado;
    }

    public FormTab getTabByPosition(int position) {
        SampleEditTabs tab = SampleEditTabs.values()[position];
        switch(tab) {
            case SAMPLE_DATA:
                return sampleEditTab;

        }
        return null;
    }

}
