/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationCriteria;

public class ImmunizationListViewModel extends ViewModel {

	private LiveData<PagedList<Immunization>> immunizationList;
	private ImmunizationFactory immunizationFactory;

	public void initializeViewModel() {
		initializeModel(new ImmunizationCriteria());
	}

	public void initializeViewModel(Case caze) {
		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setPerson(caze.getPerson());
		initializeModel(immunizationCriteria);
	}

	public void initializeViewModel(Contact contact) {
		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setPerson(contact.getPerson());
		initializeModel(immunizationCriteria);
	}

	public void initializeViewModel(EventParticipant eventParticipant) {
		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setPerson(eventParticipant.getPerson());
		initializeModel(immunizationCriteria);
	}

	private void initializeModel(ImmunizationCriteria immunizationCriteria) {
		immunizationFactory = new ImmunizationFactory();
		immunizationFactory.setImmunizationCriteria(immunizationCriteria);
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

		LivePagedListBuilder immunizationsListBuilder = new LivePagedListBuilder(immunizationFactory, config);
		immunizationList = immunizationsListBuilder.build();
	}

	public LiveData<PagedList<Immunization>> getImmunizationList() {
		return immunizationList;
	}

	public ImmunizationCriteria getImmunizationCriteria() {
		return immunizationFactory.getImmunizationCriteria();
	}

	void notifyCriteriaUpdated() {
		if (immunizationList.getValue() != null) {
			immunizationList.getValue().getDataSource().invalidate();
			if (!immunizationList.getValue().isEmpty()) {
				immunizationList.getValue().loadAround(0);
			}
		}
	}

	public static class ImmunizationDataSource extends PositionalDataSource<Immunization> {

		private ImmunizationCriteria immunizationCriteria;

		ImmunizationDataSource(ImmunizationCriteria immunizationCriteria) {
			this.immunizationCriteria = immunizationCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Immunization> callback) {
			long totalCount = DatabaseHelper.getImmunizationDao().countByCriteria(immunizationCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Immunization> formDataList = DatabaseHelper.getImmunizationDao().queryByCriteria(immunizationCriteria, offset, count);
			callback.onResult(formDataList, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Immunization> callback) {
			List<Immunization> immunizationList =
				DatabaseHelper.getImmunizationDao().queryByCriteria(immunizationCriteria, params.startPosition, params.loadSize);
			callback.onResult(immunizationList);
		}
	}

	public static class ImmunizationFactory extends DataSource.Factory {

		private MutableLiveData<ImmunizationDataSource> mutableDataSource;
		private ImmunizationDataSource immunizationDataSource;
		private ImmunizationCriteria immunizationCriteria;

		ImmunizationFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			immunizationDataSource = new ImmunizationDataSource(immunizationCriteria);
			mutableDataSource.postValue(immunizationDataSource);
			return immunizationDataSource;
		}

		public ImmunizationCriteria getImmunizationCriteria() {
			return immunizationCriteria;
		}

		public void setImmunizationCriteria(ImmunizationCriteria immunizationCriteria) {
			this.immunizationCriteria = immunizationCriteria;
		}
	}
}
