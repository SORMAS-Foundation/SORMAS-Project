/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.event.list;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;

public class EventListViewModel extends ViewModel {

    private MutableLiveData<List<Event>> events;
    private EventStatus eventStatus = EventStatus.POSSIBLE;

    public LiveData<List<Event>> getEvents() {
        if (events == null) {
            events = new MutableLiveData<>();
            loadEvents();
        }

        return events;
    }

    void setEventStatusAndReload(EventStatus eventStatus) {
        if (this.eventStatus == eventStatus) {
            return;
        }

        this.eventStatus = eventStatus;
        loadEvents();
    }

    private void loadEvents() {
        new LoadEventsTask(this).execute();
    }

    private static class LoadEventsTask extends AsyncTask<Void, Void, List<Event>> {
        private EventListViewModel model;

        LoadEventsTask(EventListViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Event> doInBackground(Void... args) {
            return DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, model.eventStatus, Event.REPORT_DATE_TIME, false);
        }

        @Override
        protected void onPostExecute(List<Event> data) {
            model.events.setValue(data);
        }
    }

}
