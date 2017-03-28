package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class EventParticipantNewPersonForm extends FormTab {

    private EventParticipant eventParticipant;
    private EventParticipantNewFragmentLayoutBinding binding;

    private Person selectedPersonFromDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            Person person = DataUtils.createNew(Person.class);
            eventParticipant = DataUtils.createNew(EventParticipant.class);
            eventParticipant.setPerson(person);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.event_participant_new_fragment_layout, container, false);
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();

        binding.setEventParticipant(eventParticipant);

    }

    @Override
    public EventParticipant getData() {
        return binding.getEventParticipant();
    }

}