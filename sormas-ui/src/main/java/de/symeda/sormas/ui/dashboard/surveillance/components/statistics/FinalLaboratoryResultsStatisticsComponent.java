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

public class FinalLaboratoryResultsStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement labResultPositive;
	private final DashboardStatisticsCountElement labResultNegative;
	private final DashboardStatisticsCountElement labResultPending;
	private final DashboardStatisticsCountElement labResultIndeterminate;
	private final boolean showNotDoneCount;
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

	public void update(Map<PathogenTestResultType, Long> testResults) {
		if (testResults != null) {
			updateTotalLabel(((Long) testResults.values().stream().mapToLong(Long::longValue).sum()).toString());

			labResultPositive.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
			labResultNegative.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
			labResultPending.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
			labResultIndeterminate.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
			if (showNotDoneCount) {
				labResultNotDone.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NOT_DONE, 0L).toString());
			}
		}
	}
}
