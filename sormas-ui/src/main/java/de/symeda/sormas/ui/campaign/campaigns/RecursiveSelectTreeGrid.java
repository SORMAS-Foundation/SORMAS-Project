package de.symeda.sormas.ui.campaign.campaigns;

import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.GridMultiSelect;
import com.vaadin.ui.components.grid.GridSelectionModel;

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

public class RecursiveSelectTreeGrid<T> extends TreeGrid<T> {

    @Override
    public GridSelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
        if (SelectionMode.MULTI == selectionMode) {
        	
        	@SuppressWarnings("unchecked")
			GridSelectionModel<T> model = (GridSelectionModel<T>) new GridMultiSelect<T>(this) { // AbstractGridMultiSelectionModel {
        		
			};
        	
        	
        	
        }
		return null;
    }
}