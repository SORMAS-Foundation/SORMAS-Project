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

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterConfiguration<E extends Enum<E>> implements IAdapterConfiguration<E> {

	private Context context;
	private IAdapterRegistrationContext registrationContext;
	private EnumMapDataBinderAdapter<E> dataBindAdapter;

	public AdapterConfiguration(Context context, EnumMapDataBinderAdapter<E> dataBindAdapter) {
		this.context = context;
		this.dataBindAdapter = dataBindAdapter;
	}

	@Override
	public IAdapterConfiguration forViewType(E viewType, IAdapterRegistrationService serivce) throws IllegalAccessException, InstantiationException {
		this.registrationContext = new AdapterRegistrationContext<E>(this.context, viewType, this.dataBindAdapter);
		serivce.register(this.registrationContext);
		return this;
	}
}
