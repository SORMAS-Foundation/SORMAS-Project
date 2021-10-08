package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
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

	public void update(Map<CaseClassification, Integer> cases) {
		updateTotalLabel(Integer.toString(cases.values().stream().reduce(0, Integer::sum)));

		caseClassificationConfirmed.updateCountLabel(cases.getOrDefault(CaseClassification.CONFIRMED, 0));
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			caseClassificationConfirmedNoSymptoms.updateCountLabel(cases.getOrDefault(CaseClassification.CONFIRMED_NO_SYMPTOMS, 0));
			caseClassificationConfirmedUnknownSymptoms.updateCountLabel(cases.getOrDefault(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, 0));
		}
		caseClassificationProbable.updateCountLabel(cases.getOrDefault(CaseClassification.PROBABLE, 0));
		caseClassificationSuspect.updateCountLabel(cases.getOrDefault(CaseClassification.SUSPECT, 0));
		caseClassificationNotACase.updateCountLabel(cases.getOrDefault(CaseClassification.NO_CASE, 0));
		caseClassificationNotYetClassified.updateCountLabel(cases.getOrDefault(CaseClassification.NOT_CLASSIFIED, 0));
	}
}
