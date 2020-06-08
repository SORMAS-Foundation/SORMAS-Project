/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component.visualization.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryPieData {

	private String title;
	private List<SummaryPieEntry> entries;
	private List<Integer> colors;
	private List<BaseLegendEntry> legendEntries;

	public SummaryPieData(String title) {
		this.title = title;
		this.entries = new ArrayList<SummaryPieEntry>();
		this.colors = new ArrayList<Integer>();
	}

	public SummaryPieData(String title, List<SummaryPieEntry> entries, List<Integer> colors) {
		this.title = title;
		this.entries = entries;
		this.colors = colors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SummaryPieEntry> getEntries() {
		return entries;
	}

	public List<BaseLegendEntry> getLegendEntries() {
		return legendEntries;
	}

	public void setEntries(List<SummaryPieEntry> entries) {
		this.entries = entries;
	}

	public List<Integer> getColors() {
		return colors;
	}

	public void setColors(List<Integer> colors) {
		this.colors = colors;
	}

	public void addEntry(SummaryPieEntry entry) {
		if (this.entries == null)
			this.entries = new ArrayList<SummaryPieEntry>();

		this.entries.add(entry);
	}

	public void addColor(Integer color) {
		if (this.colors == null)
			this.colors = new ArrayList<Integer>();

		this.colors.add(color);
	}

	public void addLegendEntry(BaseLegendEntry entry) {
		if (this.legendEntries == null)
			this.legendEntries = new ArrayList<>();

		this.legendEntries.add(entry);
	}
}
