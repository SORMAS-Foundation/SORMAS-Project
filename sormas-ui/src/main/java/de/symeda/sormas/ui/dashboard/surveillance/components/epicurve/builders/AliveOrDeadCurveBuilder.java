package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class AliveOrDeadCurveBuilder extends SurveillanceEpiCurveBuilder {

	public AliveOrDeadCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	void buildEpiCurve(List<Date> filteredDates, DashboardDataProvider dashboardDataProvider) {
		// Adds the number of alive and dead cases for each day as data
		int[] aliveNumbers = new int[filteredDates.size()];
		int[] deadNumbers = new int[filteredDates.size()];

		for (int i = 0; i < filteredDates.size(); i++) {
			CaseCriteria caseCriteria = buildCaseCriteria(filteredDates.get(i), dashboardDataProvider);

			Map<PresentCondition, Long> caseCounts = FacadeProvider.getCaseFacade().getCaseCountPerPersonCondition(caseCriteria, true, true);

			aliveNumbers[i] = caseCounts.getOrDefault(PresentCondition.ALIVE, 0L).intValue();
			deadNumbers[i] = caseCounts.getOrDefault(PresentCondition.DEAD, 0L).intValue();
		}

		hcjs.append("series: [");
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardAlive) + "', color: '#32CD32', dataLabels: { allowOverlap: false }, data: [");
		for (int i = 0; i < aliveNumbers.length; i++) {
			if (i == aliveNumbers.length - 1) {
				hcjs.append(aliveNumbers[i] + "]},");
			} else {
				hcjs.append(aliveNumbers[i] + ", ");
			}
		}
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardDead) + "', color: '#B22222', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < deadNumbers.length; i++) {
			if (i == deadNumbers.length - 1) {
				hcjs.append(deadNumbers[i] + "]}],");
			} else {
				hcjs.append(deadNumbers[i] + ", ");
			}
		}
	}
}
