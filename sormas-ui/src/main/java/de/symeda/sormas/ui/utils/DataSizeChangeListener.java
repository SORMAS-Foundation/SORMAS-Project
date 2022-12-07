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

import java.util.function.Consumer;

import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.DataProviderListener;

public class DataSizeChangeListener<T> implements DataProviderListener<T> {

	private static final long serialVersionUID = -5595860647927756469L;

	private final Consumer<Integer> dataSizeConsumer;

	/**
	 * @param dataSizeConsumer
	 *            If the data changes, {@code consumer} gets notified with the new data size.
	 */
	public DataSizeChangeListener(Consumer<Integer> dataSizeConsumer) {
		this.dataSizeConsumer = dataSizeConsumer;
	}

	@Override
	public void onDataChange(DataChangeEvent<T> event) {

		if (event instanceof DataSizeChangeEvent) {
			dataSizeConsumer.accept(((DataSizeChangeEvent<?>) event).getSize());
		}
	}
}
