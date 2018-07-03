package de.symeda.sormas.app.event.edit;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.EventValidator;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditFragment extends BaseEditFragment<FragmentEventEditLayoutBinding, Event, Event> {

    private AsyncTask onResumeTask;
    private AsyncTask saveEvent;
    private String recordUuid = null;
    private EventStatus pageStatus = null;
    private Event record;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> diseaseList;
    private List<Item> typeOfPlaceList;

    private int mLastCheckedId = -1;


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
        return r.getString(R.string.caption_event_information);
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Event event = getActivityRootData();

            if (event != null) {
                if (event.isUnreadOrChildUnread())
                    DatabaseHelper.getEventDao().markAsRead(event);
            }

            resultHolder.forItem().add(event);

            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(TypeOfPlace.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (otherIterator.hasNext())
                diseaseList =  otherIterator.next();

            if (otherIterator.hasNext())
                typeOfPlaceList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        // init fields
        //toggleTypeOfPlaceTextField();

        EventValidator.setRequiredHintsForEventData(contentBinding);
        EventValidator.setSoftRequiredHintsForEventData(contentBinding);

        contentBinding.setData(record);
        contentBinding.setEventTypeClass(EventType.class);
//        contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        contentBinding.spnDisease.initializeSpinner(diseaseList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Disease disease = (Disease) field.getValue();

                if (disease == Disease.OTHER) {
                    getContentBinding().txtOtherDisease.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().txtOtherDisease.setVisibility(View.GONE);
                    getContentBinding().txtOtherDisease.setValue("");
                }
            }
        });
        contentBinding.spnTypeOfPlace.initializeSpinner(typeOfPlaceList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                toggleTypeOfPlaceTextField();
            }
        });
        contentBinding.dtpDateOfAlert.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentEventEditLayoutBinding contentBinding, Event event) {
        contentBinding.spnDisease.setValue(event.getDisease());
        contentBinding.spnTypeOfPlace.setValue(event.getTypeOfPlace());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
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
//        onEventTypeCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (mLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mLastCheckedId = checkedId;
//
//
//
//            }
//        };

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getEventLocation();
                final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtAddress.setValue(location.toString());
                        record.setEventLocation(location);

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

    private void toggleTypeOfPlaceTextField() {
        TypeOfPlace typeOfPlace = (TypeOfPlace) record.getTypeOfPlace();
        if(typeOfPlace == TypeOfPlace.OTHER) {
            setFieldVisible(getContentBinding().txtOtherEventPlace, true);
        } else {
            // reset value
            getContentBinding().txtOtherEventPlace.setValue("");
            setFieldGone(getContentBinding().txtOtherEventPlace);
        }
    }

    public static EventEditFragment newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveEvent != null && !saveEvent.isCancelled())
            saveEvent.cancel(true);
    }
}
