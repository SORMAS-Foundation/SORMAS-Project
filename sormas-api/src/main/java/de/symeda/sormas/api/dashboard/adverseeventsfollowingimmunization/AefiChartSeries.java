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

package de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AefiChartSeries implements Serializable {

	private static final long serialVersionUID = 8537929721515000783L;

	private Object name;
	private String color;
	private List<String> seriesData = new ArrayList<>();

	public AefiChartSeries(Object name) {
		this.name = name;
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<String> getSeriesData() {
		return seriesData;
	}

	public void setSeriesData(List<String> seriesData) {
		this.seriesData = seriesData;
	}

	public void addData(String data) {
		seriesData.add(data);
	}
}
