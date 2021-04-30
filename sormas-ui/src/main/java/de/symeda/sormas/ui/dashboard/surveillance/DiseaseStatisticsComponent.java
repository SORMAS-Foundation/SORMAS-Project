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
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
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
	private Label outbreakDistrictCountLabel;
	private Label lastReportedDistrictLabel;

	//"cases in quarantine" elements 
	private DiseaseSummaryElementComponent casesInQuarantineByDate;
	private Label casesPlacedInQuarantineByDate;

	// "Contacts converted to cases"
	private Label contactsConvertedToCase;

	// "Case Fatality" elements
	private Label caseFatalityRateValue;
	private Label caseFatalityCountValue;
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

		layout.addComponent(this.createLastReportedDistrictComponent());

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.OUTBREAKS)) {
			layout.addComponent(createOutbreakDistrictComponent());
		}

		casesInQuarantineByDate = new DiseaseSummaryElementComponent(Strings.headingCasesInQuarantine);
		layout.addComponent(casesInQuarantineByDate);

		layout.addComponent(createCasesPlacedInQuarantineLayout());

		layout.addComponent(createContactsConvertedToCasesLayout());

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
			HorizontalLayout rateLayout = new HorizontalLayout();
			rateLayout.setMargin(false);
			rateLayout.setSpacing(false);

			// title
			Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardCaseFatalityRate));
			CssStyles.style(titleLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
			rateLayout.addComponent(titleLabel);

			// value
			caseFatalityRateValue = new Label("00.0%");
			CssStyles.style(
				caseFatalityRateValue,
				CssStyles.LABEL_PRIMARY,
				CssStyles.LABEL_BOLD,
				CssStyles.LABEL_LARGE,
				CssStyles.HSPACE_LEFT_3,
				CssStyles.VSPACE_TOP_5);
			rateLayout.addComponent(caseFatalityRateValue);

			component.addComponent(rateLayout);
			component.setExpandRatio(rateLayout, 1);
		}

		// count		
		{
			HorizontalLayout countLayout = new HorizontalLayout();
			countLayout.setMargin(false);
			countLayout.setSpacing(false);

			// title
			Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardFatalities));
			CssStyles.style(titleLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4, CssStyles.HSPACE_RIGHT_3);
			countLayout.addComponent(titleLabel);

			// value
			caseFatalityCountValue = new Label("0");
			CssStyles.style(
				caseFatalityCountValue,
				CssStyles.LABEL_PRIMARY,
				CssStyles.LABEL_BOLD,
				CssStyles.LABEL_LARGE,
				CssStyles.HSPACE_RIGHT_5,
				CssStyles.VSPACE_TOP_5);
			countLayout.addComponent(caseFatalityCountValue);

			// growth
			caseFatalityCountGrowth = new Label("", ContentMode.HTML);
			CssStyles.style(caseFatalityCountGrowth, CssStyles.VSPACE_TOP_5);
			countLayout.addComponent(caseFatalityCountGrowth);

			component.addComponent(countLayout);
			component.setExpandRatio(countLayout, 0);
			component.setComponentAlignment(countLayout, Alignment.MIDDLE_RIGHT);
		}

		return component;
	}

	private Layout createLastReportedDistrictComponent() {
		HorizontalLayout component = new HorizontalLayout();
		component.setMargin(false);
		component.setSpacing(false);

		// title
		Label titleLabel = new Label(I18nProperties.getCaption(I18nProperties.getCaption(Captions.dashboardLastReportedDistrict)));
		CssStyles.style(titleLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
		component.addComponent(titleLabel);

		// value
		lastReportedDistrictLabel = new Label(I18nProperties.getString(Strings.none).toUpperCase());
		CssStyles.style(
			lastReportedDistrictLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_LARGE,
			CssStyles.HSPACE_LEFT_3,
			CssStyles.VSPACE_TOP_5);
		component.addComponent(lastReportedDistrictLabel);

		return component;
	}

	private Layout createOutbreakDistrictComponent() {
		HorizontalLayout component = new HorizontalLayout();
		component.setMargin(false);
		component.setSpacing(false);

		// title
		Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardOutbreakDistricts));
		CssStyles.style(titleLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
		component.addComponent(titleLabel);

		// count
		outbreakDistrictCountLabel = new Label();
		CssStyles.style(
			outbreakDistrictCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_LARGE,
			CssStyles.HSPACE_LEFT_3,
			CssStyles.VSPACE_TOP_5);
		component.addComponent(outbreakDistrictCountLabel);

		return component;
	}

	private HorizontalLayout createCasesPlacedInQuarantineLayout() {

		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);

		Label captionPlacedInQuarantine = new Label(I18nProperties.getString(Strings.headingCasesPlacedInQuarantine));
		CssStyles.style(captionPlacedInQuarantine, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
		layout.addComponent(captionPlacedInQuarantine);

		casesPlacedInQuarantineByDate = new Label();
		CssStyles.style(
			casesPlacedInQuarantineByDate,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_LARGE,
			CssStyles.HSPACE_LEFT_3,
			CssStyles.VSPACE_TOP_5);
		layout.addComponent(casesPlacedInQuarantineByDate);

		return layout;
	}

	private HorizontalLayout createContactsConvertedToCasesLayout() {

		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);

		Label captionInQuarantine = new Label(I18nProperties.getString(Strings.headingContactsConvertedToCase));
		CssStyles.style(captionInQuarantine, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
		layout.addComponent(captionInQuarantine);

		contactsConvertedToCase = new Label();
		CssStyles.style(
			contactsConvertedToCase,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_LARGE,
			CssStyles.HSPACE_LEFT_3,
			CssStyles.VSPACE_TOP_5);
		layout.addComponent(contactsConvertedToCase);

		return layout;
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
		List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
		caseStatisticsComponent.update(cases);
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
		caseFatalityCountValue.setValue(Long.toString(fatalCasesCount));
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
		caseFatalityRateValue.setValue(fatalityRate + "%");
	}

	private void updateLastReportedDistrictComponent() {
		String district = dashboardDataProvider.getLastReportedDistrict();
		lastReportedDistrictLabel.setValue(DataHelper.isNullOrEmpty(district) ? I18nProperties.getString(Strings.none).toUpperCase() : district);
	}

	private void updateOutbreakDistrictComponent() {
		Long districtCount = dashboardDataProvider.getOutbreakDistrictCount();
		outbreakDistrictCountLabel.setValue(districtCount.toString());
	}

	private void updateEventComponent() {
		Map<EventStatus, Long> events = dashboardDataProvider.getEventCountByStatus();
		eventStatisticsComponent.update(events);
	}

	private void updateTestResultComponent() {
		Map<PathogenTestResultType, Long> testResults = dashboardDataProvider.getTestResultCountByResultType();
		testResultsStatisticsComponent.update(testResults);
	}

	private void updateCasesInQuarantineData() {

		contactsConvertedToCase.setValue(dashboardDataProvider.getContactsConvertedToCaseCount().toString());

		casesInQuarantineByDate.updateTotalLabel(dashboardDataProvider.getCasesInQuarantineCount().toString());

		casesPlacedInQuarantineByDate.setValue(dashboardDataProvider.getCasesPlacedInQuarantineCount().toString());

	}
}
