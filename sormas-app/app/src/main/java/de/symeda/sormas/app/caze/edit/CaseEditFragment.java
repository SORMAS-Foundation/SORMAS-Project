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
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.InvalidValueException;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboPropertyField;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.MoveCaseDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.layoutprocessor.CaseDiseaseLayoutProcessor;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditFragment extends BaseEditActivityFragment<FragmentCaseEditLayoutBinding, Case, Case> {

    public static final String TAG = CaseEditFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private AsyncTask moveCaseTask;
    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private Case record;
    private User user;
    private List<Item> caseClassificationList;
    private List<Item> caseOutcomeList;
    private List<Item> vaccinationList;
    private List<Item> vaccinationInfoSourceList;
    private List<Item> plagueList;

    private CaseDiseaseLayoutProcessor caseDiseaseLayoutProcessor;

    private View.OnClickListener moveToAnotherHealthFacilityCallback;

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

            resultHolder.forItem().add(ConfigProvider.getUser());

            resultHolder.forOther().add(DataUtils.getEnumItems(CaseClassification.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(CaseOutcome.class, false));

            resultHolder.forOther().add(DataUtils.getEnumItems(Vaccination.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(VaccinationInfoSource.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(PlagueType.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (itemIterator.hasNext())
                user = itemIterator.next();

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

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
        caseDiseaseLayoutProcessor = new CaseDiseaseLayoutProcessor(getContext(), getFragmentManager(), contentBinding, record, vaccinationList, vaccinationInfoSourceList, plagueList);
        caseDiseaseLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });
        caseDiseaseLayoutProcessor.processLayout(record.getDisease());

        updateCaseClassificationUI();

        if (record.getPerson().getSex() != Sex.FEMALE) {
            contentBinding.swhPregnant.setVisibility(View.GONE);
        }


        contentBinding.txtHealthFacility.addValueChangedListener(new TeboPropertyField.ValueChangeListener() {
            @Override
            public void onChange(TeboPropertyField field) {
                Facility selectedFacility = record.getHealthFacility();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(ConstantHelper.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(ConstantHelper.NONE_FACILITY_UUID);

                    if (otherHealthFacility) {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.VISIBLE);
                        getContentBinding().txtHealthFacilityDesc.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                    } else if (noneHealthFacility) {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.VISIBLE);
                        getContentBinding().txtHealthFacilityDesc.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, ConstantHelper.NONE_HEALTH_FACILITY_DETAILS));
                    } else {
                        getContentBinding().txtHealthFacilityDesc.setVisibility(View.GONE);
                    }
                } else {
                    getContentBinding().txtHealthFacilityDesc.setVisibility(View.GONE);
                }
            }
        });


        if (!ConfigProvider.getUser().hasUserRole(UserRole.INFORMANT)) {
            contentBinding.txtEpidNumber.addValueChangedListener(new TeboPropertyField.ValueChangeListener() {
                @Override
                public void onChange(TeboPropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        getContentBinding().txtEpidNumber.enableErrorState((INotificationContext)getActivity(), R.string.validation_soft_case_epid_number_empty);
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        getContentBinding().txtEpidNumber.disableErrorState((INotificationContext)getActivity());
                    } else {
                        //TODO: Re-enable error notification for EPID
                        //getContentBinding().txtEpidNumber.enableErrorState((INotificationContext)getActivity(), R.string.validation_soft_case_epid_number);
                    }
                }
            });
        } else {
            getContentBinding().txtEpidNumber.changeVisualState(VisualState.DISABLED);
        }

        if (ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
            contentBinding.spnCaseOfficerClassification.addValueChangedListener(new TeboPropertyField.ValueChangeListener() {
                @Override
                public void onChange(TeboPropertyField field) {
                    CaseClassification caseClassification = (CaseClassification) field.getValue();
                    if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
                        getContentBinding().spnCaseOfficerClassification.enableErrorState((INotificationContext)getActivity(), R.string.validation_soft_case_classification);
                    } else {
                        getContentBinding().spnCaseOfficerClassification.disableErrorState((INotificationContext)getActivity());
                    }
                }
            });
        }

        if (user != null && user.hasUserRight(UserRight.CASE_MOVE)) {
            contentBinding.casePageBottomCtrlPanel.setVisibility(View.VISIBLE);
        }

        if (user.hasUserRight(UserRight.CASE_CLASSIFY)) {
            contentBinding.spnOutcome.addValueChangedListener(new TeboPropertyField.ValueChangeListener() {
                @Override
                public void onChange(TeboPropertyField field) {
                    CaseOutcome outcome = (CaseOutcome) field.getValue();
                    if (outcome == null) {
                        getContentBinding().spnOutcome.enableErrorState((INotificationContext)getActivity(), R.string.validation_soft_case_outcome);
                    } else {
                        getContentBinding().spnOutcome.disableErrorState((INotificationContext)getActivity());
                    }

                    if (outcome == null || outcome == CaseOutcome.NO_OUTCOME) {
                        try {
                            getContentBinding().dtpDateOfOutcome.setVisibility(View.GONE);
                            getContentBinding().dtpDateOfOutcome.setValue(null);
                        } catch (InvalidValueException e) {
                            Log.e(TAG, "There was an error clearing the set value for Date of Outcome.");
                        }
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

        contentBinding.dtpDateOfOutcome.initialize(getFragmentManager());
        //contentBinding.dtpDateOfLastVaccination.initialize(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentCaseEditLayoutBinding contentBinding, Case aCase) {

    }

    @Override
    public void onPageResume(FragmentCaseEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Case caze = getActivityRootData();

                    if (caze != null) {
                        if (caze.isUnreadOrChildUnread())
                            DatabaseHelper.getCaseDao().markAsRead(caze);

                        if (caze.getPerson() == null) {
                            caze.setPerson(DatabaseHelper.getPersonDao().build());
                        }
                    }

                    resultHolder.forItem().add(caze);
                    resultHolder.forItem().add(ConfigProvider.getUser());
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record =  itemIterator.next();

                    if (itemIterator.hasNext())
                        user = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
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

                activity.saveCaseToDatabase(new ICallback<BoolResult>() {

                    @Override
                    public void result(BoolResult result) {
                        if (!result.isSuccess()) {
                            NotificationHelper.showNotification(activity, NotificationType.ERROR, R.string.notification_error_init_move_case);
                            return;
                        }

                        final MoveCaseDialog moveCaseDialog = new MoveCaseDialog(AbstractSormasActivity.getActiveActivity(), record);
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

    public static CaseEditFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (moveCaseTask != null && !moveCaseTask.isCancelled())
            moveCaseTask.cancel(true);
    }

}
