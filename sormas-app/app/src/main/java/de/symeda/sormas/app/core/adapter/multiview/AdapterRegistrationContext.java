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

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.core.ClassUtil;

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterRegistrationContext<E extends Enum<E>> implements IAdapterRegistrationContext {

	private Context context;
	private E viewType;
	private EnumMapDataBinderAdapter<E> dataBindAdapter;

	public AdapterRegistrationContext(Context context, E viewType, EnumMapDataBinderAdapter<E> dataBindAdapter) {
		this.context = context;
		this.viewType = viewType;
		this.dataBindAdapter = dataBindAdapter;
	}

	@Override
	public <TDataItem, T1 extends RecyclerView.ViewHolder, T2 extends DataBinder<T1, TDataItem>> IAdapterRegistrationData<TDataItem> registerBinder(
		Class<T2> binder)
		throws IllegalAccessException, InstantiationException {

		if (!ClassUtil.hasParameterlessPublicConstructor(binder))
			throw new InstantiationException("DataBind MUST have a parameterless constructor.");

		T2 dataBinder = binder.newInstance();
		dataBinder.setContext(this.context);
		dataBinder.setDataBinderAdapter(this.dataBindAdapter);
		this.dataBindAdapter.registerBinder(this.viewType, dataBinder);

		return new AdapterRegistrationData(dataBinder);
	}
}
