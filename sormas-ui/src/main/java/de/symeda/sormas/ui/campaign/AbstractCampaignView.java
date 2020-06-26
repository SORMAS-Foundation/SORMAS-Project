/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.campaign;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignDataView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

import java.util.Optional;

@SuppressWarnings("serial")
public abstract class AbstractCampaignView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = "campaign";

	protected AbstractCampaignView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		menu.removeAllViews();

		menu.addView(
			CampaignsView.VIEW_NAME,
			I18nProperties.getPrefixCaption("View", CampaignsView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
			params);
		menu.addView(
			CampaignDataView.VIEW_NAME,
			I18nProperties.getPrefixCaption("View", CampaignDataView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
			null,
			false);
	}

	public static void registerViews(Navigator navigator) {
		navigator.addView(CampaignsView.VIEW_NAME, CampaignsView.class);
		navigator.addView(CampaignDataView.VIEW_NAME, CampaignDataView.class);
	}

	@Override
	protected Optional<VerticalLayout> createInfoLayout() {
		return Optional.empty();
	}

}
