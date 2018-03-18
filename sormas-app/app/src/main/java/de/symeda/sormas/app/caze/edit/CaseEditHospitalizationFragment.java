package de.symeda.sormas.app.caze.edit;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditHospitalizationFragment extends BaseEditActivityFragment<FragmentCaseEditHospitalizationLayoutBinding, Hospitalization> {

    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private Hospitalization record;
    private OnTeboSwitchCheckedChangeListener onAdmittedToFacilityCheckedCallback;
    private OnTeboSwitchCheckedChangeListener onIsolationCheckedCallback;
    private OnTeboSwitchCheckedChangeListener onPreviousHospitalizationCheckedCallback;
    private int mAdmittedToFacilityLastCheckedId = -1;
    private int mIsolationLastCheckedId = -1;
    private int mPreviousHospitalizationLastCheckedId = -1;
    private IEntryItemOnClickListener onAddEntryClickListener;
    private IEntryItemOnClickListener onPrevHosItemClickListener;
    private ObservableArrayList preHospitalizations = new ObservableArrayList();

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
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Hospitalization hospitalization = null;
            Case caze = DatabaseHelper.getCaseDao().queryUuid(recordUuid);
            if (caze != null)
                hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid());

            resultHolder.forItem().add(hospitalization);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setIsolationCallback(onIsolationCheckedCallback);
        contentBinding.setAdmittedToFacilityCallback(onAdmittedToFacilityCheckedCallback);
        contentBinding.setPreviousHospitalizationCallback(onPreviousHospitalizationCheckedCallback);
        contentBinding.setPreviousHospitalizationList(getPreviousHospitalizations());
        contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
        contentBinding.setAddEntryClickCallback(onAddEntryClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_hospitalization_layout;
    }

    private void setupCallback() {
        onAdmittedToFacilityCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mAdmittedToFacilityLastCheckedId == checkedId) {
                    return;
                }

                mAdmittedToFacilityLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().ctrlAdmission.setVisibility(View.VISIBLE);
                    getContentBinding().ctrlIsolation.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().ctrlAdmission.setVisibility(View.GONE);
                    getContentBinding().ctrlIsolation.setVisibility(View.GONE);
                }
            }
        };


        onIsolationCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mIsolationLastCheckedId == checkedId) {
                    return;
                }

                mIsolationLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().dtpIsolationDate.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().dtpIsolationDate.setVisibility(View.GONE);
                }

            }
        };


        onPreviousHospitalizationCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mPreviousHospitalizationLastCheckedId == checkedId) {
                    return;
                }

                mPreviousHospitalizationLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().ctrlPrevHospitalization.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().ctrlPrevHospitalization.setVisibility(View.GONE);
                }

            }
        };


        onPrevHosItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };

        onAddEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final PreviousHospitalization hospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        ObservableArrayList newPreHospitalizations = new ObservableArrayList();
                        preHospitalizations.addAll(preHospitalizations);
                        preHospitalizations.add(0, hospitalization);
                        getContentBinding().setPreviousHospitalizationList(newPreHospitalizations);
                    }
                });

                //results.add(0, MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getPreviousHospitalizations(20).get(new Random().nextInt(10)));
            }
        };
    }

    private ObservableArrayList getPreviousHospitalizations() {
        if (record != null && preHospitalizations != null)
            preHospitalizations.addAll(record.getPreviousHospitalizations());

        return preHospitalizations;
    }

    public static CaseEditHospitalizationFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditHospitalizationFragment.class, capsule);
    }

}
