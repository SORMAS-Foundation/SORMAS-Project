package de.symeda.sormas.app.contact.read;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentContactReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

import static android.view.View.GONE;

public class ContactReadFragment extends BaseReadFragment<FragmentContactReadLayoutBinding, Contact, Contact> {

    private Contact record;
    private Case sourceCase;
    private Case resultingCase = null;

    // Instance methods

    public static ContactReadFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactReadFragment.class, capsule, activityRootData);
    }

    private void setUpControlListeners(FragmentContactReadLayoutBinding contentBinding) {
        contentBinding.openSourceCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                        sourceCase.getUuid(), sourceCase.getCaseClassification());
                CaseReadActivity.goToActivity(getActivity(), dataCapsule);
            }
        });

        contentBinding.openResultingCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                        resultingCase.getUuid(), resultingCase.getCaseClassification());
                CaseReadActivity.goToActivity(getActivity(), dataCapsule);
            }
        });
    }

    private void setUpFieldVisibilities(FragmentContactReadLayoutBinding contentBinding) {
        setVisibilityByDisease(ContactDto.class, sourceCase.getDisease(), contentBinding.mainContent);

        if (resultingCase == null) {
            contentBinding.openResultingCase.setVisibility(GONE);
        }
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        sourceCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());

        if (record.getResultingCaseUuid() != null) {
            resultingCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid());
        }
    }

    @Override
    public void onLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setCaze(sourceCase);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_contact_information);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_layout;
    }

}
