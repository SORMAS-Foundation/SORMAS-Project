/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class CustomizableEnumPropertiesComponent extends CustomField<Map<String, Object>> {

	private VerticalLayout layout;
	private Map<String, Class<?>> allProperties;
	private Map<String, Object> properties;

	@Override
	protected Component initContent() {

		layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setMargin(new MarginInfo(false, false, true, false));
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.VSPACE_TOP_4);

		if (allProperties != null) {
			buildPropertyFields();
		}

		return layout;
	}

	@Override
	public Class<? extends Map<String, Object>> getType() {
		//noinspection unchecked,InstantiatingObjectToGetClassObject,InstantiatingObjectToGetClassObject
		return (Class<? extends Map<String, Object>>) new HashMap<String, Object>().getClass();
	}

	private void buildPropertyFields() {

		if (layout == null) {
			return;
		}

		layout.removeAllComponents();

		allProperties.keySet().forEach(p -> {
			if (allProperties.get(p) == boolean.class) {
				Boolean value = (Boolean) properties.get(p);
				CheckBox checkBox = new CheckBox(I18nProperties.getPrefixCaption(CustomizableEnum.I18N_PREFIX, p), value != null ? value : false);
				CssStyles.style(checkBox, CssStyles.VSPACE_NONE);
				checkBox.addValueChangeListener(e -> properties.put(p, e.getValue()));
				layout.addComponent(checkBox);
			} else {
				throw new UnsupportedOperationException(
					String.format("Class %s is not yet implemented for properties component", allProperties.get(p).getName()));
			}
		});
	}

	@Override
	public void setValue(Map<String, Object> newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		this.properties = newFieldValue != null ? newFieldValue : new HashMap<>();
	}

	@Override
	protected Map<String, Object> getInternalValue() {
		return properties != null ? (!properties.isEmpty() ? properties : null) : null;
	}

	public void setAllProperties(Map<String, Class<?>> allProperties) {
		this.allProperties = allProperties;
		buildPropertyFields();
	}

	public boolean hasContent() {
		return !allProperties.isEmpty();
	}
}
