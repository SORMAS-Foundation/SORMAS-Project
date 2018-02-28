package de.symeda.sormas.app.caze.edit;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditHospitalizationFragment extends BaseEditActivityFragment<FragmentCaseEditHospitalizationLayoutBinding> {

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
    private ObservableArrayList results = new ObservableArrayList();

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

        record = MemoryDatabaseHelper.HOSPITALIZATION.getHospitalizations(1).get(0);

        loadPreviousHospitalizations();
        setupCallback();
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setIsolationCallback(onIsolationCheckedCallback);
        contentBinding.setAdmittedToFacilityCallback(onAdmittedToFacilityCheckedCallback);
        contentBinding.setPreviousHospitalizationCallback(onPreviousHospitalizationCheckedCallback);
        contentBinding.setPreviousHospitalizationList(results);
        contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
        contentBinding.setAddEntryClickCallback(onAddEntryClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding binding) {
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
                //AlertDialog dialog2 = new ConfirmationDialog(CaseEditActivity.getActiveActivity()).show();
                final PreviousHospitalization hospitalization = MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(1).get(0);
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        results.add(0, hospitalization);
                    }
                });

                //results.add(0, MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(20).get(new Random().nextInt(10)));
            }
        };
    }

    private void loadPreviousHospitalizations() {
        PreviousHospitalization item = MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(1).get(0);
        item.setRegion(null);
        item.setCommunity(null);
        item.setDescription(null);
        results.add(item);
    }

    public static CaseEditHospitalizationFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseEditHospitalizationFragment.class, capsule);
    }

}
