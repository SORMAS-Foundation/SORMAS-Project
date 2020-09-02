/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.statistics;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class SvgCircleElement extends Label {

	private final boolean showPercentage;
	private int percentage;

	public SvgCircleElement(boolean showPercentage) {
		setContentMode(ContentMode.HTML);
		this.showPercentage = showPercentage;
		updateSvg();
	}

	public void updateSvg(int percentage, SvgCircleElementPart... svgCircleElementParts) {
		this.percentage = percentage;
		updateSvg(svgCircleElementParts);
	}

	public void updateSvg(SvgCircleElementPart... svgCircleElementParts) {
		StringBuilder sb = new StringBuilder();
		if (svgCircleElementParts.length > 0) {
			sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewbox=\"0 0 36 36\">");
			int percentageSoFar = 0;
			for (SvgCircleElementPart circlePart : svgCircleElementParts) {
				//@formatter:off
				sb.append("<path class=\"svg-circle " + circlePart.strokeClass + "\" "
						+ "stroke-dasharray=\"" + circlePart.percentage + ", 100\" "
						+ "stroke-dashoffset=\"-" + percentageSoFar + "\" "
						+ "d=\"M18 2.0845 "
						+ "a 15.9155 15.9155 0 0 1 0 31.831 "
						+ "a 15.9155 15.9155 0 0 1 0 -31.831\"/>");
				//@formatter:on
				percentageSoFar += circlePart.percentage;
			}
			if (showPercentage) {
				//@formatter:off
				sb.append("<text x=\"18\" y=\"20.35\" fill=\"#005A9C\" "
						+ "style=\"font-size: 0.75em; font-weight: bold; text-anchor: middle;\">" 
						+ percentage + "%</text>");
				//@formatter:on
			}
			sb.append("</svg>");
		}

		setValue(sb.toString());
	}

	public class SvgCircleElementPart {

		private String strokeClass;
		private int percentage;

		public SvgCircleElementPart(String strokeClass, int percentage) {
			this.strokeClass = strokeClass;
			this.percentage = percentage;
		}
	}
}
