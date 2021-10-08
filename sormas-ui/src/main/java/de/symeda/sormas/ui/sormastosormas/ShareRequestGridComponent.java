/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ShareRequestGridComponent extends VerticalLayout {

	private static final long serialVersionUID = 99959936905302170L;
	private ShareRequestCriteria criteria;
	private ShareRequestFilterForm filterForm;
	private ShareRequestGrid grid;
	private Map<Button, String> statusButtons;
	private Button activeStatusButton;

	public ShareRequestGridComponent(
		ViewConfiguration viewConfiguration,
		ShareRequestCriteria criteria,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {
		this.criteria = criteria;

		setSizeFull();
		setMargin(false);

		grid = new ShareRequestGrid(viewConfiguration.isInEagerMode(), criteria);
		grid.setSizeFull();
		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

		VerticalLayout gridLayout = new VerticalLayout();
		// Filters to be added later
		/* gridLayout.addComponent( */createFilterBar(filterChangeHandler, filterResetHandler)/* ) */;
		gridLayout.addComponent(createStatusFilterBar(filterChangeHandler));

		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
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

	public HorizontalLayout createFilterBar(Runnable filterChangeHandler, Runnable filterResetHandler) {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		filterForm = new ShareRequestFilterForm();
		filterForm.addResetHandler((e) -> filterResetHandler.run());
		filterForm.addApplyHandler(e -> filterChangeHandler.run());

		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	public void updateFilterComponents() {
		filterForm.setValue(criteria);
	}

	public HorizontalLayout createStatusFilterBar(Runnable filterChangeHandler) {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		activeStatusButton = createAndAddStatusButton(null, statusFilterLayout, filterChangeHandler);

		createAndAddStatusButton(ShareRequestStatus.PENDING, statusFilterLayout, filterChangeHandler);
		createAndAddStatusButton(ShareRequestStatus.ACCEPTED, statusFilterLayout, filterChangeHandler);

		return statusFilterLayout;
	}

	private Button createAndAddStatusButton(@Nullable ShareRequestStatus status, HorizontalLayout buttonLayout, Runnable filterChangeHandler) {
		Button button = ButtonHelper.createButton(status == null ? I18nProperties.getCaption(Captions.all) : status.toString(), e -> {
			criteria.setStatus(status);

			filterChangeHandler.run();
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		if (status != null) {
			button.setData(status);
		}

		button.setCaptionAsHtml(true);

		buttonLayout.addComponent(button);
		statusButtons.put(button, button.getCaption());

		return button;
	}

	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getStatus()) {
				activeStatusButton = b;
			}
		});
		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}
}
