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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.surveillance;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.CaseStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.EventStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.TestResultsStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.summary.DiseaseSummaryElementComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DiseaseStatisticsComponent extends CustomLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private final DashboardDataProvider dashboardDataProvider;

	private final CaseStatisticsComponent caseStatisticsComponent;

	// "Outbreak Districts" elements
	private DiseaseSummaryElementComponent lastReportedDistrict;
	private DiseaseSummaryElementComponent outbreakDistrictCount;

	//"cases in quarantine" elements 
	private DiseaseSummaryElementComponent casesInQuarantineByDate;
	private DiseaseSummaryElementComponent casesPlacedInQuarantineByDate;

	// "Contacts converted to cases"
	private DiseaseSummaryElementComponent contactsConvertedToCase;

	// "Case Fatality" elements
	private DiseaseSummaryElementComponent caseFatalityRateValue;
	private DiseaseSummaryElementComponent caseFatalityCountValue;
	private Label caseFatalityCountGrowth;

	private final EventStatisticsComponent eventStatisticsComponent;
	private final TestResultsStatisticsComponent testResultsStatisticsComponent;

	private static final String CASE_LOC = "case";
	private static final String OUTBREAK_LOC = "outbreak";
	private static final String EVENT_LOC = "event";
	private static final String SAMPLE_LOC = "sample";

	public DiseaseStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		setWidth(100, Unit.PERCENTAGE);

		setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumn(6, 0, 12, 0, LayoutUtil.fluidRowLocs(CASE_LOC, OUTBREAK_LOC)),
				LayoutUtil.fluidColumn(6, 0, 12, 0, LayoutUtil.fluidRowLocs(EVENT_LOC, SAMPLE_LOC))));

		caseStatisticsComponent = new CaseStatisticsComponent();
		eventStatisticsComponent = new EventStatisticsComponent();
		testResultsStatisticsComponent = new TestResultsStatisticsComponent();

		addComponent(caseStatisticsComponent, CASE_LOC);
		addComponent(createOutbreakDistrictAndCaseFatalityLayout(), OUTBREAK_LOC);
		addComponent(eventStatisticsComponent, EVENT_LOC);
		addComponent(testResultsStatisticsComponent, SAMPLE_LOC);
	}

	private DashboardStatisticsSubComponent createOutbreakDistrictAndCaseFatalityLayout() {
		DashboardStatisticsSubComponent layout = new DashboardStatisticsSubComponent();

		layout.addComponent(createCaseFatalityComponent());

		lastReportedDistrict =
			new DiseaseSummaryElementComponent(Strings.headingLastReportedDistrict, I18nProperties.getString(Strings.none).toUpperCase());
		layout.addComponent(lastReportedDistrict);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.OUTBREAKS)) {
			outbreakDistrictCount = new DiseaseSummaryElementComponent(Strings.headingOutbreakDistricts);
			layout.addComponent(outbreakDistrictCount);
		}

		casesInQuarantineByDate = new DiseaseSummaryElementComponent(Strings.headingCasesInQuarantine);
		layout.addComponent(casesInQuarantineByDate);

		casesPlacedInQuarantineByDate = new DiseaseSummaryElementComponent(Strings.headingCasesPlacedInQuarantine);
		layout.addComponent(casesPlacedInQuarantineByDate);

		contactsConvertedToCase = new DiseaseSummaryElementComponent(Strings.headingContactsConvertedToCase);
		layout.addComponent(contactsConvertedToCase);

		layout.addStyleName(CssStyles.VSPACE_TOP_4);

		return layout;
	}

	private Layout createCaseFatalityComponent() {
		HorizontalLayout component = new HorizontalLayout();
		component.setMargin(false);
		component.setSpacing(false);
		component.setWidth(100, Unit.PERCENTAGE);

		// rate
		{
			caseFatalityRateValue = new DiseaseSummaryElementComponent(Strings.headingCaseFatalityRate, "00.0%");
			component.addComponent(caseFatalityRateValue);
			component.setExpandRatio(caseFatalityRateValue, 1);
		}

		// count		
		{
			caseFatalityCountValue = new DiseaseSummaryElementComponent(Strings.headingFatalities, "0");

			// growth
			caseFatalityCountGrowth = new Label("", ContentMode.HTML);
			CssStyles.style(caseFatalityCountGrowth, CssStyles.VSPACE_TOP_5);
			caseFatalityCountValue.addComponent(caseFatalityCountGrowth);

			component.addComponent(caseFatalityCountValue);
			component.setExpandRatio(caseFatalityCountValue, 0);
			component.setComponentAlignment(caseFatalityCountValue, Alignment.MIDDLE_RIGHT);
		}

		return component;
	}

	public void refresh() {
		updateCaseComponent();
		updateCaseFatalityComponent();
		updateLastReportedDistrictComponent();
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.OUTBREAKS)) {
			updateOutbreakDistrictComponent();
		}
		updateEventComponent();
		updateTestResultComponent();
		updateCasesInQuarantineData();
	}

	private void updateCaseComponent() {
		caseStatisticsComponent.update(dashboardDataProvider.getCases());
	}

	private void updateCaseFatalityComponent() {
		List<DashboardCaseDto> newCases = dashboardDataProvider.getCases();
		List<DashboardCaseDto> previousCases = dashboardDataProvider.getPreviousCases();

		int casesCount = newCases.size();
		long fatalCasesCount = newCases.stream().filter(DashboardCaseDto::wasFatal).count();
		long previousFatalCasesCount = previousCases.stream().filter(DashboardCaseDto::wasFatal).count();
		long fatalCasesGrowth = fatalCasesCount - previousFatalCasesCount;
		float fatalityRate = 100 * ((float) fatalCasesCount / (float) (casesCount == 0 ? 1 : casesCount));
		fatalityRate = Math.round(fatalityRate * 100) / 100f;

		// count
		// current
		caseFatalityCountValue.updateTotalLabel(Long.toString(fatalCasesCount));
		// growth
		String chevronType;
		String criticalLevel;

		if (fatalCasesGrowth > 0) {
			chevronType = VaadinIcons.CHEVRON_UP.getHtml();
			criticalLevel = CssStyles.LABEL_CRITICAL;
		} else if (fatalCasesGrowth < 0) {
			chevronType = VaadinIcons.CHEVRON_DOWN.getHtml();
			criticalLevel = CssStyles.LABEL_POSITIVE;
		} else {
			chevronType = VaadinIcons.CHEVRON_RIGHT.getHtml();
			criticalLevel = CssStyles.LABEL_IMPORTANT;
		}

		caseFatalityCountGrowth.setValue(
			"<div class=\"v-label v-widget " + criticalLevel + " v-label-" + criticalLevel
				+ " align-center v-label-align-center bold v-label-bold v-has-width\" " + "	  style=\"margin-top: 4px;margin-left: 5px;\">"
				+ "		<span class=\"v-icon\" style=\"font-family: VaadinIcons;\">" + chevronType + "		</span>" + "</div>");

		// rate
		caseFatalityRateValue.updateTotalLabel(fatalityRate + "%");
	}

	private void updateLastReportedDistrictComponent() {
		String district = dashboardDataProvider.getLastReportedDistrict();
		lastReportedDistrict.updateTotalLabel(DataHelper.isNullOrEmpty(district) ? I18nProperties.getString(Strings.none).toUpperCase() : district);
	}

	private void updateOutbreakDistrictComponent() {
		outbreakDistrictCount.updateTotalLabel(dashboardDataProvider.getOutbreakDistrictCount().toString());
	}

	private void updateEventComponent() {
		eventStatisticsComponent.update(dashboardDataProvider.getEventCountByStatus());
	}

	private void updateTestResultComponent() {
		testResultsStatisticsComponent.update(dashboardDataProvider.getTestResultCountByResultType());
	}

	private void updateCasesInQuarantineData() {
		casesInQuarantineByDate.updateTotalLabel(dashboardDataProvider.getCasesInQuarantineCount().toString());
		casesPlacedInQuarantineByDate.updateTotalLabel(dashboardDataProvider.getCasesPlacedInQuarantineCount().toString());
		contactsConvertedToCase.updateTotalLabel(dashboardDataProvider.getContactsConvertedToCaseCount().toString());
	}
}
