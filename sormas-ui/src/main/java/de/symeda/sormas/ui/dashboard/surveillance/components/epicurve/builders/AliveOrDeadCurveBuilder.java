package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PresentCondition;

public class AliveOrDeadCurveBuilder extends SurveillanceEpiCurveBuilder {

	public AliveOrDeadCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	List<EpiCurveSeriesElement> buildEpiCurveSeriesElements(List<Date> filteredDates, DashboardCriteria dashboardCriteria) {
		int filteredDatesSize = filteredDates.size();

		int[] aliveNumbers = new int[filteredDatesSize];
		int[] deadNumbers = new int[filteredDatesSize];
		int[] unknownNumbers = new int[filteredDatesSize];

		for (int i = 0; i < filteredDates.size(); i++) {
			dashboardCriteria = setNewCaseDatesInCaseCriteria(filteredDates.get(i), dashboardCriteria);
			Map<PresentCondition, Integer> caseCounts = FacadeProvider.getDashboardFacade().getCasesCountPerPersonCondition(dashboardCriteria);

			aliveNumbers[i] = caseCounts.getOrDefault(PresentCondition.ALIVE, 0).intValue();
			deadNumbers[i] =
				caseCounts.getOrDefault(PresentCondition.DEAD, 0).intValue() + caseCounts.getOrDefault(PresentCondition.BURIED, 0).intValue();
			unknownNumbers[i] = caseCounts.getOrDefault(PresentCondition.UNKNOWN, 0).intValue();
		}

		return Arrays.asList(
			new EpiCurveSeriesElement(Captions.dashboardAlive, "#32CD32", aliveNumbers),
			new EpiCurveSeriesElement(Captions.dashboardDead, "#B22222", deadNumbers),
			new EpiCurveSeriesElement(Captions.dashboardUnknown, "#808080", unknownNumbers));
	}
}
