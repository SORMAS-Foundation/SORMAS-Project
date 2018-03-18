package de.symeda.sormas.app.event.edit;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditPersonsInvolvedInfoFragment extends BaseEditActivityFragment<FragmentEventEditPersonInfoLayoutBinding, EventParticipant> {

    public static final String TAG = EventEditPersonsInvolvedInfoFragment.class.getSimpleName();

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

    private int mLastCheckedId = -1;

    private OccupationTypeLayoutProcessor occupationTypeLayoutProcessor;
    private PresentConditionLayoutProcessor presentConditionLayoutProcessor;

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
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_level4_1_event_edit_person_involved_info);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                // build a new event for empty uuid
                resultHolder.forItem().add(DatabaseHelper.getEventParticipantDao().build());
            } else {
                // open the given event
                resultHolder.forItem().add(DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid));
            }

            resultHolder.forOther().add(DataUtils.getEnumItems(OccupationType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(Sex.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(ApproximateAgeType.class, false));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getDaysInMonth(),true));
            resultHolder.forOther().add(DataUtils.getMonthItems(true));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getYearsToNow(),true));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

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

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventEditPersonInfoLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        occupationTypeLayoutProcessor = new OccupationTypeLayoutProcessor(getContext(), contentBinding, record.getPerson());
        occupationTypeLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        presentConditionLayoutProcessor = new PresentConditionLayoutProcessor(getContext(), getFragmentManager(), contentBinding, record.getPerson());
        presentConditionLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
        contentBinding.setCheckedCallback(onPresentConditionCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
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
        });

        contentBinding.spnAgeType.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (ageTypeList.size() > 0) ? DataUtils.addEmptyItem(ageTypeList)
                        : ageTypeList;
            }
        });

        contentBinding.spnYear.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return yearList;
            }
        });

        contentBinding.spnMonth.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return monthList;
            }
        });

        contentBinding.spnDate.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return dateList;
            }
        });

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_person_info_layout;
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
                final Location location = MemoryDatabaseHelper.LOCATION.getLocations(1).get(0);
                final LocationDialog locationDialog = new LocationDialog(TaskEditActivity.getActiveActivity(), location);
                locationDialog.show();


                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        /*getContentBinding().txtAddress.setValue(location.toString());
                        locationDialog.dismiss();*/
                    }
                });
            }
        };
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record.getPerson())) {
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    // </editor-fold>

    public static EventEditPersonsInvolvedInfoFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventEditPersonsInvolvedInfoFragment.class, capsule);
    }
}
