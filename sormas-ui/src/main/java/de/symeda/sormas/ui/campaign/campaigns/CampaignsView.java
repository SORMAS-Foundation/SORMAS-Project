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

package de.symeda.sormas.ui.campaign.campaigns;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class CampaignsView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	private CampaignCriteria criteria;
	private VerticalLayout gridLayout;
	private CampaignGrid grid;
	private Button createButton;
	private Button validateFormsButton;

	// Filter
	private TextField searchField;
	private com.vaadin.v7.ui.ComboBox relevanceStatusFilter;

	public CampaignsView() {

		super(VIEW_NAME);

		ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(CampaignsView.class).get(CampaignCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new CampaignGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UiUtil.permitted(UserRight.CAMPAIGN_EDIT)) {
			validateFormsButton = ButtonHelper.createIconButton(Captions.campaignValidateForms, VaadinIcons.CHECK_CIRCLE, e -> {
				FacadeProvider.getCampaignFormMetaFacade().validateAllFormMetas();
				Notification.show(I18nProperties.getString(Strings.messageAllCampaignFormsValid), Type.TRAY_NOTIFICATION);
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(validateFormsButton);

			createButton = ButtonHelper.createIconButton(
				Captions.campaignNewCampaign,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getCampaignController().createOrEditCampaign(null),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	private HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterLayout.setSpacing(true);

		searchField = new TextField();
		searchField.setCaption(I18nProperties.getCaption(Captions.campaignSearch));
		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptCampaignSearch));
		searchField.setImmediate(true);
		searchField.addTextChangeListener(e -> {
			criteria.freeText(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);

		// Show active/archived/all dropdown
		relevanceStatusFilter = new com.vaadin.v7.ui.ComboBox();
		relevanceStatusFilter.setId("relevanceStatus");
		relevanceStatusFilter.setWidth(160, Unit.PIXELS);
		relevanceStatusFilter.setNullSelectionAllowed(false);
		relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.campaignActiveCampaigns));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.campaignArchivedCampaigns));
		relevanceStatusFilter
			.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.campaignAllActiveAndArchivedCampaigns));
		if (UiUtil.permitted(UserRight.CAMPAIGN_DELETE)) {
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.campaignDeletedCampaigns));
		} else {
			relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
		}

		relevanceStatusFilter.addValueChangeListener(e -> {
			criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(relevanceStatusFilter);
		filterLayout.setComponentAlignment(relevanceStatusFilter, Alignment.MIDDLE_RIGHT);
		filterLayout.setExpandRatio(relevanceStatusFilter, 1);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {

		if (event != null) {
			String params = event.getParameters().trim();
			if (params.startsWith("?")) {
				params = params.substring(1);
				criteria.fromUrlParams(params);
			}
			updateFilterComponents();
		}
		grid.reload();

		super.enter(event);
	}

	private void updateFilterComponents() {

		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getFreeText());

		applyingCriteria = false;
	}
}
