package de.symeda.sormas.app.caze.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditFragment extends BaseEditActivityFragment<FragmentCaseEditLayoutBinding> {

    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private Case record;
    private List<CaseClassification> caseClassificationList;
    private List<CaseOutcome> caseOutcomeList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        //followUpStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return record;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {

        record = MemoryDatabaseHelper.CASE.getCases(1).get(0);
        caseClassificationList = MemoryDatabaseHelper.CASE_CLASSIFICATION.getCaseClassifications();
        caseOutcomeList = MemoryDatabaseHelper.CASE_OUTCOME.getCaseOutcomes();

        setupCallback();
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentCaseEditLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);


        //contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditLayoutBinding binding) {
        binding.spnCaseClassification.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (caseClassificationList.size() > 0) ? DataUtils.toItems(caseClassificationList)
                        : DataUtils.toItems(caseClassificationList, false);
            }
        });

        binding.spnOutcome.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (caseOutcomeList.size() > 0) ? DataUtils.toItems(caseOutcomeList)
                        : DataUtils.toItems(caseOutcomeList, false);
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

        binding.dtpDateOfOutcome.initialize(getFragmentManager());
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

    public static CaseEditFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseEditFragment.class, capsule);
    }
}
