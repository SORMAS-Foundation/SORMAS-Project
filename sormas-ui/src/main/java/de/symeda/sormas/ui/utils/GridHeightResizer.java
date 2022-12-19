/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Grid;

public class GridHeightResizer implements DataSizeChangeListener {

	private static final long serialVersionUID = 1951617224990265972L;

	public static final int DEFAULT_MAX_HEIGHT = 5;

	private final int maxHeight;

	public GridHeightResizer() {
		this(DEFAULT_MAX_HEIGHT);
	}

	public GridHeightResizer(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	@Override
	public void dataSizeChange(DataSizeChangeEvent event) {

		Grid<?> grid = (Grid<?>) event.getSource();
		if (event.getSize() > 0) {
			grid.setHeightByRows(Math.min(event.getSize(), maxHeight));
		} else {
			grid.setHeightByRows(1);
		}
	}
}
