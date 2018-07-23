package de.symeda.sormas.app.visit.edit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.validation.VisitValidator;

public class VisitEditFragment extends BaseEditFragment<FragmentVisitEditLayoutBinding, Visit, Visit> {

    private Visit record;
    private Contact contact;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_followup_information);
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        String contactUuid = savedInstanceState != null ? getContactUuidArg(savedInstanceState)
                : getContactUuidArg(getArguments());
        if (contactUuid != null) {
            contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLayoutBinding(FragmentVisitEditLayoutBinding contentBinding) {
        contentBinding.setData(record);

        if (isLiveValidationDisabled()) {
            disableLiveValidation(true);
        }

        VisitValidator.initializeVisitValidation(contact, contentBinding);

        contentBinding.setVisitStatusClass(VisitStatus.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentVisitEditLayoutBinding contentBinding) {
        contentBinding.visitVisitDateTime.initializeDateTimeField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_visit_edit_layout;
    }

    public static VisitEditFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitEditFragment.class, capsule, activityRootData);
    }

}
