/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory.AefiDataLayout;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory.AefiFilterFormLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiView extends AbstractView {

	public static final String VIEW_NAME = "adverseevents";

	private final AefiCriteria criteria;

	private AefiFilterFormLayout filterFormLayout;
	private final AefiDataLayout dataLayout;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	public AefiView() {
		super(VIEW_NAME);

		CssStyles.style(getViewTitleLabel(), CssStyles.PAGE_TITLE);

		criteria = ViewModelProviders.of(AefiView.class).get(AefiCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.setRelevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		dataLayout = new AefiDataLayout(criteria);

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

		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(gridLayout, 1);
		mainLayout.setStyleName("crud-main-layout");

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

	private AefiFilterFormLayout createFilterBar() {
		filterFormLayout = new AefiFilterFormLayout();

		filterFormLayout.addResetHandler(clickEvent -> {
			ViewModelProviders.of(AefiView.class).remove(AefiCriteria.class);
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
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.aefiActiveAdverseEvents));
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.aefiArchivedAdverseEvents));
			relevanceStatusFilter
				.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.aefiAllActiveAndArchivedAdverseEvents));
			relevanceStatusFilter.setCaption(null);
			relevanceStatusFilter.addStyleName(CssStyles.VSPACE_NONE);

			if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_DELETE)) {
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.aefiDeletedAdverseEvents));
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
	}
}
