package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.EventParticipantFragmentLayoutBinding;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;

public class EventParticipantDataForm extends FormTab {

    private EventParticipantFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_participant_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String personUuid = getArguments().getString(Person.UUID);
        EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
        EventParticipant eventParticipant = eventParticipantDao.queryUuid(personUuid);
        if (eventParticipant == null) {
            eventParticipant = eventParticipantDao.create();
        }
        binding.setEventParticipant(eventParticipant);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getEventParticipant();
    }
}