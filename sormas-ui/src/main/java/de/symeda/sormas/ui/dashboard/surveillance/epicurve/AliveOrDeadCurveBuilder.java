package de.symeda.sormas.ui.dashboard.surveillance.epicurve;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class AliveOrDeadCurveBuilder extends SurveillanceEpiCurveBuilder {

	public AliveOrDeadCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	public void buildEpiCurve(List<Date> filteredDates, DashboardDataProvider dashboardDataProvider) {
		// Adds the number of alive and dead cases for each day as data
		int[] aliveNumbers = new int[filteredDates.size()];
		int[] deadNumbers = new int[filteredDates.size()];

		for (int i = 0; i < filteredDates.size(); i++) {
			Date date = filteredDates.get(i);

			CaseCriteria caseCriteria = new CaseCriteria().disease(dashboardDataProvider.getDisease())
				.region(dashboardDataProvider.getRegion())
				.district(dashboardDataProvider.getDistrict());
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				caseCriteria.newCaseDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date), NewCaseDateType.MOST_RELEVANT);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				caseCriteria.newCaseDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date), NewCaseDateType.MOST_RELEVANT);
			} else {
				caseCriteria.newCaseDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date), NewCaseDateType.MOST_RELEVANT);
			}

			Map<PresentCondition, Long> caseCounts = FacadeProvider.getCaseFacade().getCaseCountPerPersonCondition(caseCriteria, true, true);

			Long aliveCount = caseCounts.get(PresentCondition.ALIVE);
			Long deadCount = caseCounts.get(PresentCondition.DEAD);
			aliveNumbers[i] = aliveCount != null ? aliveCount.intValue() : 0;
			deadNumbers[i] = deadCount != null ? deadCount.intValue() : 0;
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
