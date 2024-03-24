/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.Collections;
import java.util.Objects;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationCriteria;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory.AefiInvestigationDataLayout;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory.AefiInvestigationFilterFormLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class AefiInvestigationView extends AbstractAefiView {

	public static final String VIEW_NAME = "adverseeventinvestigations";

	private final AefiInvestigationCriteria criteria;

	private AefiInvestigationFilterFormLayout filterFormLayout;
	private final AefiInvestigationDataLayout dataLayout;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;
	private ViewConfiguration viewConfiguration;

	public AefiInvestigationView() {
		super(VIEW_NAME);

		CssStyles.style(getViewTitleLabel(), CssStyles.PAGE_TITLE);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(AefiInvestigationView.class).get(AefiInvestigationCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.setRelevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		dataLayout = new AefiInvestigationDataLayout(criteria);

		if (UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			exportLayout.setSpacing(true);
			exportLayout.setMargin(true);
			exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			exportLayout.setWidth(200, Unit.PIXELS);

			PopupButton exportButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
			addHeaderComponent(exportButton);

			Button basicExportButton = ButtonHelper.createIconButton(Captions.exportBasic, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			basicExportButton.setDescription(I18nProperties.getString(Strings.infoBasicExport));
			basicExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(basicExportButton);
			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				dataLayout.getGrid(),
				() -> viewConfiguration.isInEagerMode() ? dataLayout.getGrid().asMultiSelect().getSelectedItems() : Collections.emptySet(),
				ExportEntityName.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_INVESTIGATION);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);
		}

		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(createFilterBar());

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.setMargin(false);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		CssStyles.style(gridLayout, CssStyles.VIEW_SECTION, CssStyles.VSPACE_TOP_3);

		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(dataLayout);
		gridLayout.setExpandRatio(dataLayout, 1);

		mainLayout.addComponent(gridLayout);

		mainLayout.setMargin(new MarginInfo(false, true, true, true));
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(gridLayout, 1);
		mainLayout.addStyleNames("crud-main-layout", CssStyles.VSPACE_TOP_4);

		addComponent(mainLayout);
	}

	private void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterFormLayout.setValue(criteria);

		applyingCriteria = false;
	}

	private AefiInvestigationFilterFormLayout createFilterBar() {
		filterFormLayout = new AefiInvestigationFilterFormLayout();

		filterFormLayout.addResetHandler(clickEvent -> {
			ViewModelProviders.of(AefiInvestigationView.class).remove(AefiInvestigationCriteria.class);
			navigateTo(null, true);
		});

		filterFormLayout.addApplyHandler(clickEvent -> {
			dataLayout.refreshGrid();
		});

		return filterFormLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		if (Objects.nonNull(UserProvider.getCurrent())
			&& UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW)) {

			if (FacadeProvider.getFeatureConfigurationFacade()
				.isFeatureEnabled(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION)) {

				int daysAfterAefiEntryGetsArchived = FacadeProvider.getFeatureConfigurationFacade()
					.getProperty(
						FeatureType.AUTOMATIC_ARCHIVING,
						DeletableEntityType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION,
						FeatureTypeProperty.THRESHOLD_IN_DAYS,
						Integer.class);
				if (daysAfterAefiEntryGetsArchived > 0) {
					relevanceStatusInfoLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoArchivedAefiEntries), daysAfterAefiEntryGetsArchived),
						ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
			}
			relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
			relevanceStatusFilter.setId("relevanceStatus");
			relevanceStatusFilter.setWidth(260, Unit.PIXELS);
			relevanceStatusFilter.setNullSelectionAllowed(false);
			relevanceStatusFilter.setTextInputAllowed(false);
			relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.aefiActiveInvestigations));
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.aefiArchivedInvestigations));
			relevanceStatusFilter.setItemCaption(
				EntityRelevanceStatus.ACTIVE_AND_ARCHIVED,
				I18nProperties.getCaption(Captions.aefiAllActiveAndArchivedInvestigations));
			relevanceStatusFilter.setCaption(null);
			relevanceStatusFilter.addStyleName(CssStyles.VSPACE_NONE);

			if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_DELETE)) {
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.aefiDeletedInvestigations));
			} else {
				relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
			}

			relevanceStatusFilter.addValueChangeListener(e -> {
				if (relevanceStatusInfoLabel != null) {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
				}
				criteria.setRelevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(relevanceStatusFilter);
		}

		if (actionButtonsLayout.getComponentCount() > 0) {
			statusFilterLayout.addComponent(actionButtonsLayout);
			statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);
		}

		return statusFilterLayout;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();

		super.enter(event);
	}
}
