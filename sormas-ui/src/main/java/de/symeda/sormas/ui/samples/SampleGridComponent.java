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

package de.symeda.sormas.ui.samples;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.utils.ReloadableGrid;

public abstract class SampleGridComponent<T, C extends BaseCriteria> extends VerticalLayout {

	private static final long serialVersionUID = 2805771019861077651L;

	public abstract ReloadableGrid<T, C> getGrid();

	public abstract MenuBar getBulkOperationsDropdown();

	public abstract C getCriteria();

	public void reload(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			getCriteria().fromUrlParams(params);
		}
		updateFilterComponents();
		getGrid().reload();
	}

	public abstract void updateFilterComponents();
}
