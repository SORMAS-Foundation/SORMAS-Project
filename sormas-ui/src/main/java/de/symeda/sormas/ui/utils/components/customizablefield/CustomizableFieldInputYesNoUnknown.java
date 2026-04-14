/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.utils.components.customizablefield;

import java.util.EnumMap;
import java.util.Map;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Concrete {@link CustomizableFieldInput} for
 * {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#YES_NO_UNKNOWN}.
 * <p>
 * Renders three horizontal toggle {@link Button}s (Yes / No / Unknown). Clicking the active
 * button deselects it (nullable); clicking another button selects it. The selected value is
 * serialised to/from the DTO's {@code value} field as the enum name via
 * {@link CustomizableFieldValueDto#getValueAsYesNoUnknown()} and
 * {@link CustomizableFieldValueDto#setValueAsYesNoUnknown(YesNoUnknown)}.
 */
@SuppressWarnings({
	"java:S110", // suppress sonar too many parents warning
	"java:S2160" // suppress missing equals
})
public class CustomizableFieldInputYesNoUnknown extends CustomizableFieldInput<YesNoUnknown> {

	private static final long serialVersionUID = 1L;

	private final Map<YesNoUnknown, Button> buttons = new EnumMap<>(YesNoUnknown.class);
	/**
	 * Holds a value that was pushed via {@link #doSetValue(YesNoUnknown)} before
	 * {@link #buildInputComponent()} had a chance to create the buttons.
	 * Applied on first render.
	 */
	private YesNoUnknown pendingValue;

	public CustomizableFieldInputYesNoUnknown(CustomizableFieldMetadataDto metadata) {
		super(metadata);
	}

	@Override
	protected ValueProvider<CustomizableFieldValueDto, YesNoUnknown> getValueGetter() {
		return CustomizableFieldValueDto::getValueAsYesNoUnknown;
	}

	@Override
	protected Setter<CustomizableFieldValueDto, YesNoUnknown> getValueSetter() {
		return CustomizableFieldValueDto::setValueAsYesNoUnknown;
	}

	public Class<YesNoUnknown> getType() {
		return YesNoUnknown.class;
	}

	@Override
	protected Component buildInputComponent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		CssStyles.style(layout, CssStyles.YES_NO_UNKNOWN_GROUP);

		for (YesNoUnknown option : YesNoUnknown.values()) {
			Button btn = new Button(I18nProperties.getEnumCaption(option));
			btn.addClickListener(e -> {
				YesNoUnknown current = getValue();
				setValue(option == current ? null : option);
			});
			buttons.put(option, btn);
			layout.addComponent(btn);
		}

		if (pendingValue != null) {
			applyButtonStyles(pendingValue);
			pendingValue = null;
		}

		return layout;
	}

	/**
	 * Called by Vaadin when {@link #setValue(Object)} is invoked programmatically.
	 * Updates button highlighting to reflect the new selection.
	 * If the buttons have not been created yet, stores the value as pending.
	 */
	@Override
	protected void applyValueToWidget(YesNoUnknown value) {
		if (buttons.isEmpty()) {
			pendingValue = value;
		} else {
			applyButtonStyles(value);
		}
	}

	private void applyButtonStyles(YesNoUnknown selected) {
		for (Map.Entry<YesNoUnknown, Button> entry : buttons.entrySet()) {
			if (entry.getKey() == selected) {
				entry.getValue().addStyleName(CssStyles.YES_NO_UNKNOWN_OPTION_SELECTED);
			} else {
				entry.getValue().removeStyleName(CssStyles.YES_NO_UNKNOWN_OPTION_SELECTED);
			}
		}
	}
}
