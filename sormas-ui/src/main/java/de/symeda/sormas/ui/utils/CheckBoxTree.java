/*
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

@SuppressWarnings("deprecation")
public class CheckBoxTree<ENUM extends Enum<?>> extends VerticalLayout {

	private List<CheckBoxElement<ENUM>> checkBoxElements;
	private Map<ENUM, CheckBox> enumToggles = new HashMap<>();
	private Map<ENUM, Boolean> values;
	private Runnable valueChangeCallback;

	public CheckBoxTree(List<CheckBoxElement<ENUM>> checkBoxElements) {
		this(checkBoxElements, false, null);
	}

	public CheckBoxTree(List<CheckBoxElement<ENUM>> checkBoxElements, boolean addVerticalSpaces, Runnable valueChangeCallback) {
		this.checkBoxElements = checkBoxElements;
		this.valueChangeCallback = valueChangeCallback;
		this.setMargin(false);
		this.setSpacing(false);
		this.setWidth(100, Unit.PERCENTAGE);

		for (CheckBoxElement<ENUM> checkBoxElement : checkBoxElements) {
			final CheckBox checkBox = new CheckBox(checkBoxElement.getEnumElement().toString());
			if (addVerticalSpaces) {
				CssStyles.style(checkBox, CssStyles.VSPACE_4);
			}
			checkBox.setWidth(100, Unit.PERCENTAGE);
			int indent = getIndent(checkBoxElement);
			if (indent == 1) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_1);
			} else if (indent == 2) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_2);
			} else if (indent == 3) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_3);
			}

			final CheckBoxElement parent = checkBoxElement.getParent();
			if (parent != null) {
				final CheckBox parentCheckBox = enumToggles.get(parent.getEnumElement());
				checkBox.setVisible(parentCheckBox.getValue());
				parentCheckBox.addValueChangeListener(parentChangeEvent -> {
					checkBox.setValue(null);
					checkBox.setVisible((Boolean) parentChangeEvent.getProperty().getValue());
				});
			}

			addComponent(checkBox);
			enumToggles.put(checkBoxElement.getEnumElement(), checkBox);
		}
	}

	public void initCheckboxes() {
		for (CheckBoxElement checkBoxElement : checkBoxElements) {
			final ENUM enumValue = (ENUM) checkBoxElement.getEnumElement();
			final CheckBox checkBox = enumToggles.get(enumValue);
			checkBox.setValue(values.containsKey(enumValue) && values.get(enumValue) != null ? values.get(enumValue) : false);
			checkBox.addValueChangeListener(valueChangeEvent -> {
				values.put(enumValue, (Boolean) valueChangeEvent.getProperty().getValue());
				if (valueChangeCallback != null) {
					valueChangeCallback.run();
				}
			});
		}
	}

	private int getIndent(CheckBoxElement checkBoxElement) {
		int indent = 0;
		while (checkBoxElement.getParent() != null) {
			indent++;
			checkBoxElement = checkBoxElement.getParent();
		}
		return indent;
	}

	public Map<ENUM, Boolean> getValues() {
		return values;
	}

	public void setValues(Map<ENUM, Boolean> values) {
		if (values == null) {
			values = new HashMap<>();
		}
		this.values = values;
	}

	public void clearCheckBoxTree() {
		this.values = new HashMap<>();
		this.enumToggles.forEach((anEnum, checkBox) -> checkBox.setValue(false));
	}

	public static class CheckBoxElement<ENUM extends Enum<?>> {

		private CheckBoxElement<ENUM> parent;
		private ENUM enumElement;

		public CheckBoxElement(CheckBoxElement<ENUM> parent, ENUM enumElement) {
			this.parent = parent;
			this.enumElement = enumElement;
		}

		public CheckBoxElement getParent() {
			return parent;
		}

		public ENUM getEnumElement() {
			return enumElement;
		}
	}
}
