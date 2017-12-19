package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.EventParticipantValidator;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class EventParticipantNewPersonForm extends FormTab {

    private EventParticipant eventParticipant;
    private EventParticipantNewFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Person person = DatabaseHelper.getPersonDao().build();
        eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setPerson(person);

        binding = DataBindingUtil.inflate(inflater, R.layout.event_participant_new_fragment_layout, container, false);
        binding.setEventParticipant(eventParticipant);
        editOrCreateUserRight = (UserRight) getArguments().get(EDIT_OR_CREATE_USER_RIGHT);

        EventParticipantValidator.setRequiredHintsForNewEventParticipant(binding);

        return binding.getRoot();

    }

    @Override
    public EventParticipant getData() {
        return binding == null ? null : binding.getEventParticipant();
    }

    public EventParticipantNewFragmentLayoutBinding getBinding() {
        return binding;
    }

}