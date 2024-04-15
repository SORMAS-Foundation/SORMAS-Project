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

package de.symeda.sormas.ui.selfreport;

import java.util.HashMap;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportInvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.RelevanceStatusFilter;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SelfReportGridComponent extends VerticalLayout {

	private static final long serialVersionUID = 4120247053735846454L;

	private final SelfReportFilterForm filterForm;
	private final SelfReportGrid grid;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public SelfReportGridComponent(
		SelfReportCriteria criteria,
		ViewConfiguration viewConfiguration,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {

		filterForm = new SelfReportFilterForm();
		filterForm.addResetHandler(e -> filterResetHandler.run());
		filterForm.addApplyHandler(e -> filterChangeHandler.run());

		HorizontalLayout statusFilterBar = buildStatusFilterBar(criteria, filterChangeHandler);

		grid = new SelfReportGrid(criteria, viewConfiguration);
		grid.addDataSizeChangeListener(e -> updateStatusButtons(criteria));

		addComponents(filterForm, statusFilterBar, grid);
		setExpandRatio(grid, 1);
	}

	public void reload() {
		grid.reload();
	}

	public void updateFilterComponents(SelfReportCriteria criteria) {
		filterForm.setValue(criteria);
		updateStatusButtons(criteria);
	}

	private HorizontalLayout buildStatusFilterBar(SelfReportCriteria criteria, Runnable filterChangeHandler) {
		HorizontalLayout statusFilterBar = new HorizontalLayout();
		statusFilterBar.setMargin(false);
		statusFilterBar.setSpacing(true);
		statusFilterBar.setWidthFull();
		statusFilterBar.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		// investigation status filter
		statusButtons = new HashMap<>();

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
			criteria.setInvestigationStatus(null);
			filterChangeHandler.run();
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		buttonFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (SelfReportInvestigationStatus status : SelfReportInvestigationStatus.values()) {
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
				Captions.selfReportActiveEnvironments,
				Captions.selfReportArchivedEnvironments,
				Captions.selfReportAllActiveAndArchivedEnvironments,
				Captions.selfReportDeletedEnvironments,
				criteria.getRelevanceStatus(),
				UserRight.ENVIRONMENT_DELETE,
				relevanceStatus -> {
					criteria.setRelevanceStatus(relevanceStatus);
					filterChangeHandler.run();
				}));

		statusFilterBar.addComponent(actionButtonsLayout);
		statusFilterBar.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterBar.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterBar;
	}

	private void updateStatusButtons(SelfReportCriteria criteria) {
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
}
