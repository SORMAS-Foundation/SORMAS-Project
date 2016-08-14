package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class CaseEditPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private Bundle caseEditBundle; // this bundle contains the uuids
    private CaseEditDataTab caseEditDataTab;
    private CaseEditPersonTab caseEditPersonTab;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CaseEditPagerAdapter(FragmentManager fm, CharSequence mTitles[], String caseUuid) {
        super(fm);
        this.titles = mTitles;
        caseEditBundle = new Bundle();
        caseEditBundle.putString(Case.UUID, caseUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                caseEditDataTab = new CaseEditDataTab();
                caseEditDataTab.setArguments(caseEditBundle);
                frag = caseEditDataTab;
                break;
            case 1:
                caseEditPersonTab = new CaseEditPersonTab();
                caseEditPersonTab.setArguments(caseEditBundle);
                frag = caseEditPersonTab;
                break;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return titles.length;
    }

    public AbstractDomainObject getData(int position) {
        AbstractDomainObject ado = null;
        switch (position) {
            case 0:
                ado= caseEditDataTab.getData();
                break;
            case 1:
                ado = caseEditPersonTab.getData();
                break;
        }
        return ado;
    }
}
