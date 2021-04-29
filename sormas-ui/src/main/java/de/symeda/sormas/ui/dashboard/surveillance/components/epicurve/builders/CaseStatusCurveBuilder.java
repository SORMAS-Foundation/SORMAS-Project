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
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardNotYetClassified)
				+ "', color: '#808080', dataLabels: { allowOverlap: false }, data: [");
		addDataObject(notYetClassifiedNumbers);
		hcjs.append("]}, ");

		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardSuspect)
				+ "', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
		addDataObject(suspectNumbers);
		hcjs.append("]}, ");

		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardProbable)
				+ "', color: '#FF4500', dataLabels: { allowOverlap: false },  data: [");
		addDataObject(probableNumbers);
		hcjs.append("]}, ");

		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardConfirmed)
				+ "', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
		addDataObject(confirmedNumbers);
		hcjs.append("]}], ");
	}
}
