/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;

public class DataProviderWrapper<T, F> extends AbstractDataProvider<T, F> {

	private static final long serialVersionUID = -303172503455445508L;

	private final DataProvider<T, F> dataProvider;

	/**
	 * @param dataProvider
	 *            The actual data provider behind this wrapper.
	 */
	public DataProviderWrapper(DataProvider<T, F> dataProvider) {
		this.dataProvider = Objects.requireNonNull(dataProvider, "The wrapped data provider cannot be null.");
	}

	/**
	 * @return The actual data provider behind this wrapper.
	 */
	public DataProvider<T, F> getWrappedDataProvider() {
		return dataProvider;
	}

	@Override
	public boolean isInMemory() {
		return dataProvider.isInMemory();
	}

	@Override
	public int size(Query<T, F> query) {

		int size = dataProvider.size(query);
		fireEvent(new DataSizeChangeEvent<>(this, size));
		return size;
	}

	@Override
	public Stream<T> fetch(Query<T, F> query) {
		return dataProvider.fetch(query);
	}

	@Override
	public void refreshItem(T item) {
		dataProvider.refreshItem(item);
	}

	@Override
	public void refreshAll() {
		dataProvider.refreshAll();
	}
}
