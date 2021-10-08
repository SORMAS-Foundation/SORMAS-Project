package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;

public class TestResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement testResultPositive;
	private final DashboardStatisticsCountElement testResultNegative;
	private final DashboardStatisticsCountElement testResultPending;
	private final DashboardStatisticsCountElement testResultIndeterminate;

	public TestResultsStatisticsComponent() {
		super(Captions.dashboardNewTestResults, Descriptions.descDashboardNewTestResults);

		// Count layout
		testResultPositive = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		testResultNegative = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		testResultPending = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		testResultIndeterminate =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);
		buildCountLayout(testResultPositive, testResultNegative, testResultPending, testResultIndeterminate);
	}

	public void update(Map<PathogenTestResultType, Long> testResults) {
		updateTotalLabel(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

		testResultPositive.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
		testResultNegative.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
		testResultPending.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
		testResultIndeterminate.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
	}
}
