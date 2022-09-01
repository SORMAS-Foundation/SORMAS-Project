package de.symeda.sormas.ui.reports.aggregate;

import static de.symeda.sormas.api.report.AggregateReportGroupingLevel.DISTRICT;
import static de.symeda.sormas.api.report.AggregateReportGroupingLevel.HEALTH_FACILITY;
import static de.symeda.sormas.api.report.AggregateReportGroupingLevel.POINT_OF_ENTRY;
import static de.symeda.sormas.api.report.AggregateReportGroupingLevel.REGION;

import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class AggregateReportGroupingSelector extends ComboBox {

	public AggregateReportGroupingSelector() {
		setCaption(I18nProperties.getCaption(Captions.AggregateReport_grouping));
		setItems(REGION, DISTRICT, HEALTH_FACILITY, POINT_OF_ENTRY);
		setEmptySelectionAllowed(true);
		setValue(REGION);
	}

}
