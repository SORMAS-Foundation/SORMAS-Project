package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;

public class CaseStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement caseClassificationConfirmed;
	private final DashboardStatisticsCountElement caseClassificationConfirmedNoSymptoms;
	private final DashboardStatisticsCountElement caseClassificationConfirmedUnknownSymptoms;
	private final DashboardStatisticsCountElement caseClassificationProbable;
	private final DashboardStatisticsCountElement caseClassificationSuspect;
	private final DashboardStatisticsCountElement caseClassificationNotACase;
	private final DashboardStatisticsCountElement caseClassificationNotYetClassified;

	public CaseStatisticsComponent() {
		super(Captions.dashboardNewCases);

		// Count layout
		caseClassificationConfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CountElementStyle.CRITICAL);
		caseClassificationConfirmedNoSymptoms = new DashboardStatisticsCountElement(
			I18nProperties.getCaption(Captions.dashboardConfirmedNoSymptoms),
			CountElementStyle.CRITICAL_TRANSPARENT);
		caseClassificationConfirmedUnknownSymptoms = new DashboardStatisticsCountElement(
			I18nProperties.getCaption(Captions.dashboardConfirmedUnknownSymptoms),
			CountElementStyle.CRITICAL_MORE_TRANSPARENT);
		caseClassificationProbable =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardProbable), CountElementStyle.IMPORTANT);
		caseClassificationSuspect =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardSuspect), CountElementStyle.RELEVANT);
		caseClassificationNotACase =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotACase), CountElementStyle.POSITIVE);
		caseClassificationNotYetClassified =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotYetClassified), CountElementStyle.MINOR);
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			buildCountLayout(
				caseClassificationConfirmed,
				caseClassificationConfirmedNoSymptoms,
				caseClassificationConfirmedUnknownSymptoms,
				caseClassificationProbable,
				caseClassificationSuspect,
				caseClassificationNotACase,
				caseClassificationNotYetClassified);
		} else {
			buildCountLayout(
				caseClassificationConfirmed,
				caseClassificationProbable,
				caseClassificationSuspect,
				caseClassificationNotACase,
				caseClassificationNotYetClassified);
		}
	}

	public void update(List<DashboardCaseDto> cases) {
		updateTotalLabel(Integer.toString(cases.size()));

		int confirmedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			int confirmedCasesNoSymptomsCount =
				(int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED_NO_SYMPTOMS).count();
			caseClassificationConfirmedNoSymptoms.updateCountLabel(confirmedCasesNoSymptomsCount);
			int confirmedCasesUnknownSymptomsCount =
				(int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS).count();
			caseClassificationConfirmedUnknownSymptoms.updateCountLabel(confirmedCasesUnknownSymptomsCount);
		}
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
