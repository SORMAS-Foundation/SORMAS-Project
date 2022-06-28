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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.ui.utils.CssStyles;

public class CheckboxGroup<T> extends CustomField<Set<T>> {

	private static final long serialVersionUID = 4425620663378339988L;

	private VerticalLayout layout;
	private List<T> items;
	private Function<T, String> groupingFunction;

	@Override
	protected Component initContent() {
		layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setMargin(new MarginInfo(true, false));

		if (items != null) {
			buildCheckboxes(items);
		}

		return layout;
	}

	public void setItems(List<T> items, Function<T, String> groupingFunction) {
		this.items = items;
		this.groupingFunction = groupingFunction;

		if (layout != null) {
			layout.removeAllComponents();
			buildCheckboxes(items);
		}
	}

	private void buildCheckboxes(List<T> items) {
		List<String> groups;
		if (groupingFunction != null) {
			groups = items.stream().map(i -> groupingFunction.apply(i)).distinct().collect(Collectors.toList());
		} else {
			groups = Collections.singletonList(null);
		}

		groups.forEach((group) -> {
			List<T> groupItems = items;

			if (group != null) {
				Label groupLabel = new Label(group);
				groupLabel.addStyleName(CssStyles.H3);

				layout.addComponent(groupLabel);

				groupItems = items.stream().filter(i -> group.equals(groupingFunction.apply(i))).collect(Collectors.toList());
			}

			for (int i = 0, size = groupItems.size(); i < size - 1; i += 2) {
				T item1 = groupItems.get(i);
				T item2 = groupItems.get(i + 1);

				HorizontalLayout rowLayout = new HorizontalLayout();
				rowLayout.setWidthFull();
				rowLayout.setMargin(false);

				rowLayout.addComponent(new CheckBox(item1.toString(), createDataSource(item1)));

				if (item2 != null) {
					rowLayout.addComponent(new CheckBox(item2.toString(), createDataSource(item2)));
				}

				layout.addComponent(rowLayout);
			}
		});
	}

	private CheckBoxGroupItemDataSource createDataSource(T item1) {
		return new CheckBoxGroupItemDataSource(item1, v -> getInternalValue().contains(v), (c, v) -> {
			Set<T> internalValue = getInternalValue();
			if (c) {
				internalValue.add(v);
			} else {
				internalValue.remove(v);
			}
		});
	}

	@Override
	public Class<? extends Set<T>> getType() {
		//noinspection unchecked,InstantiatingObjectToGetClassObject,InstantiatingObjectToGetClassObject
		return (Class<? extends Set<T>>) new HashSet<T>(0).getClass();
	}

	@Override
	public void commit() throws SourceException, Validator.InvalidValueException {
		super.commit();
	}

	@Override
	protected void setInternalValue(Set<T> newValue) {
		super.setInternalValue(new HashSet<>(newValue));
	}

	private final class CheckBoxGroupItemDataSource implements Property<Boolean> {

		private static final long serialVersionUID = -1956478277209246907L;

		private final T propertyValue;
		private final Function<T, Boolean> isChecked;
		private final BiConsumer<Boolean, T> setChecked;

		private boolean readOnly;

		public CheckBoxGroupItemDataSource(T propertyValue, Function<T, Boolean> isChecked, BiConsumer<Boolean, T> setChecked) {
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
}
