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
package de.symeda.sormas.ui.highcharts;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * <p>
 * Abstract Highcharts chart.
 * </p>
 * <p>
 * Download jquery and highcharts.js (if not already loaded in your webapp) and save them in the resource directory org/vaadin/highcharts.
 * Create a new class in the package org.vaadin.highcharts (e.g. "HighChart") and inherit it from AbstractHighChart.
 * Then add a proper JavaScript annotation to the newly created class in order to load all necessary JavaScript
 * libraries that you need (e.g. jquery.js, highcharts.js, highcharts-more.js, ...).
 * Make sure your project complies with the licenses of those libraries.
 * At the end of this list add "highcharts-connector.js".
 * </p>
 * <p>
 * Example of how to extend <code>AbstractHighChart</code>:
 * </p>
 * 
 * <pre>
 * package org.vaadin.highcharts;
 *
 *{@literal @}JavaScript({"jquery-min.js", "highcharts.js", "highcharts-connector.js"})
 * public class HighChart extends AbstractHighChart {
 * 	private static final long serialVersionUID = -7326315426217377753L;
 * }
 * </pre>
 *
 * @author Stefan Endrullis
 * 
 *         Based on https://github.com/xylo/highcharts-vaadin7 (Apache 2.0 license)
 */
@JavaScript({
	"jquery-min.js",
	"highcharts.js",
	"highcharts-connector.js",
	"highcharts-exporting.js" })
public class HighChart extends AbstractJavaScriptComponent {

	private static final long serialVersionUID = 7738496276049495017L;

	protected static int currChartId = 0;

	public static int nextChartId() {
		return ++currChartId;
	}

	protected int chartId = nextChartId();

	/**
	 * Creates the chart object.
	 */
	public HighChart() {
		setId(getDomId());
		getState().setDomId(getDomId());
		getState().setHcjs("");
	}

	/**
	 * Returns the state of the chart that is shared with the web browser.
	 *
	 * @return the state of the chart that is shared with the web browser
	 */
	@Override
	protected HighChartState getState() {
		return (HighChartState) super.getState();
	}

	/**
	 * Returns the DOM ID of the chart component.
	 *
	 * @return the DOM ID of the chart component
	 */
	public String getDomId() {
		return "highchart_" + chartId;
	}

	/**
	 * <p>
	 * Sets the Highcharts JavaScript code describing the chart.
	 * Note that this code needs to bind the the JSON definition of the chart to a JS variable called <code>options</code>.
	 * </p>
	 * <p>
	 * Example:
	 * </p>
	 * 
	 * <pre>
	 * chart.setHcjs("var options = { chart: { title: 'my title' } };")
	 * </pre>
	 *
	 * @param hcjs
	 *            Highcharts JavaScript code describing the chart
	 */
	public void setHcjs(String hcjs) {
		getState().setHcjs(hcjs);
	}
}
