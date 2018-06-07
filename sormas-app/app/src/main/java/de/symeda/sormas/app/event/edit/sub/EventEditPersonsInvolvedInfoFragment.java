package de.symeda.sormas.app.event.edit.sub;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboDatePicker;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;
import de.symeda.sormas.app.event.edit.OccupationTypeLayoutProcessor;
import de.symeda.sormas.app.event.edit.PresentConditionLayoutProcessor;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditPersonsInvolvedInfoFragment extends BaseEditActivityFragment<FragmentEventEditPersonInfoLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventEditPersonsInvolvedInfoFragment.class.getSimpleName();

    private static final int DEFAULT_YEAR = 2000;

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private EventStatus pageStatus = null;
    private EventParticipant record;
    private OnTeboSwitchCheckedChangeListener onPresentConditionCheckedCallback;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> occupationTypeList;
    private List<Item> genderList;
    private List<Item> ageTypeList;
    private List<Item> dateList;
    private List<Item> monthList;
    private List<Item> yearList;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;

    private int mLastCheckedId = -1;

    private OccupationTypeLayoutProcessor occupationTypeLayoutProcessor;
    private PresentConditionLayoutProcessor presentConditionLayoutProcessor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_person_involved);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            EventParticipant eventParticipant = getActivityRootData();

            if (eventParticipant != null) {
                if (eventParticipant.isUnreadOrChildUnread())
                    DatabaseHelper.getEventParticipantDao().markAsRead(eventParticipant);
            }

            resultHolder.forItem().add(eventParticipant);

            resultHolder.forOther().add(DataUtils.getEnumItems(OccupationType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(Sex.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(ApproximateAgeType.class, false));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getDaysInMonth(),true));
            resultHolder.forOther().add(DataUtils.getMonthItems(true));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getYearsToNow(),true));
            resultHolder.forOther().add(DataUtils.getEnumItems(CauseOfDeath.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(DeathPlaceType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));

        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (otherIterator.hasNext())
                occupationTypeList =  otherIterator.next();

            if (otherIterator.hasNext())
                genderList =  otherIterator.next();

            if (otherIterator.hasNext())
                ageTypeList =  otherIterator.next();

            if (otherIterator.hasNext())
                dateList =  otherIterator.next();

            if (otherIterator.hasNext())
                monthList =  otherIterator.next();

            if (otherIterator.hasNext())
                yearList =  otherIterator.next();

            if (otherIterator.hasNext())
                causeOfDeathList =  otherIterator.next();

            if (otherIterator.hasNext())
                deathPlaceTypeList =  otherIterator.next();

            if (otherIterator.hasNext())
                diseaseList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventEditPersonInfoLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        //TODO: Validation
        //EventParticipantValidator.setRequiredHintsForEventParticipantData(binding);

        occupationTypeLayoutProcessor = new OccupationTypeLayoutProcessor(getContext(), contentBinding, record.getPerson());
        occupationTypeLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        presentConditionLayoutProcessor = new PresentConditionLayoutProcessor(getContext(), getFragmentManager(), contentBinding, record.getPerson(), causeOfDeathList, deathPlaceTypeList, diseaseList);
        presentConditionLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });
        presentConditionLayoutProcessor.setOnDateOfDeathChange(new OnDateOfDeathChangeListener() {
            @Override
            public void onChange(TeboDatePicker view, Date value) {
                updateApproximateAgeField();
            }
        });

        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
        contentBinding.setCheckedCallback(onPresentConditionCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);

        //TODO: Validation
        /*PersonValidator.setRequiredHintsForPersonData(binding);
        contentBinding.personPresentCondition.makeFieldSoftRequired();
        contentBinding.personSex.makeFieldSoftRequired();
        contentBinding.personDeathDate.makeFieldSoftRequired();
        contentBinding.personDeathPlaceDescription.makeFieldSoftRequired();
        contentBinding.personDeathPlaceType.makeFieldSoftRequired();
        contentBinding.personCauseOfDeath.makeFieldSoftRequired();
        contentBinding.personCauseOfDeathDetails.makeFieldSoftRequired();
        contentBinding.personCauseOfDeathDisease.makeFieldSoftRequired();
        contentBinding.personBurialDate.makeFieldSoftRequired();
        contentBinding.personBurialPlaceDescription.makeFieldSoftRequired();
        contentBinding.personBurialConductor.makeFieldSoftRequired();*/
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditPersonInfoLayoutBinding contentBinding) {
        contentBinding.spnOccupationType.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (occupationTypeList.size() > 0) ? DataUtils.addEmptyItem(occupationTypeList)
                        : occupationTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                if (!occupationTypeLayoutProcessor.processLayout((OccupationType)value))
                    return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnGender.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (genderList.size() > 0) ? DataUtils.addEmptyItem(genderList)
                        : genderList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnAgeType.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (ageTypeList.size() > 0) ? DataUtils.addEmptyItem(ageTypeList)
                        : ageTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnYear.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return DEFAULT_YEAR;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return yearList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnMonth.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return monthList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnDate.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return dateList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void updateUI(FragmentEventEditPersonInfoLayoutBinding contentBinding, EventParticipant eventParticipant) {
        contentBinding.spnOccupationType.setValue(eventParticipant.getPerson().getOccupationType(), true);
        contentBinding.spnGender.setValue(eventParticipant.getPerson().getSex(), true);
        contentBinding.spnAgeType.setValue(eventParticipant.getPerson().getApproximateAgeType(), true);
        contentBinding.spnYear.setValue(eventParticipant.getPerson().getBirthdateYYYY(), true);
        contentBinding.spnMonth.setValue(eventParticipant.getPerson().getBirthdateMM(), true);
        contentBinding.spnDate.setValue(eventParticipant.getPerson().getBirthdateDD(), true);
    }

    @Override
    public void onPageResume(FragmentEventEditPersonInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    EventParticipant eventParticipant = getActivityRootData();

                    if (eventParticipant != null) {
                        if (eventParticipant.isUnreadOrChildUnread())
                            DatabaseHelper.getEventParticipantDao().markAsRead(eventParticipant);
                    }

                    resultHolder.forItem().add(eventParticipant);
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
                        record = itemIterator.next();

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
        return R.layout.fragment_event_edit_person_info_layout;
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

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private void setupCallback() {
        onPresentConditionCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mLastCheckedId == checkedId) {
                    return;
                }

                mLastCheckedId = checkedId;

                if (!presentConditionLayoutProcessor.processLayout((PresentCondition)checkedItem))
                    return;
            }
        };

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getPerson().getAddress();
                final LocationDialog locationDialog = new LocationDialog(AbstractSormasActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtPermAddress.setValue(location.toString());
                        record.getPerson().setAddress(location);

                        locationDialog.dismiss();
                    }
                });

                locationDialog.show(new Callback.IAction<AlertDialog>() {
                    @Override
                    public void call(AlertDialog result) {

                    }
                });
            }
        };
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record.getPerson())) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    private void updateApproximateAgeField() {
        Integer birthyear = record.getPerson().getBirthdateYYYY();
        //TeboTextRead approximateAgeTextField =  getContentBinding().txtAge;
        //TeboSpinner approximateAgeTypeField = getContentBinding().spnAgeType;

        if(birthyear != null) {
            Integer birthday = record.getPerson().getBirthdateDD();
            Integer birthmonth = record.getPerson().getBirthdateMM();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth!=null?birthmonth-1:0, birthday!=null?birthday:1);

            Date to = new Date();
            if(record.getPerson().getDeathDate() != null) {
                to = record.getPerson().getDeathDate();
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeHelper.getApproximateAge(birthDate.getTime(),to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            Integer age = approximateAge.getElement0();

            record.getPerson().setApproximateAge(age);
            record.getPerson().setApproximateAgeType(ageType);

            updateUI();
        } else {
            //getContentBinding().txtAge.setEnabled(true, editOrCreateUserRight);
            //getContentBinding().spnAgeType.setEnabled(true, editOrCreateUserRight);
        }
    }

    // </editor-fold>

    public static EventEditPersonsInvolvedInfoFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule, EventParticipant activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventEditPersonsInvolvedInfoFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
