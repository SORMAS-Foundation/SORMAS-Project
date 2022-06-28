package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

public class AbstractAggregateReportsView extends AbstractSubNavigationView<Component> {

	public static final String ROOT_VIEW_NAME = "aggregatereports";

	public AggregateReportCriteria criteria = new AggregateReportCriteria();

	protected AbstractAggregateReportsView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		menu.removeAllViews();
		menu.addView(
			AggregateReportsView.VIEW_NAME,
			I18nProperties.getPrefixCaption("View", AggregateReportsView.VIEW_NAME.replaceAll("/", ".")),
			params);

		menu.addView(ReportDataView.VIEW_NAME, I18nProperties.getPrefixCaption("View", ReportDataView.VIEW_NAME.replaceAll("/", ".")), params);
	}

	public static void registerViews(Navigator navigator) {
		navigator.addView(AggregateReportsView.VIEW_NAME, AggregateReportsView.class);
		navigator.addView(ReportDataView.VIEW_NAME, ReportDataView.class);
	}

	public AggregateReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AggregateReportCriteria criteria) {
		this.criteria = criteria;
	}
}
