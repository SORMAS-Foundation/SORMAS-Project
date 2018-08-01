package de.symeda.sormas.app.visit.edit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.util.Bundler;

public class VisitEditFragment extends BaseEditFragment<FragmentVisitEditLayoutBinding, Visit, Visit> {

    private Visit record;
    private String contactUuid;
    private Contact contact;

    public static VisitEditFragment newInstance(Visit activityRootData, String contactUuid) {
        return newInstance(VisitEditFragment.class, new Bundler().setContactUuid(contactUuid).get(), activityRootData);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) savedInstanceState = getArguments();
        contactUuid = new Bundler(savedInstanceState).getContactUuid();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setContactUuid(contactUuid);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_visit_information);
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
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
}
