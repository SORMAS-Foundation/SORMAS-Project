package de.symeda.sormas.app.caze.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditFragment extends BaseEditActivityFragment<FragmentCaseEditLayoutBinding, Case> {

    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private Case record;
    private List<Item> caseClassificationList;
    private List<Item> caseOutcomeList;

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
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                resultHolder.forItem().add(DatabaseHelper.getCaseDao().build());
            } else {
                resultHolder.forItem().add(DatabaseHelper.getCaseDao().queryUuid(recordUuid));
            }

            resultHolder.forOther().add(DataUtils.getEnumItems(CaseClassification.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(CaseOutcome.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (otherIterator.hasNext())
                caseClassificationList = otherIterator.next();

            if (otherIterator.hasNext())
                caseOutcomeList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
        contentBinding.spnCaseClassification.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (caseClassificationList.size() > 0) ? DataUtils.addEmptyItem(caseClassificationList)
                        : caseClassificationList;
            }
        });

        contentBinding.spnOutcome.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (caseOutcomeList.size() > 0) ? DataUtils.addEmptyItem(caseOutcomeList)
                        : caseOutcomeList;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                CaseOutcome caseOutcome = (CaseOutcome)value;

                if (caseOutcome == CaseOutcome.NO_OUTCOME || caseOutcome == null) {
                    getContentBinding().dtpDateOfOutcome.setVisibility(View.GONE);
                    return;
                }

                getContentBinding().dtpDateOfOutcome.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.dtpDateOfOutcome.initialize(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_layout;
    }

    private void setupCallback() {
        /*onEventTypeCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (mLastCheckedId == checkedId) {
                    return;
                }

                mLastCheckedId = checkedId;

            }
        };*/
    }

    public static CaseEditFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditFragment.class, capsule);
    }
}
