package de.symeda.sormas.app.visit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.caze.SymptomsEditTab;
import de.symeda.sormas.app.util.FormTab;

/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class VisitEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle visitEditBundle;
    private VisitEditDataTab visitEditDataTab;
    private SymptomsEditTab symptomsEditTab;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public VisitEditPagerAdapter(FragmentManager fm, Bundle visitBundle) {
        super(fm);
        this.visitEditBundle = visitBundle;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        VisitEditTabs tab = VisitEditTabs.values()[position];
        switch (tab) {
            case VISIT_DATA:
                visitEditDataTab = new VisitEditDataTab();
                visitEditDataTab.setArguments(visitEditBundle);
                frag = visitEditDataTab;
                break;

            case SYMPTOMS:
                symptomsEditTab = new SymptomsEditTab();

                Bundle symptomsEditBundle = new Bundle();
                // create new symptoms for new visit
                if(visitEditBundle.getBoolean(VisitEditDataTab.NEW_VISIT)) {
                    String keyContactUuid = visitEditBundle.getString(VisitEditDataTab.KEY_CONTACT_UUID);
                    Contact contact = DatabaseHelper.getContactDao().queryUuid(keyContactUuid);
                    symptomsEditBundle.putSerializable(Visit.DISEASE, contact.getCaze().getDisease());
                    symptomsEditBundle.putBoolean(SymptomsEditTab.NEW_SYMPTOMS, true);
                }
                // edit symptoms for given visit
                else {
                    String visitUuid = visitEditBundle.getString(Visit.UUID);
                    Visit visit = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
                    symptomsEditBundle.putString(Symptoms.UUID, visit.getSymptoms().getUuid());
                    symptomsEditBundle.putSerializable(Visit.DISEASE, visit.getDisease());
                }

                symptomsEditTab.setArguments(symptomsEditBundle);
                frag = symptomsEditTab;
                break;
        }
        return frag;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return VisitEditTabs.values()[position].toString();
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return VisitEditTabs.values().length;
    }

    public AbstractDomainObject getData(int position) {
        VisitEditTabs tab = VisitEditTabs.values()[position];
        AbstractDomainObject ado = null;
        switch (tab) {
            case VISIT_DATA:
                ado= visitEditDataTab.getData();
                break;
            case SYMPTOMS:
                ado = symptomsEditTab.getData();
                break;
        }
        return ado;
    }

    public FormTab getTabByPosition(int position) {
        VisitEditTabs tab = VisitEditTabs.values()[position];
        switch(tab) {
            case VISIT_DATA:
                return visitEditDataTab;
            case SYMPTOMS:
                return symptomsEditTab;
        }
        return null;
    }
}
