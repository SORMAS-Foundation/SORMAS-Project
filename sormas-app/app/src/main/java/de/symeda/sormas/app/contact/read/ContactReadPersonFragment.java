package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.FragmentContactReadPersonInfoLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

public class ContactReadPersonFragment extends BaseReadFragment<FragmentContactReadPersonInfoLayoutBinding, Person, Contact> {

    private Person record;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Contact contact = getActivityRootData();
        record = contact.getPerson();
    }

    @Override
    public void onLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

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
    public int getReadLayout() {
        return R.layout.fragment_contact_read_person_info_layout;
    }

    public static ContactReadPersonFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactReadPersonFragment.class, capsule, activityRootData);
    }
}

