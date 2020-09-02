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

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * State of the chart which is transferred to the web browser whenever a property changed.
 *
 * @author Stefan Endrullis
 * 
 *         Based on https://github.com/xylo/highcharts-vaadin7 (Apache 2.0 license)
 */
public class HighChartState extends JavaScriptComponentState {

	private static final long serialVersionUID = -8963272160069864062L;

	private String domId;
	private String hcjs;

	public String getDomId() {
		return domId;
	}

	public void setDomId(String domId) {
		this.domId = domId;
	}

	public String getHcjs() {
		return hcjs;
	}

	public void setHcjs(String hcjs) {
		this.hcjs = hcjs;
	}
}
