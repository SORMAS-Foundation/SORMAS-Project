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

package de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AefiChartData implements Serializable {

	private static final long serialVersionUID = 3538219674050390425L;

	private List<Object> xAxisCategories = new ArrayList<>();
	private List<AefiChartSeries> series = new ArrayList<>();

	public AefiChartData() {
	}

	public List<Object> getxAxisCategories() {
		return xAxisCategories;
	}

	public void setxAxisCategories(List<Object> xAxisCategories) {
		this.xAxisCategories = xAxisCategories;
	}

	public List<AefiChartSeries> getSeries() {
		return series;
	}

	public void setSeries(List<AefiChartSeries> series) {
		this.series = series;
	}

	public void addXAxisCategory(Object category) {
		xAxisCategories.add(category);
	}

	public void addSeries(AefiChartSeries chartSeries) {
		series.add(chartSeries);
	}
}
