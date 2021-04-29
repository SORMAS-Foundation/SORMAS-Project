package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class CaseStatusCurveBuilder extends SurveillanceEpiCurveBuilder {

	public CaseStatusCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	void buildEpiCurve(List<Date> filteredDates, DashboardDataProvider dashboardDataProvider) {
		// Adds the number of confirmed, probable and suspect cases for each day as data
		int[] confirmedNumbers = new int[filteredDates.size()];
		int[] probableNumbers = new int[filteredDates.size()];
		int[] suspectNumbers = new int[filteredDates.size()];
		int[] notYetClassifiedNumbers = new int[filteredDates.size()];

		for (int i = 0; i < filteredDates.size(); i++) {
			CaseCriteria caseCriteria = buildCaseCriteria(filteredDates.get(i), dashboardDataProvider);

			Map<CaseClassification, Long> caseCounts = FacadeProvider.getCaseFacade().getCaseCountPerClassification(caseCriteria, true, true);

			confirmedNumbers[i] = caseCounts.getOrDefault(CaseClassification.CONFIRMED, 0L).intValue();
			probableNumbers[i] = caseCounts.getOrDefault(CaseClassification.PROBABLE, 0L).intValue();
			suspectNumbers[i] = caseCounts.getOrDefault(CaseClassification.SUSPECT, 0L).intValue();
			notYetClassifiedNumbers[i] = caseCounts.getOrDefault(CaseClassification.NOT_CLASSIFIED, 0L).intValue();
		}

		hcjs.append("series: [");
		buildSeriesElement(I18nProperties.getCaption(Captions.dashboardNotYetClassified), "#808080", notYetClassifiedNumbers);
		hcjs.append(", ");

		buildSeriesElement(I18nProperties.getCaption(Captions.dashboardSuspect), "#FFD700", suspectNumbers);
		hcjs.append(", ");

		buildSeriesElement(I18nProperties.getCaption(Captions.dashboardProbable), "#FF4500", probableNumbers);
		hcjs.append(", ");

		buildSeriesElement(I18nProperties.getCaption(Captions.dashboardConfirmed), "#B22222", confirmedNumbers);
		hcjs.append("], ");
	}
}
