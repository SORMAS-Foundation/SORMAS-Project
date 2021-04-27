package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;

public class CaseStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement caseClassificationConfirmed;
	private final DashboardStatisticsCountElement caseClassificationProbable;
	private final DashboardStatisticsCountElement caseClassificationSuspect;
	private final DashboardStatisticsCountElement caseClassificationNotACase;
	private final DashboardStatisticsCountElement caseClassificationNotYetClassified;

	public CaseStatisticsComponent() {
		super(Captions.dashboardNewCases);

		// Count layout
		caseClassificationConfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CountElementStyle.CRITICAL);
		caseClassificationProbable =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardProbable), CountElementStyle.IMPORTANT);
		caseClassificationSuspect =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardSuspect), CountElementStyle.RELEVANT);
		caseClassificationNotACase =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotACase), CountElementStyle.POSITIVE);
		caseClassificationNotYetClassified =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotYetClassified), CountElementStyle.MINOR);
		buildCountLayout(
			caseClassificationConfirmed,
			caseClassificationProbable,
			caseClassificationSuspect,
			caseClassificationNotACase,
			caseClassificationNotYetClassified);
	}

	public void update(List<DashboardCaseDto> cases) {
		updateTotalLabel(Integer.toString(cases.size()));

		int confirmedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
		int notACaseCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);
	}
}
