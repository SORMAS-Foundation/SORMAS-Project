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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.configuration.docgeneration.DocumentTemplatesView;
import de.symeda.sormas.ui.configuration.infrastructure.AreasView;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.CountriesView;
import de.symeda.sormas.ui.configuration.infrastructure.DistrictsView;
import de.symeda.sormas.ui.configuration.infrastructure.FacilitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.PointsOfEntryView;
import de.symeda.sormas.ui.configuration.infrastructure.PopulationDataView;
import de.symeda.sormas.ui.configuration.infrastructure.RegionsView;
import de.symeda.sormas.ui.configuration.linelisting.LineListingConfigurationView;
import de.symeda.sormas.ui.configuration.outbreak.OutbreaksView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.DirtyStateComponent;

import javax.validation.constraints.NotNull;

public abstract class AbstractConfigurationView extends AbstractSubNavigationView<DirtyStateComponent> {

	private static final long serialVersionUID = 3193505016439327054L;

	public static final String ROOT_VIEW_NAME = "configuration";

	protected AbstractConfigurationView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		menu.removeAllViews();

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.OUTBREAKS)) {
			menu.addView(
				OutbreaksView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", OutbreaksView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				params);
		}

		boolean isCaseSurveillanceEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);
		boolean isAnySurveillanceEnabled = isCaseSurveillanceEnabled
			|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AGGREGATE_REPORTING);

		SormasUI ui = (SormasUI)getUI();
		if (ui.getUserProvider().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
			if (isAnySurveillanceEnabled) {
				menu.addView(
					CountriesView.VIEW_NAME,
					I18nProperties.getPrefixCaption("View", CountriesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
					null,
					false);
			}
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
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

			if (ui.getUserProvider().hasUserRight(UserRight.POPULATION_MANAGE)) {
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

		if (isCaseSurveillanceEnabled && ui.getUserProvider().hasUserRight(UserRight.LINE_LISTING_CONFIGURE)) {
			RegionReferenceDto region = ui.getUserProvider().getUser().getRegion();
			menu.addView(
				LineListingConfigurationView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", LineListingConfigurationView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				region != null ? region.getUuid() : null,
				false);
		}
		if (isAnySurveillanceEnabled && ui.getUserProvider().hasUserRight(UserRight.DOCUMENT_TEMPLATE_MANAGEMENT)) {
			menu.addView(
				DocumentTemplatesView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", DocumentTemplatesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}
		if (FacadeProvider.getConfigFacade().isDevMode() && ui.getUserProvider().hasUserRole(UserRole.ADMIN)) {
			menu.addView(
				DevModeView.VIEW_NAME,
				I18nProperties.getPrefixCaption("View", DevModeView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				null,
				false);
		}
	}

	public static void registerViews(@NotNull SormasUI ui, Navigator navigator) {
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.OUTBREAKS)) {
			navigator.addView(OutbreaksView.VIEW_NAME, OutbreaksView.class);
		}

		boolean isCaseSurveillanceEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);
		boolean isAnySurveillanceEnabled = isCaseSurveillanceEnabled
			|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AGGREGATE_REPORTING);

		if (ui.getUserProvider().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
				navigator.addView(AreasView.VIEW_NAME, AreasView.class);
			}
			if (isAnySurveillanceEnabled) {
				navigator.addView(CountriesView.VIEW_NAME, CountriesView.class);
			}
			navigator.addView(RegionsView.VIEW_NAME, RegionsView.class);
			navigator.addView(DistrictsView.VIEW_NAME, DistrictsView.class);
			navigator.addView(CommunitiesView.VIEW_NAME, CommunitiesView.class);
			if (isAnySurveillanceEnabled) {
				navigator.addView(FacilitiesView.VIEW_NAME, FacilitiesView.class);
			}
			if (isCaseSurveillanceEnabled) {
				navigator.addView(PointsOfEntryView.VIEW_NAME, PointsOfEntryView.class);
			}

			if (ui.getUserProvider().hasUserRight(UserRight.POPULATION_MANAGE)) {
				navigator.addView(PopulationDataView.VIEW_NAME, PopulationDataView.class);
			}
		}

		//		if (LoginHelper.hasUserRight(UserRight.USER_RIGHTS_MANAGE)) {
		//			navigator.addView(UserRightsView.VIEW_NAME, UserRightsView.class);
		//		}

		if (isCaseSurveillanceEnabled && ui.getUserProvider().hasUserRight(UserRight.LINE_LISTING_CONFIGURE)) {
			navigator.addView(LineListingConfigurationView.VIEW_NAME, LineListingConfigurationView.class);
		}

		if (isAnySurveillanceEnabled && ui.getUserProvider().hasUserRight(UserRight.DOCUMENT_TEMPLATE_MANAGEMENT)) {
			navigator.addView(DocumentTemplatesView.VIEW_NAME, DocumentTemplatesView.class);
		}

		if (FacadeProvider.getConfigFacade().isDevMode() && ui.getUserProvider().hasUserRole(UserRole.ADMIN)) {
			navigator.addView(DevModeView.VIEW_NAME, DevModeView.class);
		}
	}

}
