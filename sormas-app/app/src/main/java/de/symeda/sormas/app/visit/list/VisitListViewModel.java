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

package de.symeda.sormas.app.visit.list;

import java.util.List;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;

public class VisitListViewModel extends ViewModel {

	private MutableLiveData<List<Visit>> visits;
	private Contact contact;

	public LiveData<List<Visit>> getVisits(Contact contact) {
		this.contact = contact;

		if (visits == null) {
			visits = new MutableLiveData<>();
			loadVisits();
		}

		return visits;
	}

	private void loadVisits() {
		new LoadVisitsTask(this).execute();
	}

	private static class LoadVisitsTask extends AsyncTask<Void, Void, List<Visit>> {

		private VisitListViewModel model;

		LoadVisitsTask(VisitListViewModel model) {
			this.model = model;
		}

		@Override
		protected List<Visit> doInBackground(Void... args) {
			return DatabaseHelper.getVisitDao().getByContact(model.contact);
		}

		@Override
		protected void onPostExecute(List<Visit> data) {
			model.visits.setValue(data);
		}
	}
}
