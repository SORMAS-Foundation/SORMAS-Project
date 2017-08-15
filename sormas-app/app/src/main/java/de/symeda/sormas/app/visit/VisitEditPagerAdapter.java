package de.symeda.sormas.app.visit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.caze.SymptomsEditForm;
import de.symeda.sormas.app.util.FormTab;

/**
 * @see <a href="http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html">www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html</a>
 */

public class VisitEditPagerAdapter extends FragmentStatePagerAdapter {

    private Bundle visitEditBundle;

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
                frag = new VisitEditDataForm();
                frag.setArguments(visitEditBundle);
                break;

            case SYMPTOMS:
                frag = new SymptomsEditForm();

                Bundle symptomsEditBundle = new Bundle();
                // build new symptoms for new visit
                if(visitEditBundle.getBoolean(VisitEditDataForm.NEW_VISIT)) {
                    String keyContactUuid = visitEditBundle.getString(VisitEditDataForm.KEY_CONTACT_UUID);
                    Contact contact = DatabaseHelper.getContactDao().queryUuid(keyContactUuid);
                    symptomsEditBundle.putSerializable(Visit.DISEASE, contact.getCaze().getDisease());
                    symptomsEditBundle.putBoolean(SymptomsEditForm.NEW_SYMPTOMS, true);
                    symptomsEditBundle.putBoolean(SymptomsEditForm.FOR_VISIT, true);
                }
                // edit symptoms for given visit
                else {
                    String visitUuid = visitEditBundle.getString(Visit.UUID);
                    Visit visit = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
                    symptomsEditBundle.putString(Symptoms.UUID, visit.getSymptoms().getUuid());
                    symptomsEditBundle.putSerializable(Visit.DISEASE, visit.getDisease());
                    symptomsEditBundle.putBoolean(SymptomsEditForm.FOR_VISIT, true);
                    symptomsEditBundle.putBoolean(SymptomsEditForm.VISIT_COOPERATIVE,
                            visit.getVisitStatus() == VisitStatus.COOPERATIVE);
                }

                frag.setArguments(symptomsEditBundle);
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
}
