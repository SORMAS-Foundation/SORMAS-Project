package de.symeda.sormas.app.person.edit;

import android.content.res.Resources;
import android.os.Bundle;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;

public class PersonEditFragment extends BaseEditFragment<FragmentPersonEditLayoutBinding, Person, AbstractDomainObject> {

    public static final String TAG = PersonEditFragment.class.getSimpleName();

    private Person record;

    // Instance methods

    public static PersonEditFragment newInstance(BaseFormNavigationCapsule capsule, AbstractDomainObject activityRootData) {
        return newInstance(PersonEditFragment.class, capsule, activityRootData);
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_person_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        AbstractDomainObject ado = getActivityRootData();

        if (ado instanceof Case) {
            record = ((Case) ado).getPerson();
        } else if (ado instanceof Contact) {
            record = ((Contact) ado).getPerson();
        } else {
            throw new UnsupportedOperationException("ActivityRootData of class " + ado.getClass().getSimpleName()
                    + " does not support PersonReadFragment");
        }
    }

    @Override
    public void onLayoutBinding(FragmentPersonEditLayoutBinding contentBinding) {

    }

    @Override
    public void onAfterLayoutBinding(FragmentPersonEditLayoutBinding contentBinding) {

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_person_edit_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }



}
