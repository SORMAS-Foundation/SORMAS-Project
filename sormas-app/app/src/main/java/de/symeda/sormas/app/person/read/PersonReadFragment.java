package de.symeda.sormas.app.person.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentPersonReadLayoutBinding;

public class PersonReadFragment extends BaseReadFragment<FragmentPersonReadLayoutBinding, Person, AbstractDomainObject> {

    public static final String TAG = PersonReadFragment.class.getSimpleName();

    private Person record;

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
    public void onLayoutBinding(FragmentPersonReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_patient_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_person_read_layout;
    }

    public static PersonReadFragment newInstance(BaseFormNavigationCapsule capsule, AbstractDomainObject activityRootData) {
        return newInstance(PersonReadFragment.class, capsule, activityRootData);
    }

}
