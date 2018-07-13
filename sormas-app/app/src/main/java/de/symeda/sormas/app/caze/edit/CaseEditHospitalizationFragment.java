package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

public class CaseEditHospitalizationFragment extends BaseEditFragment<FragmentCaseEditHospitalizationLayoutBinding, Hospitalization, Case> {

    private Hospitalization record;
    private Case caze;

    private IEntryItemOnClickListener onAddEntryClickListener;
    private IEntryItemOnClickListener onPrevHosItemClickListener;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_hospitalization_information);
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        caze = getActivityRootData();
        record = caze.getHospitalization();
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {

        setupCallback();

        contentBinding.hospitalizationHealthFacility.setVisibility((caze.getHealthFacility() != null) ? View.VISIBLE : View.GONE);

        if (caze.getHealthFacility() != null) {
            boolean otherHealthFacility = caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
            boolean noneHealthFacility = caze.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

            if (otherHealthFacility) {
                contentBinding.hospitalizationHealthFacilityDetails.setVisibility(View.VISIBLE);
            } else if (noneHealthFacility) {
                contentBinding.hospitalizationHealthFacilityDetails.setVisibility(View.VISIBLE);
            } else {
                contentBinding.hospitalizationHealthFacilityDetails.setVisibility(View.GONE);
            }
        } else {
            contentBinding.hospitalizationHealthFacilityDetails.setVisibility(View.GONE);
        }

        contentBinding.setData(record);
        contentBinding.setCaze(caze);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setPreviousHospitalizationList(getPreviousHospitalizations());
        contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
        contentBinding.setAddEntryClickCallback(onAddEntryClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
        contentBinding.hospitalizationAdmissionDate.initializeDateField(getFragmentManager());
        contentBinding.hospitalizationDischargeDate.initializeDateField(getFragmentManager());
        contentBinding.hospitalizationIsolationDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_hospitalization_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    private void setupCallback() {

        onPrevHosItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final PreviousHospitalization hospitalization = (PreviousHospitalization) item;
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updatePreviousHospitalizations((PreviousHospitalization) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removePreviousHospitalizations((PreviousHospitalization) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onAddEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final PreviousHospitalization hospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addPreviousHospitalizations((PreviousHospitalization) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removePreviousHospitalizations((PreviousHospitalization) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);

                //results.add(0, MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getPreviousHospitalizations(20).get(new Random().nextInt(10)));
            }
        };
    }

    private ObservableArrayList getPreviousHospitalizations() {
        ObservableArrayList newPreHospitalizations = new ObservableArrayList();
        if (record != null)
            newPreHospitalizations.addAll(record.getPreviousHospitalizations());

        return newPreHospitalizations;
    }

    private void removePreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        record.getPreviousHospitalizations().remove(item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void updatePreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        //record.getPreviousHospitalizations().remove(item);
        //record.getPreviousHospitalizations().add(0, (PreviousHospitalization)item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void addPreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        record.getPreviousHospitalizations().add(0, (PreviousHospitalization) item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void verifyPrevHospitalizationStatus() {
        YesNoUnknown hospitalizedPreviously = record.getAdmittedToHealthFacility();
        if (hospitalizedPreviously == YesNoUnknown.YES && getPreviousHospitalizations().size() <= 0) {
            getContentBinding().hospitalizationHospitalizedPreviously.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().hospitalizationHospitalizedPreviously.disableErrorState();
        }
    }

    public static CaseEditHospitalizationFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditHospitalizationFragment.class, capsule, activityRootData);
    }
}
