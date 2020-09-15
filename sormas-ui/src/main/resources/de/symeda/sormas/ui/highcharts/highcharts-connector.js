/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
window.de_symeda_sormas_ui_highcharts_HighChart = function () {

	this.onStateChange = function () {

		// make sure to manually reload this after making changes, because it is being cached  

		// read state
		var domId = this.getState().domId;
		var hcjs = this.getState().hcjs;

		var connector = this;

		// evaluate highcharts JS which needs to define var "options"
		eval(hcjs);
		
		// set chart context
		var chart = Highcharts.chart(domId, options);
		chart.setSize(this.getElement().offsetWidth, this.getElement().offsetHeight, { duration: 0 });
		
		// resize the diagram whenever the vaadin element is resized
		this.addResizeListener(this.getElement(), function(o,b) {
			chart.setSize(o.element.offsetWidth, o.element.offsetHeight, { duration: 0 });
		});
	};
}