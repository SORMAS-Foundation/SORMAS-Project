/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration;

import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Layout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.configuration.customizableenum.CustomizableEnumValuesView;
import de.symeda.sormas.ui.configuration.docgeneration.DocumentTemplatesView;
import de.symeda.sormas.ui.configuration.docgeneration.emailtemplate.EmailTemplatesView;
import de.symeda.sormas.ui.configuration.infrastructure.AreasView;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.ContinentsView;
import de.symeda.sormas.ui.configuration.infrastructure.CountriesView;
import de.symeda.sormas.ui.configuration.infrastructure.DistrictsView;
import de.symeda.sormas.ui.configuration.infrastructure.FacilitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.PointsOfEntryView;
import de.symeda.sormas.ui.configuration.infrastructure.PopulationDataView;
import de.symeda.sormas.ui.configuration.infrastructure.RegionsView;
import de.symeda.sormas.ui.configuration.infrastructure.SubcontinentsView;
import de.symeda.sormas.ui.configuration.infrastructure.components.CountryCombo;
import de.symeda.sormas.ui.configuration.linelisting.LineListingConfigurationView;
import de.symeda.sormas.ui.configuration.outbreak.OutbreaksView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.FieldHelper;

public abstract class AbstractConfigurationView extends AbstractSubNavigationView<DirtyStateComponent> {

	private static final long serialVersionUID = 3193505016439327054L;

	public static final String ROOT_VIEW_NAME = "configuration";

	protected AbstractConfigurationView(String viewName) {
		super(viewName);
	}

