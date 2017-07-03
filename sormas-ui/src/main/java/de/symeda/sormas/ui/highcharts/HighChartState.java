package de.symeda.sormas.ui.highcharts;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * State of the chart which is transferred to the web browser whenever a property changed.
 *
 * @author Stefan Endrullis
 *  
 * Based on https://github.com/xylo/highcharts-vaadin7 (Apache 2.0 license)
 */
public class HighChartState extends JavaScriptComponentState {
	private static final long serialVersionUID = -8963272160069864062L;

	public String domId;
	public String hcjs;
}