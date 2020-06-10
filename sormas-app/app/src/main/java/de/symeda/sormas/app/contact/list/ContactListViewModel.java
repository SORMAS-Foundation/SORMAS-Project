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

package de.symeda.sormas.app.contact.list;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactCriteria;

public class ContactListViewModel extends ViewModel {

	private LiveData<PagedList<Contact>> contacts;
	private ContactDataFactory contactDataFactory;

	public void initializeViewModel(Case caze) {
		contactDataFactory = new ContactDataFactory();
		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.caze(caze);
		contactDataFactory.setContactCriteria(contactCriteria);
		initializeList();
	}

	public void initializeViewModel() {
		contactDataFactory = new ContactDataFactory();
		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.followUpStatus(null);
		contactDataFactory.setContactCriteria(contactCriteria);
		initializeList();
	}

	public LiveData<PagedList<Contact>> getContacts() {
		return contacts;
	}

	void notifyCriteriaUpdated() {
		if (contacts.getValue() != null) {
			contacts.getValue().getDataSource().invalidate();
			if (!contacts.getValue().isEmpty()) {
				contacts.getValue().loadAround(0);
			}
		}
	}

	public ContactCriteria getContactCriteria() {
		return contactDataFactory.getContactCriteria();
	}

	public static class ContactDataSource extends PositionalDataSource<Contact> {

		private ContactCriteria contactCriteria;

		ContactDataSource(ContactCriteria contactCriteria) {
			this.contactCriteria = contactCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Contact> callback) {
			long totalCount = DatabaseHelper.getContactDao().countByCriteria(contactCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Contact> contacts = DatabaseHelper.getContactDao().queryByCriteria(contactCriteria, offset, count);
			callback.onResult(contacts, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Contact> callback) {
			List<Contact> contacts = DatabaseHelper.getContactDao().queryByCriteria(contactCriteria, params.startPosition, params.loadSize);
			callback.onResult(contacts);
		}
	}

	public static class ContactDataFactory extends DataSource.Factory {

		private MutableLiveData<ContactDataSource> mutableDataSource;
		private ContactDataSource contactDataSource;
		private ContactCriteria contactCriteria;

		ContactDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			contactDataSource = new ContactDataSource(contactCriteria);
			mutableDataSource.postValue(contactDataSource);
			return contactDataSource;
		}

		void setContactCriteria(ContactCriteria contactCriteria) {
			this.contactCriteria = contactCriteria;
		}

		ContactCriteria getContactCriteria() {
			return contactCriteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder contactListBuilder = new LivePagedListBuilder(contactDataFactory, config);
		contacts = contactListBuilder.build();
	}
}
