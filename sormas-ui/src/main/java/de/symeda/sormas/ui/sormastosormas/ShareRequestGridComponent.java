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

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;

public class ShareRequestGridComponent extends VerticalLayout {

	private static final long serialVersionUID = 99959936905302170L;
	private ShareRequestCriteria criteria;
	private ShareRequestFilterForm filterForm;
	private ShareRequestGrid grid;

	public ShareRequestGridComponent(
		ShareRequestsViewConfiguration viewConfiguration,
		ShareRequestCriteria criteria,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {
		this.criteria = criteria;

		setSizeFull();
		setMargin(false);

		grid = new ShareRequestGrid(viewConfiguration.isInEagerMode(), criteria, viewConfiguration.getViewType());
		grid.setSizeFull();

		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar(filterChangeHandler, filterResetHandler));

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
}
