/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.surveillance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.utils.CssStyles;

public class SurveillanceEpiCurveComponent extends AbstractEpiCurveComponent {

	private static final long serialVersionUID = 6582975657305031105L;

	private SurveillanceEpiCurveMode epiCurveSurveillanceMode;
	
	public SurveillanceEpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected OptionGroup createEpiCurveModeSelector() {
		if (epiCurveSurveillanceMode == null) {
			epiCurveSurveillanceMode = SurveillanceEpiCurveMode.CASE_STATUS;
		}
		
		OptionGroup epiCurveModeOptionGroup = new OptionGroup();
		epiCurveModeOptionGroup.setMultiSelect(false);
		CssStyles.style(epiCurveModeOptionGroup, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_SUBTLE);
		epiCurveModeOptionGroup.addItems((Object[]) SurveillanceEpiCurveMode.values());
		epiCurveModeOptionGroup.setValue(epiCurveSurveillanceMode);	
		epiCurveModeOptionGroup.select(epiCurveSurveillanceMode);
		epiCurveModeOptionGroup.addValueChangeListener(e -> {
			epiCurveSurveillanceMode = (SurveillanceEpiCurveMode) e.getProperty().getValue();
			clearAndFillEpiCurveChart();
		});
		return epiCurveModeOptionGroup;
	}
	
	@Override
	public void clearAndFillEpiCurveChart() {
		StringBuilder hcjs = new StringBuilder();
		hcjs.append(
				"var options = {"
						+ "chart:{ "
						+ " type: 'column', "
						+ " backgroundColor: 'transparent' "
						+ "},"
						+ "credits:{ enabled: false },"
						+ "exporting:{ "
						+ " enabled: true,"
						+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
						+ "},"
						+ "title:{ text: '' },"
				);

		// Creates and sets the labels for each day on the x-axis
		List<Date> filteredDates = buildListOfFilteredDates();
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : filteredDates) {
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				String label = DateHelper.formatLocalShortDate(date);
				newLabels.add(label);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				calendar.setTime(date);
				String label = DateHelper.getEpiWeek(date).toShortString();
				newLabels.add(label);
			} else {
				String label = DateHelper.formatDateWithMonthAbbreviation(date);
				newLabels.add(label);
			}
		}

		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		hcjs.append("yAxis: { min: 0, title: { text: '" + I18nProperties.getCaption(Captions.dashboardNumberOfCases) + "' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>" + I18nProperties.getCaption(Captions.dashboardTotal) + ": {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");

		if (epiCurveSurveillanceMode == SurveillanceEpiCurveMode.CASE_STATUS) {
			// Adds the number of confirmed, probable and suspect cases for each day as data
			int[] confirmedNumbers = new int[newLabels.size()];
			int[] probableNumbers = new int[newLabels.size()];
			int[] suspectNumbers = new int[newLabels.size()];
			int[] notYetClassifiedNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				CaseCriteria caseCriteria = new CaseCriteria()
						.disease(dashboardDataProvider.getDisease())
						.region(dashboardDataProvider.getRegion())
						.district(dashboardDataProvider.getDistrict())
						.surveillanceType(dashboardDataProvider.getCaseSurveillanceType());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date), NewCaseDateType.MOST_RELEVANT);
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date), NewCaseDateType.MOST_RELEVANT);
				} else {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date), NewCaseDateType.MOST_RELEVANT);
				}

				Map<CaseClassification, Long> caseCounts = FacadeProvider.getCaseFacade()
						.getCaseCountPerClassification(caseCriteria, false);

				Long confirmedCount = caseCounts.get(CaseClassification.CONFIRMED);
				Long probableCount = caseCounts.get(CaseClassification.PROBABLE);
				Long suspectCount = caseCounts.get(CaseClassification.SUSPECT);
				Long notYetClassifiedCount = caseCounts.get(CaseClassification.NOT_CLASSIFIED);
				confirmedNumbers[i] = confirmedCount != null ? confirmedCount.intValue() : 0;
				probableNumbers[i] = probableCount != null ? probableCount.intValue() : 0;
				suspectNumbers[i] = suspectCount != null ? suspectCount.intValue() : 0;
				notYetClassifiedNumbers[i] = notYetClassifiedCount != null ? notYetClassifiedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardNotYetClassified) + "', color: '#808080', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < notYetClassifiedNumbers.length; i++) {
				if (i == notYetClassifiedNumbers.length - 1) {
					hcjs.append(notYetClassifiedNumbers[i] + "]},");
				} else {
					hcjs.append(notYetClassifiedNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardSuspect) + "', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < suspectNumbers.length; i++) {
				if (i == suspectNumbers.length - 1) {
					hcjs.append(suspectNumbers[i] + "]},");
				} else {
					hcjs.append(suspectNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardProbable) + "', color: '#FF4500', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < probableNumbers.length; i++) {
				if (i == probableNumbers.length - 1) {
					hcjs.append(probableNumbers[i] + "]},");
				} else {
					hcjs.append(probableNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardConfirmed) + "', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < confirmedNumbers.length; i++) {
				if (i == confirmedNumbers.length - 1) {
					hcjs.append(confirmedNumbers[i] + "]}]};");
				} else {
					hcjs.append(confirmedNumbers[i] + ", ");
				}
			}
		} else {
			// Adds the number of alive and dead cases for each day as data
			int[] aliveNumbers = new int[newLabels.size()];
			int[] deadNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				CaseCriteria caseCriteria = new CaseCriteria()
						.disease(dashboardDataProvider.getDisease())
						.region(dashboardDataProvider.getRegion())
						.district(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date), NewCaseDateType.MOST_RELEVANT);
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date), NewCaseDateType.MOST_RELEVANT);
				} else {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date), NewCaseDateType.MOST_RELEVANT);
				}

				Map<PresentCondition, Long> caseCounts = FacadeProvider.getCaseFacade()
						.getCaseCountPerPersonCondition(caseCriteria, false);

				Long aliveCount = caseCounts.get(PresentCondition.ALIVE);
				Long deadCount = caseCounts.get(PresentCondition.DEAD);
				aliveNumbers[i] = aliveCount != null ? aliveCount.intValue() : 0;
				deadNumbers[i] = deadCount != null ? deadCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardAlive) + "', color: '#32CD32', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < aliveNumbers.length; i++) {
				if (i == aliveNumbers.length - 1) {
					hcjs.append(aliveNumbers[i] + "]},");
				} else {
					hcjs.append(aliveNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: '" + I18nProperties.getCaption(Captions.dashboardDead) + "', color: '#B22222', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < deadNumbers.length; i++) {
				if (i == deadNumbers.length - 1) {
					hcjs.append(deadNumbers[i] + "]}]};");
				} else {
					hcjs.append(deadNumbers[i] + ", ");
				}
			}
		}

		epiCurveChart.setHcjs(hcjs.toString());	
	}
	
	
}
