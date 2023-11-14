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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.util.ReflectTools;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class CheckboxSet<T> extends CustomField<Set<T>> {

	private static final long serialVersionUID = 4425620663378339988L;

	private VerticalLayout layout;

	private List<CheckboxRow> rows = new ArrayList<>(0);

	private List<T> items;
	private Function<T, String> groupingFunction;
	private Function<T, String> itemDescriptionProvider;

	private int columnCount = 2;

	@Override
	protected Component initContent() {
		layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setMargin(new MarginInfo(true, false));

		if (items != null) {
			resetLayout();
		}

		return layout;
	}

	public void setItems(List<T> items, Function<T, String> groupingFunction, Function<T, String> itemDescriptionProvider) {
		this.items = items;
		this.groupingFunction = groupingFunction;
		this.itemDescriptionProvider = itemDescriptionProvider;

		resetLayout();
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		resetLayout();
	}

	private void resetLayout() {
		if (layout == null) {
			return;
		}

		layout.removeAllComponents();
		rows = buildCheckboxRows(items);
	}

	private List<CheckboxRow> buildCheckboxRows(List<T> items) {
		List<String> groups;
		if (groupingFunction != null) {
			groups = items.stream().map(i -> groupingFunction.apply(i)).distinct().collect(Collectors.toList());
		} else {
			groups = Collections.singletonList(null);
		}

		return groups.stream().map((group) -> {
			List<CheckboxRow> groupRows;
			if (group != null) {
				groupRows = createRows(items.stream().filter(i -> group.equals(groupingFunction.apply(i))).collect(Collectors.toList()));
				layout.addComponent(createGroupHeader(group, groupRows));
			} else {
				groupRows = createRows(items);
			}

			layout.addComponents(groupRows.toArray(new Component[] {}));
			return groupRows;
		}).flatMap(List::stream).collect(Collectors.toList());
	}

	private List<CheckboxRow> createRows(List<T> groupItems) {
		List<CheckboxRow> rows = new ArrayList<>();
		for (int i = 0, size = groupItems.size(); i < size; i += columnCount) {
			List<T> rowItems = groupItems.subList(i, Math.min(i + columnCount, groupItems.size()));

			CheckboxRow checkboxRow = new CheckboxRow(rowItems, columnCount);

			checkboxRow.checkBoxes.forEach(cb -> cb.addValueChangeListener(e -> {
				fireEvent(new CheckboxValueChangeEvent(cb));
			}));

			rows.add(checkboxRow);
		}

		return rows;
	}

	private HorizontalLayout createGroupHeader(String group, List<CheckboxRow> rows) {
		Label groupLabel = new Label(group);
		groupLabel.addStyleName(CssStyles.H3);

		Label tickForAllLAbel = new Label(I18nProperties.getString(Strings.checkboxSetTickAnAnswerForAll));
		tickForAllLAbel.addStyleName(CssStyles.LABEL_ITALIC);

		Button yesAllBurron = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.actionYesAll),
			(e) -> rows.forEach(r -> r.setValueForAll(true)),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		Button noAllButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.actionNoAll),
			(e) -> rows.forEach(r -> r.setValueForAll(false)),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);

		HorizontalLayout buttonsLayout = new HorizontalLayout(tickForAllLAbel, yesAllBurron, noAllButton);
		buttonsLayout.setWidthFull();
		buttonsLayout.setExpandRatio(tickForAllLAbel, 1);

		HorizontalLayout headerLayout = new HorizontalLayout(groupLabel, buttonsLayout);
		headerLayout.setWidthFull();

		headerLayout.setExpandRatio(groupLabel, 0.5f);
		headerLayout.setExpandRatio(buttonsLayout, 0.5f);
		headerLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);

		return headerLayout;
	}

	private CheckBox createCheckbox(T item) {
		CheckBox checkBox = new CheckBox(item.toString(), createDataSource(item));
		checkBox.setData(item);
		if (itemDescriptionProvider != null) {
			checkBox.setDescription(itemDescriptionProvider.apply(item));
		}
		checkBox.addValueChangeListener(e -> fireValueChange(false));

		return checkBox;
	}

	private CheckboxSetItemDataSource<T> createDataSource(T item) {
		return new CheckboxSetItemDataSource<>(item, v -> getSafeInternalValue().contains(v), (c, v) -> {
			Set<T> internalValue = getSafeInternalValue();
			if (c) {
				internalValue.add(v);
			} else {
				internalValue.remove(v);
			}

			setInternalValue(internalValue);
		});
	}

	@Override
	public Class<? extends Set<T>> getType() {
		//noinspection unchecked,InstantiatingObjectToGetClassObject,InstantiatingObjectToGetClassObject
		return (Class<? extends Set<T>>) new HashSet<T>(0).getClass();
	}

	@Override
	protected void setInternalValue(Set<T> newValue) {
		if (newValue != null) {
			super.setInternalValue(new HashSet<>(newValue));
			rows.forEach(r -> r.checkBoxes.forEach(c -> c.setValue(newValue.contains(c.getData()))));
		} else {
			super.setInternalValue(newValue);
		}
	}

	protected Set<T> getSafeInternalValue() {
		Set<T> internalValue = getInternalValue();
		return internalValue != null ? new HashSet<>(internalValue) : new HashSet<>();
	}

	public Registration addCheckboxValueChangeListener(CheckboxValueChangeListener listener) {
		return addListener(CheckboxValueChangeEvent.class, listener, CheckboxValueChangeListener.CHECKBOX_CHANGE_METHOD);
	}

	public Optional<CheckBox> getCheckboxByData(T data) {
		return rows.stream().map(r -> r.checkBoxes).flatMap(Collection::stream).filter(checkBox -> data.equals(checkBox.getData())).findFirst();
	}

	public interface CheckboxValueChangeListener extends SerializableEventListener {

		Method CHECKBOX_CHANGE_METHOD =
			ReflectTools.findMethod(CheckboxValueChangeListener.class, "checkboxValueChange", CheckboxValueChangeEvent.class);

		void checkboxValueChange(CheckboxValueChangeEvent event);

	}

	public static final class CheckboxValueChangeEvent extends Component.Event {

		private static final long serialVersionUID = 7689979222604280185L;

		public CheckboxValueChangeEvent(CheckBox source) {
			super(source);
		}

		public CheckBox getCheckbox() {
			return (CheckBox) getComponent();
		}
	}

	private final class CheckboxRow extends HorizontalLayout {

		private static final long serialVersionUID = -3874252190392021250L;

		private final List<CheckBox> checkBoxes;

		public CheckboxRow(List<T> items, int columnCount) {
			int itemCount = items.size();
			checkBoxes = new ArrayList<>(itemCount);
			setWidthFull();
			setMargin(false);

			items.forEach(i -> {
				CheckBox cb = createCheckbox(i);
				addComponent(cb);
				checkBoxes.add(cb);
			});

			if (columnCount > itemCount) {
				// add empty columns to fill the row
				addComponent(new HorizontalLayout());
			}
		}

		public void setValueForAll(boolean checked) {
			checkBoxes.forEach(cb -> cb.setValue(checked));
		}
	}
}
