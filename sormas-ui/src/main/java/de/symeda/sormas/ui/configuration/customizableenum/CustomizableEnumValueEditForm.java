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

package de.symeda.sormas.ui.configuration.customizableenum;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static java.util.function.Predicate.not;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.CheckboxSet;
import de.symeda.sormas.ui.utils.components.CustomizableEnumPropertiesComponent;
import de.symeda.sormas.ui.utils.components.CustomizableEnumTranslationComponent;

public class CustomizableEnumValueEditForm extends AbstractEditForm<CustomizableEnumValueDto> {

	private CustomizableEnumPropertiesComponent propertiesComponent;
	private CustomizableEnumTranslationComponent translationsComponent;
	private CheckboxSet<Disease> cbsDiseases;

	private static final String HTML_LAYOUT = fluidRowLocs(CustomizableEnumValueDto.DATA_TYPE, CustomizableEnumValueDto.UUID)
		+ fluidRowLocs(CustomizableEnumValueDto.VALUE, CustomizableEnumValueDto.CAPTION)
		+ fluidRowLocs(CustomizableEnumValueDto.DESCRIPTION)
		+ fluidRowLocs(CustomizableEnumValueDto.ACTIVE)
		+ fluidRowLocsCss(CssStyles.VSPACE_TOP_3, CustomizableEnumValueDto.PROPERTIES)
		+ fluidRowLocs(CustomizableEnumValueDto.TRANSLATIONS)
		+ fluidRowLocs(CustomizableEnumValueDto.DISEASES);

	public CustomizableEnumValueEditForm() {

		super(
			CustomizableEnumValueDto.class,
			CustomizableEnumValueDto.I18N_PREFIX,
			true,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getNoop());

		setWidth(840, Unit.PIXELS);
	}

	@Override
	protected void addFields() {

		addField(CustomizableEnumValueDto.DATA_TYPE, ComboBox.class);
		addField(CustomizableEnumValueDto.ACTIVE, CheckBox.class);
		addFields(
			CustomizableEnumValueDto.UUID,
			CustomizableEnumValueDto.VALUE,
			CustomizableEnumValueDto.CAPTION,
			CustomizableEnumValueDto.DESCRIPTION);

		setRequired(true, CustomizableEnumValueDto.CAPTION);
		setReadOnly(true, CustomizableEnumValueDto.DATA_TYPE, CustomizableEnumValueDto.UUID);
		setEnabled(false, CustomizableEnumValueDto.VALUE);

		propertiesComponent = addField(CustomizableEnumValueDto.PROPERTIES, CustomizableEnumPropertiesComponent.class);
		propertiesComponent.setCaption(I18nProperties.getPrefixCaption(CustomizableEnumValueDto.I18N_PREFIX, CustomizableEnumValueDto.PROPERTIES));

		translationsComponent = addField(CustomizableEnumValueDto.TRANSLATIONS, CustomizableEnumTranslationComponent.class);
		translationsComponent
			.setCaption(I18nProperties.getPrefixCaption(CustomizableEnumValueDto.I18N_PREFIX, CustomizableEnumValueDto.TRANSLATIONS));

		cbsDiseases = addField(CustomizableEnumValueDto.DISEASES, CheckboxSet.class);
		cbsDiseases.setColumnCount(3);
		cbsDiseases.setCaption(I18nProperties.getPrefixCaption(CustomizableEnumValueDto.I18N_PREFIX, CustomizableEnumValueDto.DISEASES));
	}

	private static List<Disease> getSelectableDiseases(Set<Disease> selectedDiseases) {

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		if (CollectionUtils.isNotEmpty(selectedDiseases)) {
			List<Disease> inactiveSelectedDiseases = selectedDiseases.stream().filter(not(diseases::contains)).collect(Collectors.toList());
			diseases.addAll(inactiveSelectedDiseases);
		}

		return diseases;
	}

	@Override
	public void setValue(CustomizableEnumValueDto newFieldValue) {

		super.setValue(newFieldValue);
		propertiesComponent.setValue(newFieldValue.getProperties());
		try {
			propertiesComponent.setAllProperties(newFieldValue.getDataType().getEnumClass().getConstructor().newInstance().getAllProperties());
			if (!propertiesComponent.hasContent()) {
				propertiesComponent.setVisible(false);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		translationsComponent.setValue(newFieldValue.getTranslations());

		if (cbsDiseases != null) {
			Set<Disease> selectedDiseases = newFieldValue.getDiseases();
			cbsDiseases.setItems(getSelectableDiseases(selectedDiseases), null, null);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
