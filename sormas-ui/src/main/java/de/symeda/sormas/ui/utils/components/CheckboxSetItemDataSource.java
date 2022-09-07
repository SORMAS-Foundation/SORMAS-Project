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

package de.symeda.sormas.ui.utils.components;

import com.vaadin.v7.data.Property;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CheckboxSetItemDataSource<T> implements Property<Boolean> {

	private static final long serialVersionUID = -1956478277209246907L;

	private final T propertyValue;
	private final Function<T, Boolean> isChecked;
	private final BiConsumer<Boolean, T> setChecked;

	private boolean readOnly;

	public CheckboxSetItemDataSource(T propertyValue, Function<T, Boolean> isChecked, BiConsumer<Boolean, T> setChecked) {
		this.propertyValue = propertyValue;
		this.isChecked = isChecked;
		this.setChecked = setChecked;
	}

	@Override
	public Boolean getValue() {
		return isChecked.apply(propertyValue);
	}

	@Override
	public void setValue(Boolean checked) throws ReadOnlyException {
		if (readOnly) {
			throw new ReadOnlyException();
		}

		setChecked.accept(checked, propertyValue);
	}

	@Override
	public Class<? extends Boolean> getType() {
		return Boolean.class;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
