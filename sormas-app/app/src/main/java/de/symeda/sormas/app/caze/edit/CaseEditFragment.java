package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.MoveCaseDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.layoutprocessor.CaseDiseaseLayoutProcessor;

public class CaseEditFragment extends BaseEditFragment<FragmentCaseEditLayoutBinding, Case, Case> {

    public static final String TAG = CaseEditFragment.class.getSimpleName();

    private AsyncTask moveCaseTask;
    private Case record;

    private List<Item> caseClassificationList;
    private List<Item> caseOutcomeList;
    private List<Item> vaccinationList;
    private List<Item> vaccinationInfoSourceList;
    private List<Item> plagueList;
    private List<Item> dengueFeverList;

    private CaseDiseaseLayoutProcessor caseDiseaseLayoutProcessor;

    private View.OnClickListener moveToAnotherHealthFacilityCallback;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_information);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();

        caseClassificationList = DataUtils.getEnumItems(CaseClassification.class, false);
        caseOutcomeList = DataUtils.getEnumItems(CaseOutcome.class, false);
        vaccinationList = DataUtils.getEnumItems(Vaccination.class, false);
        vaccinationInfoSourceList = DataUtils.getEnumItems(VaccinationInfoSource.class, false);
        plagueList = DataUtils.getEnumItems(PlagueType.class, false);
        dengueFeverList = DataUtils.getEnumItems(DengueFeverType.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {

        setupCallback();

        caseDiseaseLayoutProcessor = new CaseDiseaseLayoutProcessor(getContext(), getFragmentManager(), contentBinding, record, vaccinationList, vaccinationInfoSourceList, plagueList, dengueFeverList);
        caseDiseaseLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });
        caseDiseaseLayoutProcessor.processLayout(record.getDisease());

        updateCaseClassificationUI();

        if (record.getPerson().getSex() != Sex.FEMALE) {
            contentBinding.caseDataPregnant.setVisibility(View.GONE);
        }


        contentBinding.caseDataHealthFacility.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility selectedFacility = record.getHealthFacility();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

                    if (otherHealthFacility) {
                        getContentBinding().hospitalizationHealthFacilityDetails.setVisibility(View.VISIBLE);
                        getContentBinding().hospitalizationHealthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                    } else if (noneHealthFacility) {
                        getContentBinding().hospitalizationHealthFacilityDetails.setVisibility(View.VISIBLE);
                        getContentBinding().hospitalizationHealthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
                    } else {
                        getContentBinding().hospitalizationHealthFacilityDetails.setVisibility(View.GONE);
                    }
                } else {
                    getContentBinding().hospitalizationHealthFacilityDetails.setVisibility(View.GONE);
                }
            }
        });


        if (!ConfigProvider.getUser().hasUserRole(UserRole.INFORMANT)) {
            contentBinding.caseDataEpidNumber.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        getContentBinding().caseDataEpidNumber.enableWarningState((NotificationContext) getActivity(), R.string.validation_soft_case_epid_number_empty);
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        getContentBinding().caseDataEpidNumber.disableWarningState();
                    } else {
                        getContentBinding().caseDataEpidNumber.enableWarningState((NotificationContext) getActivity(), R.string.validation_soft_case_epid_number);
                    }
                }
            });
        } else {
            getContentBinding().caseDataEpidNumber.changeVisualState(VisualState.DISABLED);
        }

        if (ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
            contentBinding.caseDataCaseOfficerClassification.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    CaseClassification caseClassification = (CaseClassification) field.getValue();
                    if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
                        getContentBinding().caseDataCaseOfficerClassification.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_case_classification);
                    } else {
                        getContentBinding().caseDataCaseOfficerClassification.disableErrorState();
                    }
                }
            });
        }

        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_TRANSFER)) {
            contentBinding.casePageBottomCtrlPanel.setVisibility(View.VISIBLE);
        }

        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_CLASSIFY)) {
            contentBinding.caseDataOutcome.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    CaseOutcome outcome = (CaseOutcome) field.getValue();
                    if (outcome == null) {
                        getContentBinding().caseDataOutcome.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_case_outcome);
                    } else {
                        getContentBinding().caseDataOutcome.disableErrorState();
                    }

                    if (outcome == null || outcome == CaseOutcome.NO_OUTCOME) {
                        getContentBinding().caseDataOutcomeDate.setVisibility(View.GONE);
                        getContentBinding().caseDataOutcomeDate.setValue(null);

                    } else {
                        getContentBinding().caseDataOutcomeDate.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            contentBinding.caseDataOutcome.changeVisualState(VisualState.DISABLED);
            contentBinding.caseDataOutcomeDate.changeVisualState(VisualState.DISABLED);
        }

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setPlagueTypeClass(PlagueType.class);
        contentBinding.setMoveToAnotherHealthFacilityCallback(moveToAnotherHealthFacilityCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
        contentBinding.caseDataCaseClassification.initializeSpinner(DataUtils.addEmptyItem(caseClassificationList));
        contentBinding.caseDataCaseOfficerClassification.initializeSpinner(DataUtils.addEmptyItem(caseClassificationList));
        contentBinding.caseDataOutcome.initializeSpinner(caseOutcomeList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                CaseOutcome caseOutcome = (CaseOutcome) field.getValue();

                if (caseOutcome == CaseOutcome.NO_OUTCOME || caseOutcome == null) {
                    getContentBinding().caseDataOutcomeDate.setVisibility(View.GONE);
                } else {
                    getContentBinding().caseDataOutcomeDate.setVisibility(View.VISIBLE);
                }
            }
        });

        contentBinding.caseDataOutcomeDate.setFragmentManager(getFragmentManager());
        //contentBinding.dtpDateOfLastVaccination.setFragmentManager(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_layout;
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
        moveToAnotherHealthFacilityCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CaseEditActivity activity = (CaseEditActivity) CaseEditFragment.this.getActivity();

                if (activity == null)
                    return;

                activity.saveCaseToDatabase(new Callback.IAction<BoolResult>() {

                    @Override
                    public void call(BoolResult result) {
                        if (!result.isSuccess()) {
                            NotificationHelper.showNotification(activity, NotificationType.ERROR, R.string.notification_error_init_move_case);
                            return;
                        }

                        final MoveCaseDialog moveCaseDialog = new MoveCaseDialog(BaseActivity.getActiveActivity(), record);
                        moveCaseDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                            @Override
                            public void onOkClick(View v, Object item, View viewRoot) {
                                record = (Case) item;
                                requestLayoutRebind();
                                moveCaseDialog.dismiss();

                            }
                        });

                        moveCaseDialog.show(null);
                    }
                });
            }
        };
    }

    private void updateCaseClassificationUI() {
        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT)) {
            getContentBinding().caseDataCaseOfficerClassification.setVisibility(View.GONE);
            if (record.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
                getContentBinding().caseDataCaseClassification.setVisibility(View.GONE);
            }
        } else {
            getContentBinding().caseDataCaseClassification.setVisibility(View.GONE);
        }
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.yesNoUnknownClass, YesNoUnknown.class)) {
            Log.e(TAG, "There is no variable 'yesNoUnknownClass' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.plagueTypeClass, PlagueType.class)) {
            Log.e(TAG, "There is no variable 'plagueTypeClass' in layout " + layoutName);
        }
    }

    public static CaseEditFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (moveCaseTask != null && !moveCaseTask.isCancelled())
            moveCaseTask.cancel(true);
    }
}
