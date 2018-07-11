package de.symeda.sormas.app.event.edit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.EventValidator;

public class EventEditFragment extends BaseEditFragment<FragmentEventEditLayoutBinding, Event, Event> {

    private Event record;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> diseaseList;
    private List<Item> typeOfPlaceList;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_information);
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        diseaseList = DataUtils.getEnumItems(Disease.class, false);
        typeOfPlaceList = DataUtils.getEnumItems(TypeOfPlace.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {

        setupCallback();
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
        contentBinding.eventDisease.initializeSpinner(diseaseList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Disease disease = (Disease) field.getValue();

                if (disease == Disease.OTHER) {
                    getContentBinding().eventDiseaseDetails.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().eventDiseaseDetails.setVisibility(View.GONE);
                    getContentBinding().eventDiseaseDetails.setValue("");
                }
            }
        });
        contentBinding.eventTypeOfPlace.initializeSpinner(typeOfPlaceList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                toggleTypeOfPlaceTextField();
            }
        });
        contentBinding.eventEventDate.setFragmentManager(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_layout;
    }

    private void setupCallback() {

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getEventLocation();
                final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().eventEventLocation.setValue(location.toString());
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
        if (typeOfPlace == TypeOfPlace.OTHER) {
            setFieldVisible(getContentBinding().eventTypeOfPlaceText, true);
        } else {
            // reset value
            getContentBinding().eventTypeOfPlaceText.setValue("");
            setFieldGone(getContentBinding().eventTypeOfPlaceText);
        }
    }

    public static EventEditFragment newInstance(EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(EventEditFragment.class, capsule, activityRootData);
    }
}
