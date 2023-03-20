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

// TODO: Rename the class to LaboratoryResultsStatisticsComponent
public class FinalLaboratoryResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement labResultPositive;
	private final DashboardStatisticsCountElement labResultNegative;
	private final DashboardStatisticsCountElement labResultPending;
	private final DashboardStatisticsCountElement labResultIndeterminate;
	private final boolean showNotDoneCount;
	private boolean withPercentage;
	private DashboardStatisticsCountElement labResultNotDone;

	public FinalLaboratoryResultsStatisticsComponent(
		String titleCaption,
		String description,
		String subtitleCaption,
		boolean showNotDoneCount,
		boolean showInfoIcon) {
		super(titleCaption, description, showInfoIcon == true ? I18nProperties.getString(Strings.infoDashboardFinalLaboratoryResult) : "");

		this.showNotDoneCount = showNotDoneCount;

		if (subtitleCaption != null) {
			Label subTitleLabel = new Label(I18nProperties.getCaption(subtitleCaption));
			CssStyles.style(subTitleLabel, CssStyles.H3, CssStyles.VSPACE_TOP_NONE);
			addComponent(subTitleLabel);
		}

		// Count layout
		labResultPositive = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		labResultNegative = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		labResultPending = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		labResultIndeterminate =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);

		if (showNotDoneCount) {
			labResultNotDone = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotDone), CountElementStyle.NEUTRAL);

			buildCountLayout(labResultPositive, labResultNegative, labResultPending, labResultIndeterminate, labResultNotDone);
		} else {
			buildCountLayout(labResultPositive, labResultNegative, labResultPending, labResultIndeterminate);
		}
	}

	//TODO: refactor this method
	public void update(Map<PathogenTestResultType, Long> testResults) {
		if (testResults != null) {
			Long totalCount = null;
			Long labResultPositiveCount = testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L);
			Long labResultNegativeCount = testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L);
			Long labResultPendingCount = testResults.getOrDefault(PathogenTestResultType.PENDING, 0L);
			Long labResultIndeterminateCount = testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L);
			Long labResultNotDoneCount = showNotDoneCount ? testResults.getOrDefault(PathogenTestResultType.NOT_DONE, 0L) : null;

			if (withPercentage) {
				totalCount = testResults.values().stream().reduce(0L, Long::sum);

				labResultPositive.updateCountLabel(labResultPositiveCount + " (" + calculatePercentage(totalCount, labResultPositiveCount) + " %)");
				labResultNegative.updateCountLabel(labResultNegativeCount + " (" + calculatePercentage(totalCount, labResultNegativeCount) + " %)");
				labResultPending.updateCountLabel(labResultPendingCount + " (" + calculatePercentage(totalCount, labResultPendingCount) + " %)");
				labResultIndeterminate
					.updateCountLabel(labResultIndeterminateCount + " (" + calculatePercentage(totalCount, labResultIndeterminateCount) + " %)");

				if (labResultNotDoneCount != null) {
					labResultNotDone.updateCountLabel(labResultNotDoneCount + " (" + calculatePercentage(totalCount, labResultNotDoneCount) + " %)");
				}

			} else {
				updateTotalLabel(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

				labResultPositive.updateCountLabel(labResultPositiveCount.toString());
				labResultNegative.updateCountLabel(labResultNegativeCount.toString());
				labResultPending.updateCountLabel(labResultPendingCount.toString());
				labResultIndeterminate.updateCountLabel(labResultIndeterminateCount.toString());
				if (labResultNotDoneCount != null) {
					labResultNotDone.updateCountLabel(labResultNotDoneCount.toString());
				}
			}
		}
	}

	public int calculatePercentage(Long totalCount, Long labResultCount) {
		return totalCount == 0 ? 0 : (int) ((labResultCount * 100.0f) / totalCount);
	}

	public void setWithPercentage(boolean withPercentage) {
		this.withPercentage = withPercentage;
	}

}
