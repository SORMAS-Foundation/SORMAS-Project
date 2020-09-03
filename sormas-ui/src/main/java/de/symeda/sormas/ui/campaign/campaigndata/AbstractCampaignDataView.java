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

package de.symeda.sormas.ui.campaign.campaigndata;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;

public abstract class AbstractCampaignDataView extends AbstractDetailView<CampaignFormDataReferenceDto> {

	private static final long serialVersionUID = 4919695277077799182L;

	public static final String ROOT_VIEW_NAME = CampaignDataView.VIEW_NAME;

	protected AbstractCampaignDataView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(CampaignDataView.VIEW_NAME, I18nProperties.getCaption(Captions.campaignCampaignData));
		menu.addView(CampaignFormDataView.VIEW_NAME, I18nProperties.getCaption(Captions.campaignCampaignDataForm), params);
	}

	@Override
	protected CampaignFormDataReferenceDto getReferenceByUuid(String uuid) {
		final CampaignFormDataReferenceDto reference;
		if (FacadeProvider.getCampaignFormDataFacade().exists(uuid)) {
			reference = FacadeProvider.getCampaignFormDataFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

}
