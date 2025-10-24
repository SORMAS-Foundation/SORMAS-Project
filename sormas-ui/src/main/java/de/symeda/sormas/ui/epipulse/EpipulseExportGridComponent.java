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

package de.symeda.sormas.ui.epipulse;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EpipulseExportGridComponent extends VerticalLayout {

	private EpipulseExportCriteria criteria;

	private EpipulseExportGrid grid;
	private EpipulseExportView epipulseExportView;

	// Filter
	private EpipulseExportGridFilterForm filterForm;
	private ComboBox relevanceStatusFilter;

	private Label viewTitleLabel;
	private String originalViewTitle;

	public EpipulseExportGridComponent(Label viewTitleLabel, EpipulseExportView epipulseExportView) {
		setSizeFull();
		setMargin(false);

		this.viewTitleLabel = viewTitleLabel;
		this.epipulseExportView = epipulseExportView;
		originalViewTitle = viewTitleLabel.getValue();

		criteria = ViewModelProviders.of(EpipulseExportView.class).get(EpipulseExportCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new EpipulseExportGrid(criteria);
		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		filterForm = new EpipulseExportGridFilterForm();
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(EpipulseExportView.class).remove(EpipulseExportCriteria.class);
			epipulseExportView.navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> grid.reload());

		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_NONE);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UiUtil.permitted(UserRight.EPIPULSE_EXPORT_VIEW)) {
				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
				relevanceStatusFilter.setId("relevanceStatusFilter");
				relevanceStatusFilter.setWidth(220, Unit.PIXELS);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems(EntityRelevanceStatus.getAllExceptDeleted());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.epipulseActiveExports));

				if (UiUtil.permitted(UserRight.TASK_VIEW_ARCHIVED)) {
					relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.epipulseArchivedExports));
					relevanceStatusFilter
						.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.epipulseAllExports));
				} else {
					relevanceStatusFilter.removeItem(EntityRelevanceStatus.ARCHIVED);
					relevanceStatusFilter.removeItem(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED);
				}

				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					epipulseExportView.navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setMargin(false);
		gridLayout.setSizeFull();
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.addStyleNames("crud-main-layout");
		gridLayout.setMargin(new MarginInfo(false, true, true, true));
		gridLayout.addStyleName(CssStyles.VSPACE_TOP_4);
	}

	public void reload(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		epipulseExportView.setApplyingCriteria(true);

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		epipulseExportView.setApplyingCriteria(false);
	}

	public EpipulseExportGrid getGrid() {
		return grid;
	}

	public EpipulseExportCriteria getCriteria() {
		return criteria;
	}
}
