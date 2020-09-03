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

package de.symeda.sormas.app.core.adapter.multiview;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterRegistrationData<TDataItem, T1 extends RecyclerView.ViewHolder> implements IAdapterRegistrationData<TDataItem> {

	private List<TDataItem> data;
	private DataBinder<T1, TDataItem> dataBinder;

	public AdapterRegistrationData(DataBinder<T1, TDataItem> dataBinder) {
		this.dataBinder = dataBinder;
	}

	@Override
	public IAdapterRegistrationData<TDataItem> registerData(List<TDataItem> data) {
		this.data = data;
		this.dataBinder.addAll(data);
		return this;
	}

	public IAdapterRegistrationData<TDataItem> forEach(IAdapterDataModifier modifier) {

		//For each item in data
		int position = 0;
		for (TDataItem item : this.data) {
			modifier.modify(item, position);
			position = position + 1;
		}
		return this;
	}
}
/*
 * public interface IAdapterRegistrationData<TDataItem> {
 * IAdapterRegistrationData<TDataItem> registerData(List<TDataItem> data);
 * IAdapterRegistrationData<TDataItem> forEach(IAdapterDataModifier modifier);
 * }
 */
