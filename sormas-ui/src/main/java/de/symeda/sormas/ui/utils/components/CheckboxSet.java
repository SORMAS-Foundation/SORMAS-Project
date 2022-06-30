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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
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
			List<CheckboxRow> rows;
			if (group != null) {
				rows = createRows(items.stream().filter(i -> group.equals(groupingFunction.apply(i))).collect(Collectors.toList()));
				layout.addComponent(createGroupHeader(group, rows));
			} else {
				rows = createRows(items);
			}

			layout.addComponents(rows.toArray(new Component[] {}));
		});
	}

	private List<CheckboxRow> createRows(List<T> groupItems) {
		List<CheckboxRow> rows = new ArrayList<>();
		for (int i = 0, size = groupItems.size(); i < size; i += 2) {
			T item1 = groupItems.get(i);
			T item2 = i < size - 1 ? groupItems.get(i + 1) : null;

			rows.add(new CheckboxRow(item1, item2));
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
			(e) -> rows.forEach(r -> r.setAllChecked(true)),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		Button noAllButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.actionNoAll),
			(e) -> rows.forEach(r -> r.setAllChecked(false)),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);

		HorizontalLayout buttonsLayout = new HorizontalLayout(tickForAllLAbel, yesAllBurron, noAllButton);
		buttonsLayout.setWidthFull();
		buttonsLayout.setExpandRatio(tickForAllLAbel, 1);

		HorizontalLayout headerLayout = new HorizontalLayout(groupLabel, buttonsLayout);
		headerLayout.setWidthFull();

		headerLayout.setExpandRatio(groupLabel, 0.5f);
		headerLayout.setExpandRatio(buttonsLayout, 0.5f);

		return headerLayout;
	}

	private CheckBox createCheckbox(T item1) {
		CheckBox checkBox = new CheckBox(item1.toString(), createDataSource(item1));

		checkBox.addValueChangeListener(e -> fireValueChange(false));

		return checkBox;
	}

	private CheckboxSetItemDataSource<T> createDataSource(T item) {
		return new CheckboxSetItemDataSource<>(item, v -> getInternalValue().contains(v), (c, v) -> {
			Set<T> internalValue = getInternalValue();
			if (c) {
				internalValue.add(v);
			} else {
				internalValue.remove(v);
			}
		});
	}

	private final class CheckboxRow extends HorizontalLayout {

		private static final long serialVersionUID = -3874252190392021250L;

		private final CheckBox left;
		private final CheckBox right;

		public CheckboxRow(T leftItem, T rightItem) {
			setWidthFull();
			setMargin(false);

			left = createCheckbox(leftItem);
			addComponent(left);

			if (rightItem != null) {
				right = createCheckbox(rightItem);
				addComponent(right);
			} else {
				right = null;
			}
		}

		public void setAllChecked(boolean checked) {
			left.setValue(checked);

			if (right != null) {
				right.setValue(checked);
			}
		}
	}

	@Override
	public Class<? extends Set<T>> getType() {
		//noinspection unchecked,InstantiatingObjectToGetClassObject,InstantiatingObjectToGetClassObject
		return (Class<? extends Set<T>>) new HashSet<T>(0).getClass();
	}

	@Override
	protected void setInternalValue(Set<T> newValue) {
		super.setInternalValue(new HashSet<>(newValue));
	}

}
