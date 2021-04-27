package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.utils.CssStyles;

public class TestResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final Label testResultCountLabel;
	private final DashboardStatisticsCountElement testResultPositive;
	private final DashboardStatisticsCountElement testResultNegative;
	private final DashboardStatisticsCountElement testResultPending;
	private final DashboardStatisticsCountElement testResultIndeterminate;

	public TestResultsStatisticsComponent() {
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);
		// count
		testResultCountLabel = new Label();
		testResultCountLabel.setDescription(I18nProperties.getDescription(Descriptions.descDashboardNewTestResults));
		CssStyles.style(
			testResultCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(testResultCountLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardNewTestResults));
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(titleLabel);

		addComponent(headerLayout);

		// Count layout
		testResultPositive = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		testResultNegative = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		testResultPending = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		testResultIndeterminate =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);
		buildCountLayout(testResultPositive, testResultNegative, testResultPending, testResultIndeterminate);
	}

	public void update(Map<PathogenTestResultType, Long> testResults) {
		testResultCountLabel.setValue(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

		testResultPositive.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
		testResultNegative.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
		testResultPending.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
		testResultIndeterminate.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
	}
}
