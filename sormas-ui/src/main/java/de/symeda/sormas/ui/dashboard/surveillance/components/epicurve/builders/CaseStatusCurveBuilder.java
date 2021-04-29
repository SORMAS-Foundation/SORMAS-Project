package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class CaseStatusCurveBuilder extends SurveillanceEpiCurveBuilder {

	public CaseStatusCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	List<EpiCurveSeriesElement> buildEpiCurveSeriesElements(List<Date> filteredDates, CaseCriteria caseCriteria) {
		int filteredDatesSize = filteredDates.size();

		int[] confirmedNumbers = new int[filteredDatesSize];
		int[] probableNumbers = new int[filteredDatesSize];
		int[] suspectNumbers = new int[filteredDatesSize];
		int[] notYetClassifiedNumbers = new int[filteredDatesSize];

		int[] confirmedNoSymptomsNumbers = new int[filteredDatesSize];
		int[] confirmedUnknownSymptomsNumbers = new int[filteredDatesSize];

		for (int i = 0; i < filteredDates.size(); i++) {
			caseCriteria = setNewCaseDatesInCaseCriteria(filteredDates.get(i), caseCriteria);
			Map<CaseClassification, Long> caseCounts = FacadeProvider.getCaseFacade().getCaseCountPerClassification(caseCriteria, true, true);

			confirmedNumbers[i] = caseCounts.getOrDefault(CaseClassification.CONFIRMED, 0L).intValue();
			probableNumbers[i] = caseCounts.getOrDefault(CaseClassification.PROBABLE, 0L).intValue();
			suspectNumbers[i] = caseCounts.getOrDefault(CaseClassification.SUSPECT, 0L).intValue();
			notYetClassifiedNumbers[i] = caseCounts.getOrDefault(CaseClassification.NOT_CLASSIFIED, 0L).intValue();

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
				confirmedNoSymptomsNumbers[i] = caseCounts.getOrDefault(CaseClassification.CONFIRMED_NO_SYMPTOMS, 0L).intValue();
				confirmedUnknownSymptomsNumbers[i] = caseCounts.getOrDefault(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, 0L).intValue();
			}
		}

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			return Arrays.asList(
				new EpiCurveSeriesElement(Captions.dashboardNotYetClassified, "#808080", notYetClassifiedNumbers),
				new EpiCurveSeriesElement(Captions.dashboardSuspect, "#FFD700", suspectNumbers),
				new EpiCurveSeriesElement(Captions.dashboardProbable, "#FF4500", probableNumbers),
				new EpiCurveSeriesElement(Captions.dashboardConfirmedUnknownSymptoms, "rgba(200, 0, 0, 0.5)", confirmedUnknownSymptomsNumbers),
				new EpiCurveSeriesElement(Captions.dashboardConfirmedNoSymptoms, "rgba(200, 0, 0, 0.7)", confirmedNoSymptomsNumbers),
				new EpiCurveSeriesElement(Captions.dashboardConfirmed, "#B22222", confirmedNumbers));
		} else {
			return Arrays.asList(
				new EpiCurveSeriesElement(Captions.dashboardNotYetClassified, "#808080", notYetClassifiedNumbers),
				new EpiCurveSeriesElement(Captions.dashboardSuspect, "#FFD700", suspectNumbers),
				new EpiCurveSeriesElement(Captions.dashboardProbable, "#FF4500", probableNumbers),
				new EpiCurveSeriesElement(Captions.dashboardConfirmed, "#B22222", confirmedNumbers));
		}
	}
}
