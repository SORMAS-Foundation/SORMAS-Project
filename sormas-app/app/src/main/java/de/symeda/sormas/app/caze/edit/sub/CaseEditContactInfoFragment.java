package de.symeda.sormas.app.caze.edit.sub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 19/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditContactInfoFragment extends BaseEditActivityFragment<FragmentContactEditLayoutBinding, Contact> {

    private String recordUuid = null;
    private ContactClassification pageStatus = null;
    private Contact record;
    private Case associatedCase;
    private View.OnClickListener createCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

    private List<Item> relationshipList;


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
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_case_contact_info);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid != null && !recordUuid.isEmpty()) {
                Contact contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
                resultHolder.forItem().add(contact);

                if (contact != null) {
                    resultHolder.forItem().add(findAssociatedCase(contact.getPerson(), contact.getCaze().getDisease()));
                }
            }

            resultHolder.forOther().add(DataUtils.getEnumItems(ContactRelation.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (itemIterator.hasNext())
                associatedCase = itemIterator.next();

            if (otherIterator.hasNext())
                relationshipList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
        //FieldHelper.initSpinnerField(binding.contactContactClassification, ContactClassification.class);
        //FieldHelper.initSpinnerField(binding.contactContactStatus, ContactStatus.class);
        //FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);

        if (associatedCase == null) {
            contentBinding.associatedCaseLayout.setVisibility(View.GONE);
        } else {
            contentBinding.contactPageBottomCtrlPanel.setVisibility(View.GONE);
        }

        setVisibilityByDisease(ContactDto.class, record.getCaze().getDisease(), contentBinding.mainContent);

        //contentBinding.contactLastContactDate.makeFieldSoftRequired();
        //contentBinding.contactContactProximity.makeFieldSoftRequired();
        //contentBinding.contactRelationToCase.makeFieldSoftRequired();

        contentBinding.setData(record);
        contentBinding.setContactProximityClass(ContactProximity.class);
        contentBinding.setCreateCaseCallback(createCaseCallback);
        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
        contentBinding.spnContactRelationship.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (relationshipList.size() > 0) ? DataUtils.addEmptyItem(relationshipList)
                        : relationshipList;
            }
        });


        contentBinding.dtpDateOfLastContact.initialize(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_layout;
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

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseEditContactInfoFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditContactInfoFragment.class, capsule);
    }

}
