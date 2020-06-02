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

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
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
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterValuesElement extends StatisticsFilterElement {

	private final StatisticsCaseAttribute attribute;
	private final StatisticsCaseSubAttribute subAttribute;

	private Registration valueChangeListenerRegistration;
	private ExtTokenField tokenField;
	private ComboBox<TokenizableValue> addDropdown;

	/**
	 * Only needed when this element is part of a Region/District element.
	 */
	private StatisticsFilterJurisdictionElement jurisdictionElement;

	public StatisticsFilterValuesElement(String caption, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute, int rowIndex) {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		this.attribute = attribute;
		this.subAttribute = subAttribute;

		ExtTokenField tokenField = createTokenField(caption, rowIndex);
		VerticalLayout utilityButtonsLayout = createUtilityButtonsLayout(rowIndex);
		addComponent(tokenField);
		addComponent(utilityButtonsLayout);
		setExpandRatio(tokenField, 1);
		setExpandRatio(utilityButtonsLayout, 0);
		setComponentAlignment(utilityButtonsLayout, Alignment.MIDDLE_RIGHT);
	}

	public StatisticsFilterValuesElement(String caption, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute, StatisticsFilterJurisdictionElement jurisdictionElement, int rowIndex) {
		this(caption, attribute, subAttribute, rowIndex);
		this.jurisdictionElement = jurisdictionElement;
	}

	public void updateDropdownContent() {
		addDropdown.setItems(getFilterValues());
	}

	private ExtTokenField createTokenField(String caption, int rowIndex) {
		tokenField = new ExtTokenField();
		tokenField.setId("tokens-" + rowIndex);
		tokenField.setCaption(caption);
		tokenField.setWidth(100, Unit.PERCENTAGE);
		tokenField.setEnableDefaultDeleteTokenAction(true);

		addDropdown = new ComboBox<TokenizableValue>("", getFilterValues());
		addDropdown.setId("select-" + rowIndex);
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

	private VerticalLayout createUtilityButtonsLayout(int rowIndex) {
		VerticalLayout utilityButtonsLayout = new VerticalLayout();
		utilityButtonsLayout.setMargin(false);
		utilityButtonsLayout.setSpacing(false);
		utilityButtonsLayout.setSizeUndefined();

		Button addAllButton = ButtonHelper.createIconButtonWithCaption(Captions.all + "-" + rowIndex, I18nProperties.getCaption(Captions.all), VaadinIcons.PLUS_CIRCLE, e -> {
			for (TokenizableValue tokenizable : getFilterValues()) {
				tokenField.addTokenizable(tokenizable);
			}
		}, ValoTheme.BUTTON_LINK);

		Button removeAllButton = ButtonHelper.createIconButtonWithCaption(Captions.actionClear + "-" + rowIndex, I18nProperties.getCaption(Captions.actionClear), VaadinIcons.CLOSE_CIRCLE, e -> {
			for (Tokenizable tokenizable : tokenField.getValue()) {
				tokenField.removeTokenizable(tokenizable);
			}
		}, ValoTheme.BUTTON_LINK);

		utilityButtonsLayout.addComponent(addAllButton);
		utilityButtonsLayout.addComponent(removeAllButton);

		return utilityButtonsLayout;
	}

	private List<TokenizableValue> getFilterValues() {
		if (subAttribute != null) {
			switch (subAttribute) {
				case YEAR:
				case QUARTER:
				case MONTH:
				case EPI_WEEK:
				case QUARTER_OF_YEAR:
				case MONTH_OF_YEAR:
				case EPI_WEEK_OF_YEAR:
					List<StatisticsGroupingKey> dateValues = StatisticsHelper.getTimeGroupingKeys(attribute, subAttribute);
					return createTokens(dateValues);
				case REGION:
					return createTokens(FacadeProvider.getRegionFacade().getAllActiveAsReference());
				case DISTRICT:
					if (jurisdictionElement == null) {
						return createTokens(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
					}

					List<TokenizableValue> selectedRegionTokenizables = jurisdictionElement.getSelectedRegions();
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
				case COMMUNITY:
					if (jurisdictionElement == null) {
						return new ArrayList<>();
					}

					List<TokenizableValue> selectedDistrictTokenizables = jurisdictionElement.getSelectedDistricts();
					if (CollectionUtils.isNotEmpty(selectedDistrictTokenizables)) {
						List<CommunityReferenceDto> communities = new ArrayList<>();
						for (TokenizableValue selectedDistrictTokenizable : selectedDistrictTokenizables) {
							DistrictReferenceDto selectedDistrict = (DistrictReferenceDto) selectedDistrictTokenizable.getValue();
							communities.addAll(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(selectedDistrict.getUuid()));
						}
						return createTokens(communities);
					} else {
						return new ArrayList<>();
					}
				case HEALTH_FACILITY:
					if (jurisdictionElement == null) {
						return new ArrayList<>();
					}

					selectedDistrictTokenizables = jurisdictionElement.getSelectedDistricts();
					List<TokenizableValue> selectedCommunityTokenizables = jurisdictionElement.getSelectedCommunities();
					if (CollectionUtils.isNotEmpty(selectedCommunityTokenizables)) {
						List<FacilityReferenceDto> facilities = new ArrayList<>();
						for (TokenizableValue selectedCommunityTokenizable : selectedCommunityTokenizables) {
							CommunityReferenceDto selectedCommunity = (CommunityReferenceDto) selectedCommunityTokenizable.getValue();
							facilities.addAll(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(selectedCommunity, false));
						}
						return createTokens(facilities);
					} else if (CollectionUtils.isNotEmpty(selectedDistrictTokenizables)) {
						List<FacilityReferenceDto> facilities = new ArrayList<>();
						for (TokenizableValue selectedDistrictTokenizable : selectedDistrictTokenizables) {
							DistrictReferenceDto selectedDistrict = (DistrictReferenceDto) selectedDistrictTokenizable.getValue();
							facilities.addAll(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(selectedDistrict, false));
						}
						return createTokens(facilities);
					} else {
						return new ArrayList<>();
					}
				default:
					throw new IllegalArgumentException(this.toString());
			}
		} else {
			switch (attribute) {
				case SEX:
					List<TokenizableValue> tokens = createTokens(Sex.values());
					tokens.add(new TokenizableValue(I18nProperties.getCaption(Captions.unknown), tokens.size()));
					return tokens;
				case AGE_INTERVAL_1_YEAR:
				case AGE_INTERVAL_5_YEARS:
				case AGE_INTERVAL_CHILDREN_COARSE:
				case AGE_INTERVAL_CHILDREN_FINE:
				case AGE_INTERVAL_CHILDREN_MEDIUM:
				case AGE_INTERVAL_BASIC:
					List<StatisticsGroupingKey> ageIntervalValues = StatisticsHelper.getAgeIntervalGroupingKeys(attribute);
					return createTokens(ageIntervalValues);
				case DISEASE:
					return createTokens(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
				case CLASSIFICATION:
					return createTokens(CaseClassification.values());
				case OUTCOME:
					return createTokens(CaseOutcome.values());
				case REPORTING_USER_ROLE:
					return createTokens(UserRole.values());
				default:
					throw new IllegalArgumentException(this.toString());
			}
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
