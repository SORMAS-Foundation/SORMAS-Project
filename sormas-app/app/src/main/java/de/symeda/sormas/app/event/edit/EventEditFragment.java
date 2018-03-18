package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
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
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditFragment extends BaseEditActivityFragment<FragmentEventEditLayoutBinding, Event> {

    private String recordUuid = null;
    private EventStatus pageStatus = null;
    private Event record;
    private OnTeboSwitchCheckedChangeListener onEventTypeCheckedCallback;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> diseaseList;
    private List<Item> typeOfPlaceList;

    private int mLastCheckedId = -1;


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
        return null;
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                // build a new event for empty uuid
                resultHolder.forItem().add(DatabaseHelper.getEventDao().build());
            } else {
                // open the given event
                resultHolder.forItem().add(DatabaseHelper.getEventDao().queryUuid(recordUuid));
            }

            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(TypeOfPlace.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

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
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        // init fields
        //toggleTypeOfPlaceTextField();

        //EventValidator.setRequiredHintsForEventData(binding);
        //EventValidator.setSoftRequiredHintsForEventData(binding);

        contentBinding.setData(record);
        contentBinding.setEventTypeClass(EventType.class);
        contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        contentBinding.spnDisease.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (diseaseList.size() > 0) ? DataUtils.addEmptyItem(diseaseList)
                        : diseaseList;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                Disease disease = (Disease)value;

                if (disease == Disease.OTHER) {
                    getContentBinding().txtOtherDisease.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().txtOtherDisease.setVisibility(View.GONE);
                    getContentBinding().txtOtherDisease.setValue("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        contentBinding.spnTypeOfPlace.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (typeOfPlaceList.size() > 0) ? DataUtils.addEmptyItem(typeOfPlaceList)
                        : typeOfPlaceList;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                toggleTypeOfPlaceTextField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        contentBinding.dtpDateOfAlert.initialize(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_layout;
    }

    private void setupCallback() {
        onEventTypeCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (mLastCheckedId == checkedId) {
                    return;
                }

                mLastCheckedId = checkedId;

            }
        };

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                //getContentBinding().txtSourceLastName.enableErrorState("HOIOIOO");
                //final Location location = MemoryDatabaseHelper.LOCATION.getLocations(1).get(0);
                final Location location = record.getEventLocation();
                final LocationDialog locationDialog = new LocationDialog(TaskEditActivity.getActiveActivity(), location);
                locationDialog.show();


                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtAddress.setValue(location.toString());
                        locationDialog.dismiss();
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

    public static EventEditFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventEditFragment.class, capsule);
    }
}
