/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.event.eventparticipant.list;

import java.util.List;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;

public class EventParticipantListViewModel extends ViewModel {

	private MutableLiveData<List<EventParticipant>> eventParticipants;
	private Event event;

	public LiveData<List<EventParticipant>> getEventParticipants(Event event) {
		this.event = event;

		if (eventParticipants == null) {
			eventParticipants = new MutableLiveData<>();
			loadEventParticipants();
		}

		return eventParticipants;
	}

	private void loadEventParticipants() {
		new LoadEventParticipantsTask(this).execute();
	}

	private static class LoadEventParticipantsTask extends AsyncTask<Void, Void, List<EventParticipant>> {

		private EventParticipantListViewModel model;

		LoadEventParticipantsTask(EventParticipantListViewModel model) {
			this.model = model;
		}

		@Override
		protected List<EventParticipant> doInBackground(Void... args) {
			return DatabaseHelper.getEventParticipantDao().getByEvent(model.event);
		}

		@Override
		protected void onPostExecute(List<EventParticipant> data) {
			model.eventParticipants.setValue(data);
		}
	}
}
