package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.LocationDialogBuilder;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.validation.EventValidator;

public class EventEditDataForm extends FormTab {

    private EventDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_data_fragment_layout, container, false);

        String eventUuid = null;
        if (getArguments() != null) {
            eventUuid = getArguments().getString(Event.UUID);
        }

        Event event;

        if (eventUuid==null) {
            // build a new event for empty uuid
            event = DatabaseHelper.getEventDao().build();
        } else {
            // open the given event
            final EventDao eventDao = DatabaseHelper.getEventDao();
            event = eventDao.queryUuid(eventUuid);
        }

        binding.setEvent(event);

        binding.eventEventType.initialize(EventType.class);
        FieldHelper.initSpinnerField(binding.eventTypeOfPlace, TypeOfPlace.class);
        binding.eventEventDate.initialize(this);

        binding.eventTypeOfPlace.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleTypeOfPlaceTextField();
            }
        });

        FieldHelper.initSpinnerField(binding.eventDisease, Disease.class);

        LocationDialogBuilder.addLocationField(getActivity(), event.getEventLocation(), binding.eventEventLocation, binding.eventEventLocationBtn, new Consumer() {
            @Override
            public void accept(Object parameter) {
                if(parameter instanceof Location) {
                    binding.eventEventLocation.setValue(parameter.toString());
                    binding.getEvent().setEventLocation(((Location)parameter));
                }
            }
        });

        // init fields
        toggleTypeOfPlaceTextField();

        EventValidator.setRequiredHintsForEventData(binding);

        binding.eventDisease.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                if (field.getValue() == Disease.OTHER) {
                    binding.eventDiseaseDetails.setVisibility(View.VISIBLE);
                } else {
                    binding.eventDiseaseDetails.setVisibility(View.GONE);
                    binding.eventDiseaseDetails.setValue(null);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getEvent();
    }


    private void toggleTypeOfPlaceTextField() {
        TypeOfPlace typeOfPlace = (TypeOfPlace) binding.eventTypeOfPlace.getValue();
        if(typeOfPlace == TypeOfPlace.OTHER) {
            setFieldVisible(binding.eventTypeOfPlaceTxt, true);
        }
        else {
            // reset value
            binding.eventTypeOfPlaceTxt.setValue(null);
            setFieldGone(binding.eventTypeOfPlaceTxt);
        }
    }

    public EventDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}