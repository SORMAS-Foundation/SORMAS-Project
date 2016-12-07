package de.symeda.sormas.app.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.CaseEditDataTab;
import de.symeda.sormas.app.caze.CaseEditPersonTab;
import de.symeda.sormas.app.caze.CaseEditSymptomsTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class ContactEditPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private Bundle bundle; // this bundle contains the uuids
    private ContactEditDataTab contactEditDataTab;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ContactEditPagerAdapter(FragmentManager fm, CharSequence mTitles[], String contactUuid) {
        super(fm);
        this.titles = mTitles;
        bundle = new Bundle();
        bundle.putString(Contact.UUID, contactUuid);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                contactEditDataTab = new ContactEditDataTab();
                contactEditDataTab.setArguments(bundle);
                frag = contactEditDataTab;
                break;
//            case 1:
//                contactEditDataTab = new ContactEditDataTab();
//                contactEditDataTab.setArguments(bundle);
//                frag = contactEditDataTab;
//                break;
//            case 2:
//                contactEditDataTab = new ContactEditDataTab();
//                contactEditDataTab.setArguments(bundle);
//                frag = contactEditDataTab;
//                break;

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
                ado= contactEditDataTab.getData();
                break;
            case 1:
                ado = null;
                break;
            case 2:
                ado = null;
                break;
        }
        return ado;
    }
}
