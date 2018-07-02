package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import de.symeda.sormas.api.caze.InvestigationStatus;
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
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.MoveCaseDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.layoutprocessor.CaseDiseaseLayoutProcessor;

public class CaseEditFragment extends BaseEditActivityFragment<FragmentCaseEditLayoutBinding, Case, Case> {

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
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }
            }

            resultHolder.forItem().add(caze);

            resultHolder.forOther().add(DataUtils.getEnumItems(CaseClassification.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(CaseOutcome.class, false));

            resultHolder.forOther().add(DataUtils.getEnumItems(Vaccination.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(VaccinationInfoSource.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(PlagueType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(DengueFeverType.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (otherIterator.hasNext())
                caseClassificationList = otherIterator.next();

            if (otherIterator.hasNext())
                caseOutcomeList = otherIterator.next();

            if (otherIterator.hasNext())
                vaccinationList = otherIterator.next();

            if (otherIterator.hasNext())
                vaccinationInfoSourceList = otherIterator.next();

            if (otherIterator.hasNext())
                plagueList = otherIterator.next();

            if (otherIterator.hasNext())
                dengueFeverList = otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
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
            contentBinding.swhPregnant.setVisibility(View.GONE);
        }


        contentBinding.txtHealthFacility.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility selectedFacility = record.getHealthFacility();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

                    if (otherHealthFacility) {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.VISIBLE);
                        getContentBinding().txtHealthFacilityDesc.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                    } else if (noneHealthFacility) {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.VISIBLE);
                        getContentBinding().txtHealthFacilityDesc.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
                    } else {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.GONE);
                    }
                } else {
                    getContentBinding().txtHealthFacilityDesc.setVisibility(View.GONE);
                }
            }
        });


        if (!ConfigProvider.getUser().hasUserRole(UserRole.INFORMANT)) {
            contentBinding.txtEpidNumber.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        getContentBinding().txtEpidNumber.enableWarningState((NotificationContext)getActivity(), R.string.validation_soft_case_epid_number_empty);
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        getContentBinding().txtEpidNumber.disableWarningState();
                    } else {
                        getContentBinding().txtEpidNumber.enableWarningState((NotificationContext)getActivity(), R.string.validation_soft_case_epid_number);
                    }
                }
            });
        } else {
            getContentBinding().txtEpidNumber.changeVisualState(VisualState.DISABLED);
        }

        if (ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
            contentBinding.spnCaseOfficerClassification.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    CaseClassification caseClassification = (CaseClassification) field.getValue();
                    if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
                        getContentBinding().spnCaseOfficerClassification.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_case_classification);
                    } else {
                        getContentBinding().spnCaseOfficerClassification.disableErrorState();
                    }
                }
            });
        }

        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_TRANSFER)) {
            contentBinding.casePageBottomCtrlPanel.setVisibility(View.VISIBLE);
        }

        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_CLASSIFY)) {
            contentBinding.spnOutcome.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    CaseOutcome outcome = (CaseOutcome) field.getValue();
                    if (outcome == null) {
                        getContentBinding().spnOutcome.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_case_outcome);
                    } else {
                        getContentBinding().spnOutcome.disableErrorState();
                    }

                    if (outcome == null || outcome == CaseOutcome.NO_OUTCOME) {
                            getContentBinding().dtpDateOfOutcome.setVisibility(View.GONE);
                            getContentBinding().dtpDateOfOutcome.setValue(null);

                    } else {
                        getContentBinding().dtpDateOfOutcome.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            contentBinding.spnOutcome.changeVisualState(VisualState.DISABLED);
            contentBinding.dtpDateOfOutcome.changeVisualState(VisualState.DISABLED);
        }

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setPlagueTypeClass(PlagueType.class);
        contentBinding.setMoveToAnotherHealthFacilityCallback(moveToAnotherHealthFacilityCallback);
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

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnCaseOfficerClassification.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (caseClassificationList.size() > 0) ? DataUtils.addEmptyItem(caseClassificationList)
                        : caseClassificationList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
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
            public VisualState getInitVisualState() {
                return null;
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

        contentBinding.dtpDateOfOutcome.setFragmentManager(getFragmentManager());
        //contentBinding.dtpDateOfLastVaccination.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentCaseEditLayoutBinding contentBinding, Case aCase) {

    }

    @Override
    public void onPageResume(FragmentCaseEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        record = getActivityRootData();
        requestLayoutRebind();
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void setupCallback() {
        moveToAnotherHealthFacilityCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CaseEditActivity activity = (CaseEditActivity)CaseEditFragment.this.getActivity();

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
                                record = (Case)item;
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
            getContentBinding().spnCaseOfficerClassification.setVisibility(View.GONE);
            if (record.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
                getContentBinding().spnCaseClassification.setVisibility(View.GONE);
            }
        } else {
            getContentBinding().spnCaseClassification.setVisibility(View.GONE);
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
