package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;


public class ContactEditFragment extends BaseEditFragment<FragmentContactEditLayoutBinding, Contact, Contact> {

    private Contact record;
    private Case associatedCase;

    private View.OnClickListener createCaseCallback;
    private View.OnClickListener openCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

    private List<Item> relationshipList;

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
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());

        relationshipList = DataUtils.getEnumItems(ContactRelation.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {

        setupCallback();

        updateBottonPanel();

        setVisibilityByDisease(ContactDto.class, record.getCaseDisease(), contentBinding.mainContent);

        contentBinding.setData(record);
        contentBinding.setCaze(associatedCase);
        contentBinding.setContactProximityClass(ContactProximity.class);
        contentBinding.setCreateCaseCallback(createCaseCallback);
        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
        contentBinding.setOpenCaseCallback(openCaseCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
        contentBinding.contactRelationToCase.initializeSpinner(relationshipList);


        contentBinding.contactLastContactDate.setFragmentManager(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_layout;
    }

    private void setupCallback() {
        createCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext())
                        .setContactUuid(record.getUuid()).setPersonUuid(record.getPerson().getUuid());
                CaseNewActivity.goToActivity(getContext(), dataCapsule);
            }
        };
        openCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCase();
            }
        };

        openCaseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                openCase();
            }
        };
    }

    private void openCase() {
        if (associatedCase != null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                    associatedCase.getUuid(), associatedCase.getCaseClassification());
            CaseEditActivity.goToActivity(getActivity(), dataCapsule);
        }
    }

    private void updateBottonPanel() {
        if (associatedCase == null) {
            getContentBinding().btnOpenCase.setVisibility(View.GONE);
        } else {
            getContentBinding().btnCreateCase.setVisibility(View.GONE);
        }

        if (getContentBinding().btnOpenCase.getVisibility() == View.GONE && getContentBinding().btnCreateCase.getVisibility() == View.GONE) {
            getContentBinding().contactPageBottomCtrlPanel.setVisibility(View.GONE);
        }
    }

    public static ContactEditFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactEditFragment.class, capsule, activityRootData);
    }
}
