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

package de.symeda.sormas.app.event.list;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventCriteria;

public class EventListViewModel extends ViewModel {

	private LiveData<PagedList<Event>> events;
	private EventDataFactory eventDataFactory;

	public EventListViewModel() {
		eventDataFactory = new EventDataFactory();
		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.eventStatus(null);
		eventDataFactory.setEventCriteria(eventCriteria);

		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder eventListBuilder = new LivePagedListBuilder(eventDataFactory, config);
		events = eventListBuilder.build();
	}

	public LiveData<PagedList<Event>> getEvents() {
		return events;
	}

	void notifyCriteriaUpdated() {
		if (events.getValue() != null) {
			events.getValue().getDataSource().invalidate();
			if (!events.getValue().isEmpty()) {
				events.getValue().loadAround(0);
			}
		}
	}

	public EventCriteria getEventCriteria() {
		return eventDataFactory.getEventCriteria();
	}

	public static class EventDataSource extends PositionalDataSource<Event> {

		private EventCriteria eventCriteria;

		EventDataSource(EventCriteria eventCriteria) {
			this.eventCriteria = eventCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Event> callback) {
			long totalCount = DatabaseHelper.getEventDao().countByCriteria(eventCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Event> events = DatabaseHelper.getEventDao().queryByCriteria(eventCriteria, offset, count);
			callback.onResult(events, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Event> callback) {
			List<Event> events = DatabaseHelper.getEventDao().queryByCriteria(eventCriteria, params.startPosition, params.loadSize);
			callback.onResult(events);
		}
	}

	public static class EventDataFactory extends DataSource.Factory {

		private MutableLiveData<EventDataSource> mutableDataSource;
		private EventDataSource eventDataSource;
		private EventCriteria eventCriteria;

		EventDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			eventDataSource = new EventDataSource(eventCriteria);
			mutableDataSource.postValue(eventDataSource);
			return eventDataSource;
		}

		void setEventCriteria(EventCriteria eventCriteria) {
			this.eventCriteria = eventCriteria;
		}

		EventCriteria getEventCriteria() {
			return eventCriteria;
		}
	}
}