	public static Class<? extends AbstractConfigurationView> registerViews(Navigator navigator) {

		Class<? extends AbstractConfigurationView> firstAccessibleView = null;

		if (UiUtil.permitted(FeatureType.OUTBREAKS, UserRight.OUTBREAK_VIEW)) {
			navigator.addView(OutbreaksView.VIEW_NAME, OutbreaksView.class);
			firstAccessibleView = OutbreaksView.class;
		}

		boolean isCaseSurveillanceEnabled = UiUtil.enabled(FeatureType.CASE_SURVEILANCE);
		boolean isAnySurveillanceEnabled = FacadeProvider.getFeatureConfigurationFacade().isAnySurveillanceEnabled();

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_VIEW)) {
			if (UiUtil.enabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
				navigator.addView(AreasView.VIEW_NAME, AreasView.class);
			}
			if (FacadeProvider.getFeatureConfigurationFacade().isCountryEnabled()) {
				navigator.addView(ContinentsView.VIEW_NAME, ContinentsView.class);
				navigator.addView(SubcontinentsView.VIEW_NAME, SubcontinentsView.class);
				navigator.addView(CountriesView.VIEW_NAME, CountriesView.class);
			}
			navigator.addView(RegionsView.VIEW_NAME, RegionsView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : RegionsView.class;
			navigator.addView(DistrictsView.VIEW_NAME, DistrictsView.class);
			navigator.addView(CommunitiesView.VIEW_NAME, CommunitiesView.class);
			if (isAnySurveillanceEnabled) {
				navigator.addView(FacilitiesView.VIEW_NAME, FacilitiesView.class);
			}
			if (isCaseSurveillanceEnabled) {
				navigator.addView(PointsOfEntryView.VIEW_NAME, PointsOfEntryView.class);
			}

			if (UiUtil.permitted(UserRight.POPULATION_MANAGE)) {
				navigator.addView(PopulationDataView.VIEW_NAME, PopulationDataView.class);
			}
		}

		//		if (LoginHelper.hasUserRight(UserRight.USER_RIGHTS_MANAGE)) {
		//			navigator.addView(UserRightsView.VIEW_NAME, UserRightsView.class);
		//		}

		if (isCaseSurveillanceEnabled && UiUtil.permitted(UserRight.LINE_LISTING_CONFIGURE)) {
			navigator.addView(LineListingConfigurationView.VIEW_NAME, LineListingConfigurationView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : LineListingConfigurationView.class;
		}

		if (isAnySurveillanceEnabled && UiUtil.permitted(UserRight.DOCUMENT_TEMPLATE_MANAGEMENT)) {
			navigator.addView(DocumentTemplatesView.VIEW_NAME, DocumentTemplatesView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : DocumentTemplatesView.class;
		}

		if (UiUtil.permitted(FeatureType.EXTERNAL_EMAILS, UserRight.EMAIL_TEMPLATE_MANAGEMENT)) {
			navigator.addView(EmailTemplatesView.VIEW_NAME, EmailTemplatesView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : EmailTemplatesView.class;
		}

		if (UiUtil.permitted(UserRight.CUSTOMIZABLE_ENUM_MANAGEMENT)) {
			navigator.addView(CustomizableEnumValuesView.VIEW_NAME, CustomizableEnumValuesView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : CustomizableEnumValuesView.class;
		}

		if (FacadeProvider.getConfigFacade().isDevMode() && UiUtil.permitted(UserRight.DEV_MODE)) {
			navigator.addView(DevModeView.VIEW_NAME, DevModeView.class);
			firstAccessibleView = firstAccessibleView != null ? firstAccessibleView : DevModeView.class;
		}

		return firstAccessibleView;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		menu.removeAllViews();

		if (UiUtil.permitted(FeatureType.OUTBREAKS, UserRight.OUTBREAK_VIEW)) {
			menu.addView(
				OutbreaksView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", OutbreaksView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				params);
		}

		boolean isCaseSurveillanceEnabled = UiUtil.enabled(FeatureType.CASE_SURVEILANCE);
		boolean isAnySurveillanceEnabled = FacadeProvider.getFeatureConfigurationFacade().isAnySurveillanceEnabled();

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_VIEW)) {
			if (FacadeProvider.getFeatureConfigurationFacade().isCountryEnabled()) {
				menu.addView(
					ContinentsView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", ContinentsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
				menu.addView(
					SubcontinentsView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", SubcontinentsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
				menu.addView(
					CountriesView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", CountriesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}
			if (UiUtil.enabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
				menu.addView(
					AreasView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", AreasView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}
			menu.addView(
				RegionsView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", RegionsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
			menu.addView(
				DistrictsView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", DistrictsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
			menu.addView(
				CommunitiesView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", CommunitiesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
			if (isAnySurveillanceEnabled) {
				menu.addView(
					FacilitiesView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", FacilitiesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}
			if (isCaseSurveillanceEnabled) {
				menu.addView(
					PointsOfEntryView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", PointsOfEntryView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}

			if (UiUtil.permitted(UserRight.POPULATION_MANAGE)) {
				menu.addView(
					PopulationDataView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", PopulationDataView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}
		}

		//		if (LoginHelper.hasUserRight(UserRight.USER_RIGHTS_MANAGE)) {
		//			menu.addView(UserRightsView.VIEW_NAME, I18nProperties.getPrefixFragment("View",
		//					UserRightsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""), params);
		//		}

		if (isCaseSurveillanceEnabled && UiUtil.permitted(UserRight.LINE_LISTING_CONFIGURE)) {
			RegionReferenceDto region = UiUtil.getUser().getRegion();
			menu.addView(
				LineListingConfigurationView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", LineListingConfigurationView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				region != null ? region.getUuid() : null,
				false);
		}
		if (isAnySurveillanceEnabled && UiUtil.permitted(UserRight.DOCUMENT_TEMPLATE_MANAGEMENT)) {
			menu.addView(
				DocumentTemplatesView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", DocumentTemplatesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}

		if (UiUtil.permitted(FeatureType.EXTERNAL_EMAILS, UserRight.EMAIL_TEMPLATE_MANAGEMENT)) {
			menu.addView(
				EmailTemplatesView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", EmailTemplatesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}

		if (UiUtil.permitted(UserRight.CUSTOMIZABLE_ENUM_MANAGEMENT)) {
			menu.addView(
				CustomizableEnumValuesView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", CustomizableEnumValuesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}

		if (FacadeProvider.getConfigFacade().isDevMode() && UiUtil.permitted(UserRight.DEV_MODE)) {
			menu.addView(
				DevModeView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", DevModeView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}
	}

	protected ComboBox addCountryFilter(Layout layout, Consumer<CountryReferenceDto> changeHandler, ComboBox regionFilter) {
		ComboBox countryFilter = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isCountryEnabled()) {
			countryFilter = new CountryCombo((country, isServerCountry) -> {
				changeHandler.accept(country);

				if (regionFilter != null) {
					if (isServerCountry) {
						FieldHelper.updateItems(regionFilter, FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
					} else {
						FieldHelper.updateItems(regionFilter, FacadeProvider.getRegionFacade().getAllActiveByCountry(country.getUuid()));
					}
				}
			});
			layout.addComponent(countryFilter);
		}
		return countryFilter != null ? countryFilter : ComboBoxHelper.createComboBoxV7();
	}
}
