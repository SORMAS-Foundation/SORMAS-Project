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

package de.symeda.sormas.app.immunization.vaccination;

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
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.backend.vaccination.VaccinationCriteria;

public class VaccinationListViewModel extends ViewModel {

	private LiveData<PagedList<Vaccination>> vaccinations;
	private VaccinationDataFactory vaccinationDataFactory;

	public void initializeViewModel(Immunization immunization) {
		vaccinationDataFactory = new VaccinationDataFactory();
		VaccinationCriteria vaccinationCriteria = new VaccinationCriteria();
		vaccinationCriteria.immunization(immunization);
		vaccinationDataFactory.setVaccinationCriteria(vaccinationCriteria);
		initializeList();
	}

	public void initializeViewModel() {
		vaccinationDataFactory = new VaccinationDataFactory();
		VaccinationCriteria vaccinationCriteria = new VaccinationCriteria();
		vaccinationDataFactory.setVaccinationCriteria(vaccinationCriteria);
		initializeList();
	}

	public void initializeViewModel(Case caze) {
		vaccinationDataFactory = new VaccinationDataFactory();
		VaccinationCriteria vaccinationCriteria = new VaccinationCriteria();
		vaccinationCriteria.caze(caze);
		vaccinationDataFactory.setVaccinationCriteria(vaccinationCriteria);
		initializeList();
	}

	public void initializeViewModel(Contact contact) {
		vaccinationDataFactory = new VaccinationDataFactory();
		VaccinationCriteria vaccinationCriteria = new VaccinationCriteria();
		vaccinationCriteria.contact(contact);
		vaccinationDataFactory.setVaccinationCriteria(vaccinationCriteria);
		initializeList();
	}

	public void initializeViewModel(EventParticipant eventParticipant) {
		vaccinationDataFactory = new VaccinationDataFactory();
		VaccinationCriteria vaccinationCriteria = new VaccinationCriteria();
		vaccinationCriteria.eventParticipant(eventParticipant);
		vaccinationDataFactory.setVaccinationCriteria(vaccinationCriteria);
		initializeList();
	}

	public LiveData<PagedList<Vaccination>> getVaccinations() {
		return vaccinations;
	}

	void notifyCriteriaUpdated() {
		if (vaccinations.getValue() != null) {
			vaccinations.getValue().getDataSource().invalidate();
			if (!vaccinations.getValue().isEmpty()) {
				vaccinations.getValue().loadAround(0);
			}
		}
	}

	public VaccinationCriteria getVaccinationCriteria() {
		return vaccinationDataFactory.getVaccinationCriteria();
	}

	public static class VaccinationDataSource extends PositionalDataSource<Vaccination> {

		private VaccinationCriteria vaccinationCriteria;

		VaccinationDataSource(VaccinationCriteria vaccinationCriteria) {
			this.vaccinationCriteria = vaccinationCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Vaccination> callback) {
			long totalCount = DatabaseHelper.getVaccinationDao().countByCriteria(vaccinationCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Vaccination> vaccinations = DatabaseHelper.getVaccinationDao().queryByCriteria(vaccinationCriteria, offset, count);
			callback.onResult(vaccinations, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Vaccination> callback) {
			List<Vaccination> vaccinations =
				DatabaseHelper.getVaccinationDao().queryByCriteria(vaccinationCriteria, params.startPosition, params.loadSize);
			callback.onResult(vaccinations);
		}
	}

	public static class VaccinationDataFactory extends DataSource.Factory {

		private MutableLiveData<VaccinationDataSource> mutableDataSource;
		private VaccinationDataSource vaccinationDataSource;
		private VaccinationCriteria vaccinationCriteria;

		VaccinationDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			vaccinationDataSource = new VaccinationDataSource(vaccinationCriteria);
			mutableDataSource.postValue(vaccinationDataSource);
			return vaccinationDataSource;
		}

		void setVaccinationCriteria(VaccinationCriteria vaccinationCriteria) {
			this.vaccinationCriteria = vaccinationCriteria;
		}

		VaccinationCriteria getVaccinationCriteria() {
			return vaccinationCriteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder vaccinationListBuilder = new LivePagedListBuilder(vaccinationDataFactory, config);
		vaccinations = vaccinationListBuilder.build();
	}
}
