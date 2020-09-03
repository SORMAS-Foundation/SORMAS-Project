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

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryCircularData {

	private String title;
	private double value;
	private double percentage;
	private int finishedColor;
	private int unfinishedColor;

	public SummaryCircularData(String title, double value, double percentage, int finishedColor, int unfinishedColor) {
		this.title = title;
		this.value = value;
		this.percentage = percentage;
		this.finishedColor = finishedColor;
		this.unfinishedColor = unfinishedColor;
	}

	public SummaryCircularData(String title, double value, double percentage) {
		this.title = title;
		this.value = value;
		this.percentage = percentage;
		this.finishedColor = R.color.circularProgressFinishedDefault;
		this.unfinishedColor = R.color.circularProgressUnfinishedDefault;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public int getFinishedColor() {
		return finishedColor;
	}

	public void setFinishedColor(int finishedColor) {
		this.finishedColor = finishedColor;
	}

	public int getUnfinishedColor() {
		return unfinishedColor;
	}

	public void setUnfinishedColor(int unfinishedColor) {
		this.unfinishedColor = unfinishedColor;
	}
}
