package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.LocationDialog;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Consumer;

public class EventEditDataForm extends FormTab {

    private EventDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String eventUuid = getArguments().getString(Event.UUID);
        final EventDao eventDao = DatabaseHelper.getEventDao();
        Event event = null;

        // create a new event for empty uuid
        if(eventUuid==null) {
            event = DatabaseHelper.getEventDao().getNewEvent();

        }
        // open the given event
        else {
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

        final Location location = event.getEventLocation() != null ? event.getEventLocation() : DataUtils.createNew(Location.class);
        LocationDialog.addLocationField(getActivity(), location, binding.eventEventLocation, binding.eventEventLocationBtn, new Consumer() {
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

}