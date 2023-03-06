package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.utils.CssStyles;

public class TestResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement testResultPositive;
	private final DashboardStatisticsCountElement testResultNegative;
	private final DashboardStatisticsCountElement testResultPending;
	private final DashboardStatisticsCountElement testResultIndeterminate;
	private final boolean showNotDoneCount;
	private DashboardStatisticsCountElement testResultNotDone;

	public TestResultsStatisticsComponent(String titleCaption, String description, String subtitleCaption, boolean showNotDoneCount) {
		super(titleCaption, description);

		this.showNotDoneCount = showNotDoneCount;

		if (subtitleCaption != null) {
			Label subTitleLabel = new Label(I18nProperties.getCaption(subtitleCaption));
			CssStyles.style(subTitleLabel, CssStyles.H3, CssStyles.VSPACE_TOP_NONE);
			addComponent(subTitleLabel);
		}

		// Count layout
		testResultPositive = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		testResultNegative = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		testResultPending = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		testResultIndeterminate =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);

		if (showNotDoneCount) {
			testResultNotDone = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotDone), CountElementStyle.NEUTRAL);

			buildCountLayout(testResultPositive, testResultNegative, testResultPending, testResultIndeterminate, testResultNotDone);
		} else {
			buildCountLayout(testResultPositive, testResultNegative, testResultPending, testResultIndeterminate);
		}
	}

	public void update(Map<PathogenTestResultType, Long> testResults) {
		if (testResults != null) {
			updateTotalLabel(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

			testResultPositive.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
			testResultNegative.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
			testResultPending.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
			testResultIndeterminate.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
			if (showNotDoneCount) {
				testResultNotDone.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NOT_DONE, 0L).toString());
			}
		}
	}
}
