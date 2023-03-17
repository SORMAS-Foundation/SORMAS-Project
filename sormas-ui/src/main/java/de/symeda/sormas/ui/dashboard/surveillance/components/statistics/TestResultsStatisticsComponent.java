package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.utils.CssStyles;

public class TestResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement positiveResult;
	private final DashboardStatisticsCountElement negativeResult;
	private final DashboardStatisticsCountElement pendingResult;
	private final DashboardStatisticsCountElement indeterminatResult;
	private final boolean showNotDoneCount;
	private DashboardStatisticsCountElement notDoneResult;

	public TestResultsStatisticsComponent(String titleCaption, String description, String subtitleCaption, boolean showNotDoneCount) {
		super(titleCaption, description, I18nProperties.getString(Strings.infoDashboardFinalLaboratoryResult));

		this.showNotDoneCount = showNotDoneCount;

		if (subtitleCaption != null) {
			Label subTitleLabel = new Label(I18nProperties.getCaption(subtitleCaption));
			CssStyles.style(subTitleLabel, CssStyles.H3, CssStyles.VSPACE_TOP_NONE);
			addComponent(subTitleLabel);
		}

		// Count layout
		positiveResult = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		negativeResult = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		pendingResult = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		indeterminatResult = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);

		if (showNotDoneCount) {
			notDoneResult = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotDone), CountElementStyle.NEUTRAL);

			buildCountLayout(positiveResult, negativeResult, pendingResult, indeterminatResult, notDoneResult);
		} else {
			buildCountLayout(positiveResult, negativeResult, pendingResult, indeterminatResult);
		}
	}

	public void update(Map<PathogenTestResultType, Long> testResults) {
		if (testResults != null) {
			updateTotalLabel(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

			positiveResult.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
			negativeResult.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
			pendingResult.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
			indeterminatResult.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
			if (showNotDoneCount) {
				notDoneResult.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NOT_DONE, 0L).toString());
			}
		}
	}
}
