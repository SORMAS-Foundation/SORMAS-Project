/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.explicatis.ext_token_field.ExtTokenField;
import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsAttributeEnum;
import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterValuesElement extends StatisticsFilterElement {

	private final StatisticsAttribute attribute;
	private final StatisticsSubAttribute subAttribute;

	private Registration valueChangeListenerRegistration;
	private ExtTokenField tokenField;
	private ComboBox<TokenizableValue> addDropdown;

	/**
	 * Only needed when this element is part of a Region/District element.
	 */
	private StatisticsFilterRegionDistrictElement regionDistrictElement;

	public StatisticsFilterValuesElement(String caption, StatisticsAttribute attribute, StatisticsSubAttribute subAttribute) {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		this.attribute = attribute;
		this.subAttribute = subAttribute;

		ExtTokenField tokenField = createTokenField(caption);
		VerticalLayout utilityButtonsLayout = createUtilityButtonsLayout();
		addComponent(tokenField);
		addComponent(utilityButtonsLayout);
		setExpandRatio(tokenField, 1);
		setExpandRatio(utilityButtonsLayout, 0);
		setComponentAlignment(utilityButtonsLayout, Alignment.MIDDLE_RIGHT);
	}

	public StatisticsFilterValuesElement(String caption, StatisticsAttribute attribute, StatisticsSubAttribute subAttribute, StatisticsFilterRegionDistrictElement regionDistrictElement) {
		this(caption, attribute, subAttribute);
		this.regionDistrictElement = regionDistrictElement;
	}

	public void updateDropdownContent() {
		addDropdown.setItems(getFilterValues());
	}

	private ExtTokenField createTokenField(String caption) {
		tokenField = new ExtTokenField();
		tokenField.setCaption(caption);
		tokenField.setWidth(100, Unit.PERCENTAGE);
		tokenField.setEnableDefaultDeleteTokenAction(true);

		addDropdown = new ComboBox<TokenizableValue>("", getFilterValues());
		addDropdown.addStyleName(CssStyles.VSPACE_NONE);
		addDropdown.setPlaceholder(I18nProperties.getString(Strings.promptTypeToAdd));
		tokenField.setInputField(addDropdown);
		addDropdown.addValueChangeListener(e -> {
			TokenizableValue token = e.getValue();
			if (token != null) {
				tokenField.addTokenizable(token);
				addDropdown.setValue(null);
			}
		});

		return tokenField;
	}

	private VerticalLayout createUtilityButtonsLayout() {
		VerticalLayout utilityButtonsLayout = new VerticalLayout();
		utilityButtonsLayout.setMargin(false);
		utilityButtonsLayout.setSpacing(false);
		utilityButtonsLayout.setSizeUndefined();

		Button addAllButton = new Button(I18nProperties.getCaption(Captions.all), VaadinIcons.PLUS_CIRCLE);
		CssStyles.style(addAllButton, ValoTheme.BUTTON_LINK);
		addAllButton.addClickListener(e -> {
			for (TokenizableValue tokenizable : getFilterValues()) {
				tokenField.addTokenizable(tokenizable);
			}
		});

		Button removeAllButton = new Button(I18nProperties.getCaption(Captions.actionClear), VaadinIcons.CLOSE_CIRCLE);
		CssStyles.style(removeAllButton, ValoTheme.BUTTON_LINK);
		removeAllButton.addClickListener(e -> {
			for (Tokenizable tokenizable : tokenField.getValue()) {
				tokenField.removeTokenizable(tokenizable);
			}
		});

		utilityButtonsLayout.addComponent(addAllButton);
		utilityButtonsLayout.addComponent(removeAllButton);

		return utilityButtonsLayout;
	}

	private List<TokenizableValue> getFilterValues() {
		if (subAttribute != null) {
			switch (StatisticsSubAttribute.getBaseEnum(subAttribute)) {
				case REGION:
					return createTokens(FacadeProvider.getRegionFacade().getAllActiveAsReference());
				case DISTRICT:
					if (regionDistrictElement == null) {
						return createTokens(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
					}
					
					List<TokenizableValue> selectedRegionTokenizables = regionDistrictElement.getSelectedRegions();
					if (CollectionUtils.isNotEmpty(selectedRegionTokenizables)) {
						List<DistrictReferenceDto> districts = new ArrayList<>();
						for (TokenizableValue selectedRegionTokenizable : selectedRegionTokenizables) {
							RegionReferenceDto selectedRegion = (RegionReferenceDto) selectedRegionTokenizable.getValue();
							districts.addAll(FacadeProvider.getDistrictFacade().getAllActiveByRegion(selectedRegion.getUuid()));
						}
						return createTokens(districts);
					} else {
						return createTokens(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
					}
				default:
					return createTokens(subAttribute.getValues(attribute));
			}
		}
		else {
			List<TokenizableValue> tokens = createTokens(attribute.getValues());
			
			if (attribute.getBaseEnum() == StatisticsAttributeEnum.SEX)
				tokens.add(new TokenizableValue(I18nProperties.getCaption(Captions.unknown), tokens.size()));
			
			return tokens;
		}
	}

	public void setValueChangeListener(ValueChangeListener<List<Tokenizable>> valueChangeListener) {
		if (valueChangeListenerRegistration != null) {
			valueChangeListenerRegistration.remove();
			valueChangeListenerRegistration = null;
		}

		if (valueChangeListener != null) {
			valueChangeListenerRegistration = tokenField.addValueChangeListener(valueChangeListener);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TokenizableValue> getSelectedValues() {
		return (List<TokenizableValue>)(List<? extends Tokenizable>)tokenField.getValue();
	}

	public ExtTokenField getTokenField() {
		return tokenField;
	}
	
}
