package de.symeda.sormas.app.contact.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadLayoutBinding;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadFragment extends BaseReadActivityFragment<FragmentContactReadLayoutBinding, Contact> {

    private String recordUuid = null;
    private ContactClassification pageStatus = null;
    private Contact record;
    private Case associatedCase;
    private View.OnClickListener createCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                resultHolder.forItem().add(DatabaseHelper.getContactDao().build());
                resultHolder.forItem().add(DatabaseHelper.getCaseDao().build());
            } else {
                Case caze = null;
                Contact contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
                resultHolder.forItem().add(contact);

                if (contact != null)
                    caze = findAssociatedCase(contact.getPerson(), contact.getCaze().getDisease());

                resultHolder.forItem().add(caze);
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (itemIterator.hasNext())
                associatedCase = itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
        if (associatedCase == null) {
            contentBinding.associatedCaseLayout.setVisibility(View.GONE);
        } else {
            contentBinding.contactPageBottomCtrlPanel.setVisibility(View.GONE);
        }

        setVisibilityByDisease(ContactDto.class, record.getCaze().getDisease(), contentBinding.mainContent);

        contentBinding.setCreateCaseCallback(createCaseCallback);
        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
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

    private Case findAssociatedCase(Person person, Disease disease) {
        if(person == null || disease == null) {
            return null;
        }

        Case caze = DatabaseHelper.getCaseDao().getByPersonAndDisease(person, disease);
        if (caze != null) {
            return caze;
        } else {
            return null;
        }
    }

    private void setupCallback() {
        createCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(getContext(),
                        "").setContactUuid(recordUuid);
                CaseNewActivity.goToActivity(getContext(), dataCapsule);
            }
        };

        openCaseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (associatedCase != null) {
                    CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                            associatedCase.getUuid()).setReadPageStatus(associatedCase.getCaseClassification());
                    CaseReadActivity.goToActivity(getActivity(), dataCapsule);
                }
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadFragment.class, capsule);
    }

}
