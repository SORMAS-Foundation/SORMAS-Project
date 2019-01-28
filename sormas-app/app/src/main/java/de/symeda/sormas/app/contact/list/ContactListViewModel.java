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

package de.symeda.sormas.app.contact.list;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;

public class ContactListViewModel extends ViewModel {

    private MutableLiveData<List<Contact>> contacts;
    private FollowUpStatus followUpStatus = FollowUpStatus.FOLLOW_UP;
    private Case caze;

    public LiveData<List<Contact>> getContacts() {
        if (contacts == null) {
            contacts = new MutableLiveData<>();
            loadContacts();
        }

        return contacts;
    }

    public LiveData<List<Contact>> getContacts(Case caze) {
        this.caze = caze;
        return getContacts();
    }

    void setFollowUpStatusAndReload(FollowUpStatus followUpStatus) {
        if (this.followUpStatus == followUpStatus) {
            return;
        }

        this.followUpStatus = followUpStatus;
        loadContacts();
    }

    private void loadContacts() {
        new LoadContactsTask(this).execute();
    }

    private static class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {
        private ContactListViewModel model;

        LoadContactsTask(ContactListViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Contact> doInBackground(Void... args) {
            if (model.caze == null) {
                return DatabaseHelper.getContactDao().queryForEq(Contact.FOLLOW_UP_STATUS, model.followUpStatus, Contact.REPORT_DATE_TIME, false);
            } else {
                return DatabaseHelper.getContactDao().getByCase(model.caze);
            }
        }

        @Override
        protected void onPostExecute(List<Contact> data) {
            model.contacts.setValue(data);
        }
    }

}
