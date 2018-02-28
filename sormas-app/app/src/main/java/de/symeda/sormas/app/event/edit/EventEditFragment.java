package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditFragment extends BaseEditActivityFragment<FragmentEventEditLayoutBinding> {

    private String recordUuid = null;
    private EventStatus followUpStatus = null;
    private EventStatus pageStatus = null;
    private Event record;
    private OnTeboSwitchCheckedChangeListener onEventTypeCheckedCallback;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Disease> diseaseList;
    private List<TypeOfPlace> typeOfPlaceList;

    private int mLastCheckedId = -1;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
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

        record = MemoryDatabaseHelper.EVENT.getEvents(1).get(0);
        diseaseList = MemoryDatabaseHelper.DISEASE.getDiseases(20);
        typeOfPlaceList = MemoryDatabaseHelper.TYPE_OF_PLACE.getPlaces(10);

        setupCallback();
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentEventEditLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        contentBinding.setData(record);
        contentBinding.setEventTypeClass(EventType.class);
        contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditLayoutBinding binding) {
        binding.spnDisease.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (diseaseList.size() > 0) ? DataUtils.toItems(diseaseList)
                        : DataUtils.toItems(diseaseList, false);
            }
        });
        binding.spnTypeOfPlace.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (typeOfPlaceList.size() > 0) ? DataUtils.toItems(typeOfPlaceList)
                        : DataUtils.toItems(typeOfPlaceList, false);
            }
        });
        binding.dtpDateOfAlert.initialize(getFragmentManager());
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


                final Location location = MemoryDatabaseHelper.LOCATION.getLocations(1).get(0);
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

    public static EventEditFragment newInstance(EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(EventEditFragment.class, capsule);
    }
}
