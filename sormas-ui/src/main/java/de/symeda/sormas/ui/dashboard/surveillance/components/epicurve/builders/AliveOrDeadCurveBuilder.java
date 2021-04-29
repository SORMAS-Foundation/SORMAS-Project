package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class AliveOrDeadCurveBuilder extends SurveillanceEpiCurveBuilder {

	public AliveOrDeadCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	List<EpiCurveSeriesElement> buildEpiCurveSeriesElements(List<Date> filteredDates, CaseCriteria caseCriteria) {
		int[] aliveNumbers = new int[filteredDates.size()];
		int[] deadNumbers = new int[filteredDates.size()];

		for (int i = 0; i < filteredDates.size(); i++) {
			caseCriteria = setNewCaseDatesInCaseCriteria(filteredDates.get(i), caseCriteria);
			Map<PresentCondition, Long> caseCounts = FacadeProvider.getCaseFacade().getCaseCountPerPersonCondition(caseCriteria, true, true);

			aliveNumbers[i] = caseCounts.getOrDefault(PresentCondition.ALIVE, 0L).intValue();
			deadNumbers[i] = caseCounts.getOrDefault(PresentCondition.DEAD, 0L).intValue();
		}

		return Arrays.asList(
			new EpiCurveSeriesElement(Captions.dashboardAlive, "#32CD32", aliveNumbers),
			new EpiCurveSeriesElement(Captions.dashboardDead, "#B22222", deadNumbers));
	}
}
