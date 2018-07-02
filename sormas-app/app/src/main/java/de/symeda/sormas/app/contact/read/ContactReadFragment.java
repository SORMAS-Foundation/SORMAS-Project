package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.databinding.FragmentContactReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

public class ContactReadFragment extends BaseReadFragment<FragmentContactReadLayoutBinding, Contact, Contact> {

    private Contact record;
    private Case associatedCase;
    private OnLinkClickListener openCaseLinkCallback;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());
    }

    @Override
    public void onLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {

        setupCallback();

        setVisibilityByDisease(ContactDto.class, record.getCaseDisease(), contentBinding.mainContent);

        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
        contentBinding.setData(record);
        contentBinding.setCaze(associatedCase);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_contact_information);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_layout;
    }

    private void setupCallback() {

        openCaseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (associatedCase != null) {
                    CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                            associatedCase.getUuid(), associatedCase.getCaseClassification());
                    CaseReadActivity.goToActivity(getActivity(), dataCapsule);
                }
            }
        };
    }

    public static ContactReadFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactReadFragment.class, capsule, activityRootData);
    }
}
