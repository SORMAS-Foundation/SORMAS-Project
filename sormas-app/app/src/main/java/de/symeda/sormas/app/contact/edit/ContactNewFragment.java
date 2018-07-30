package de.symeda.sormas.app.contact.edit;

import java.util.List;

import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class ContactNewFragment extends BaseEditFragment<FragmentContactNewLayoutBinding, Contact, Contact> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Contact record;

    private List<Item> relationshipList;

    public static ContactNewFragment newInstance(Contact activityRootData) {
        return newInstance(ContactNewFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_contact);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        relationshipList = DataUtils.getEnumItems(ContactRelation.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        if (isLiveValidationDisabled()) {
            disableLiveValidation(true);
        }

        contentBinding.setContactProximityClass(ContactProximity.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.contactRelationToCase.initializeSpinner(relationshipList);
        contentBinding.contactLastContactDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_new_layout;
    }
}
