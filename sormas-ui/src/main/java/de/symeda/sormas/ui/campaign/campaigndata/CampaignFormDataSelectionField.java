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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignFormDataSelectionField extends VerticalLayout {

	private static final long serialVersionUID = 1771273989939992148L;

	private final CampaignFormDataDto newData;
	private final CampaignFormDataDto existingData;
	private final Runnable cancelCallback;
	private final Runnable skipCallback;
	private final Runnable overwriteCallback;
	private final String infoText;

	public CampaignFormDataSelectionField(
		CampaignFormDataDto newData,
		CampaignFormDataDto existingData,
		String infoText,
		Runnable cancelCallback,
		Runnable skipCallback,
		Runnable overwriteCallback) {
		this.newData = newData;
		this.existingData = existingData;
		this.infoText = infoText;
		this.cancelCallback = cancelCallback;
		this.skipCallback = skipCallback;
		this.overwriteCallback = overwriteCallback;

		initialize();
	}

	private void initialize() {

		setSpacing(true);
		setMargin(true);
		setHeightUndefined();
		setWidthFull();

		addInfoComponent();
		addFormDataDetailsComponent();
		addButtons();
	}

	private void addInfoComponent() {
		addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private void addFormDataDetailsComponent() {

		VerticalLayout formDataDetailsLayout = new VerticalLayout();
		formDataDetailsLayout.setMargin(false);

		Label newFormDataHeading = new Label(I18nProperties.getString(Strings.headingCampaignFormDataDuplicateNew));
		CssStyles.style(newFormDataHeading, CssStyles.H3);
		formDataDetailsLayout.addComponent(newFormDataHeading);
		formDataDetailsLayout.addComponent(buildFormDataDetailsComponent(newData, false));

		Label existingFormDataHeading = new Label(I18nProperties.getString(Strings.headingCampaignFormDataDuplicateExisting));
		CssStyles.style(existingFormDataHeading, CssStyles.H3);
		formDataDetailsLayout.addComponent(existingFormDataHeading);
		formDataDetailsLayout.addComponent(buildFormDataDetailsComponent(existingData, true));

		addComponent(formDataDetailsLayout);
	}

	private void addButtons() {

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidthUndefined();

		Button btnCancel = new Button(I18nProperties.getCaption(Captions.importCancelImport));
		btnCancel.addClickListener(e -> cancelCallback.run());
		buttonLayout.addComponent(btnCancel);

		Button btnSkip = new Button(I18nProperties.getCaption(Captions.actionSkip));
		btnSkip.addClickListener(e -> skipCallback.run());
		buttonLayout.addComponent(btnSkip);

		Button btnOverwrite = new Button(I18nProperties.getCaption(Captions.actionOverwrite));
		CssStyles.style(btnOverwrite, ValoTheme.BUTTON_PRIMARY);
		btnOverwrite.addClickListener(e -> overwriteCallback.run());
		buttonLayout.addComponent(btnOverwrite);

		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	private HorizontalLayout buildFormDataDetailsComponent(CampaignFormDataDto formData, boolean existingData) {
		HorizontalLayout formDataLayout = new HorizontalLayout();
		formDataLayout.setSpacing(true);
		{
			Label fdRegion = new Label(formData.getRegion() != null ? formData.getRegion().toString() : "");
			fdRegion.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.REGION));
			fdRegion.setWidthUndefined();
			formDataLayout.addComponent(fdRegion);

			Label fdDistrict = new Label(formData.getDistrict() != null ? formData.getDistrict().toString() : "");
			fdDistrict.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.DISTRICT));
			fdDistrict.setWidthUndefined();
			formDataLayout.addComponent(fdDistrict);

			Label fdCommunity = new Label(formData.getCommunity() != null ? formData.getCommunity().toString() : "");
			fdCommunity.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.COMMUNITY));
			fdCommunity.setWidthUndefined();
			formDataLayout.addComponent(fdCommunity);

			Label fdFormDate = new Label(DateFormatHelper.formatDate(formData.getFormDate()));
			fdFormDate.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.FORM_DATE));
			fdFormDate.setWidthUndefined();
			formDataLayout.addComponent(fdFormDate);

			if (existingData) {
				Label fdCreatingUser = new Label(formData.getCreatingUser() != null ? formData.getCreatingUser().toString() : "");
				fdCreatingUser.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.CREATING_USER));
				fdCreatingUser.setWidthUndefined();
				formDataLayout.addComponent(fdCreatingUser);

				Label fdCreationDate = new Label(DateFormatHelper.formatDate(formData.getCreationDate()));
				fdCreationDate.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataDto.I18N_PREFIX, CampaignFormDataDto.CREATION_DATE));
				fdCreationDate.setWidthUndefined();
				formDataLayout.addComponent(fdCreationDate);
			}
		}

		return formDataLayout;
	}

}
