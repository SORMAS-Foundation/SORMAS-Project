/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import java.util.HashMap;
import java.util.Set;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.RelevanceStatusFilter;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class EnvironmentGridComponent extends VerticalLayout {

	private static final long serialVersionUID = 8767664326530203161L;
	private final EnvironmentFilterForm filterForm;
	private final EnvironmentGrid grid;

	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public EnvironmentGridComponent(
		EnvironmentCriteria criteria,
		ViewConfiguration viewConfiguration,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {
		setSizeFull();
		setSpacing(false);

		grid = new EnvironmentGrid(criteria, viewConfiguration);
		grid.addDataSizeChangeListener(e -> updateStatusButtons(criteria));

		filterForm = new EnvironmentFilterForm();
		filterForm.addResetHandler(e -> filterResetHandler.run());
		filterForm.addApplyHandler(e -> filterChangeHandler.run());

		HorizontalLayout statusFilterBar = buildStatusFilterBar(criteria, filterChangeHandler);

		addComponents(filterForm, statusFilterBar, grid);
		setExpandRatio(grid, 1);
	}

	public void reload() {
		grid.reload();
	}

	public void updateFilterComponents(EnvironmentCriteria criteria) {
		filterForm.setValue(criteria);
		updateStatusButtons(criteria);
	}

	private HorizontalLayout buildStatusFilterBar(EnvironmentCriteria criteria, Runnable filterChangeHandler) {
		HorizontalLayout statusFilterBar = new HorizontalLayout();
		statusFilterBar.setMargin(false);
		statusFilterBar.setSpacing(true);
		statusFilterBar.setWidthFull();
		statusFilterBar.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		// investigation status filter
		statusButtons = new HashMap<>();

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
			criteria.investigationStatus(null);
			filterChangeHandler.run();
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		buttonFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (InvestigationStatus status : InvestigationStatus.values()) {
			Button statusButton = ButtonHelper.createButton(status.toString(), e -> {
				criteria.setInvestigationStatus(status);
				filterChangeHandler.run();
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			statusButton.setData(status);
			statusButton.setCaptionAsHtml(true);

			buttonFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}

		statusFilterBar.addComponent(buttonFilterLayout);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.addComponent(
			RelevanceStatusFilter.createRelevanceStatusFilter(
				Captions.environmentActiveEnvironments,
				Captions.environmentArchivedEnvironments,
				Captions.environmentAllActiveAndArchivedEnvironments,
				Captions.environmentDeletedEnvironments,
				criteria.getRelevanceStatus(),
				UserRight.ENVIRONMENT_DELETE,
				relevanceStatus -> {
					if (grid.getColumn(grid.DELETE_REASON_COLUMN) != null) {
						grid.getColumn(grid.DELETE_REASON_COLUMN).setHidden(!relevanceStatus.equals(EntityRelevanceStatus.DELETED));
					}
					criteria.setRelevanceStatus(relevanceStatus);
					filterChangeHandler.run();
				}));

		statusFilterBar.addComponent(actionButtonsLayout);
		statusFilterBar.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterBar.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterBar;
	}

	private void updateStatusButtons(EnvironmentCriteria criteria) {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getInvestigationStatus()) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
		}
	}

	public EnvironmentGrid getGrid() {
		return grid;
	}

	public Set<EnvironmentIndexDto> getSelectedItems() {
		return grid.asMultiSelect().getSelectedItems();
	}
}
