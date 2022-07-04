
package de.symeda.sormas.ui.reports;

import de.symeda.sormas.ui.utils.ViewConfiguration;

public class AggregateReportViewConfiguration extends ViewConfiguration {

	private AggregateReportViewType aggregateReportViewType;

	public AggregateReportViewType getViewType() {
		return aggregateReportViewType;
	}

	public void setViewType(AggregateReportViewType aggregateReportViewType) {
		this.aggregateReportViewType = aggregateReportViewType;
	}
}
