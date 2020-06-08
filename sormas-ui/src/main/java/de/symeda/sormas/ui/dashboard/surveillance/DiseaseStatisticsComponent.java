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
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DiseaseStatisticsComponent extends CustomLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;

	// "New Cases" elements
	private Label caseCountLabel;
//	private Label caseDiseaseLabel;
	private DashboardStatisticsCountElement caseClassificationConfirmed;
	private DashboardStatisticsCountElement caseClassificationProbable;
	private DashboardStatisticsCountElement caseClassificationSuspect;
	private DashboardStatisticsCountElement caseClassificationNotACase;
	private DashboardStatisticsCountElement caseClassificationNotYetClassified;

	// "Outbreak Districts" elements
	private Label outbreakDistrictCountLabel;
	private Label lastReportedDistrictLabel;

	// "Case Fatality" elements
	private Label caseFatalityRateValue;
	private Label caseFatalityCountValue;
	private Label caseFatalityCountGrowth;

	// "New Events" elements
	private Label eventCountLabel;
	private DashboardStatisticsCountElement eventStatusConfirmed;
	private DashboardStatisticsCountElement eventStatusPossible;
	private DashboardStatisticsCountElement eventStatusNotAnEvent;

	// "New Test Results" elements
	private Label testResultCountLabel;
	private DashboardStatisticsCountElement testResultPositive;
	private DashboardStatisticsCountElement testResultNegative;
	private DashboardStatisticsCountElement testResultPending;
	private DashboardStatisticsCountElement testResultIndeterminate;

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

		addComponent(createCaseComponent(), CASE_LOC);
		addComponent(createOutbreakDistrictAndCaseFatalityLayout(), OUTBREAK_LOC);
		addComponent(createEventComponent(), EVENT_LOC);
		addComponent(createTestResultComponent(), SAMPLE_LOC);
	}

	private DashboardStatisticsSubComponent createCaseComponent() {
		DashboardStatisticsSubComponent caseComponent = new DashboardStatisticsSubComponent();

		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);
		// count
		caseCountLabel = new Label();
		CssStyles.style(
			caseCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(caseCountLabel);
		// title
		Label caseComponentTitle = new Label(I18nProperties.getCaption(Captions.dashboardNewCases));
		CssStyles.style(caseComponentTitle, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(caseComponentTitle);

		caseComponent.addComponent(headerLayout);

		// Count layout
		CssLayout countLayout = caseComponent.createCountLayout(true);
		caseClassificationConfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CountElementStyle.CRITICAL);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationConfirmed);
		caseClassificationProbable =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardProbable), CountElementStyle.IMPORTANT);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationProbable);
		caseClassificationSuspect =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardSuspect), CountElementStyle.RELEVANT);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationSuspect);
		caseClassificationNotACase =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotACase), CountElementStyle.POSITIVE);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationNotACase);
		caseClassificationNotYetClassified =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotYetClassified), CountElementStyle.MINOR);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationNotYetClassified);
		caseComponent.addComponent(countLayout);

		return caseComponent;
	}

	private DashboardStatisticsSubComponent createOutbreakDistrictAndCaseFatalityLayout() {
		DashboardStatisticsSubComponent layout = new DashboardStatisticsSubComponent();

		layout.addComponent(createCaseFatalityComponent());

		layout.addComponent(this.createLastReportedDistrictComponent());

		layout.addComponent(createOutbreakDistrictComponent());

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

	private DashboardStatisticsSubComponent createEventComponent() {
		DashboardStatisticsSubComponent eventComponent = new DashboardStatisticsSubComponent();

		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);

		// count
		eventCountLabel = new Label();
		CssStyles.style(
			eventCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(eventCountLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardNewEvents));
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(titleLabel);

		eventComponent.addComponent(headerLayout);

		// Count layout
		CssLayout countLayout = eventComponent.createCountLayout(true);
		eventStatusConfirmed = new DashboardStatisticsCountElement(EventStatus.CONFIRMED.toString(), CountElementStyle.CRITICAL);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusConfirmed);
		eventStatusPossible = new DashboardStatisticsCountElement(EventStatus.POSSIBLE.toString(), CountElementStyle.IMPORTANT);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusPossible);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement(EventStatus.NO_EVENT.toString(), CountElementStyle.POSITIVE);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusNotAnEvent);
		eventComponent.addComponent(countLayout);

		return eventComponent;
	}

	private DashboardStatisticsSubComponent createTestResultComponent() {
		DashboardStatisticsSubComponent testResultComponent = new DashboardStatisticsSubComponent();

		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);
		// count
		testResultCountLabel = new Label();
		testResultCountLabel.setDescription(I18nProperties.getDescription(Descriptions.descDashboardNewTestResults));
		CssStyles.style(
			testResultCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(testResultCountLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardNewTestResults));
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(titleLabel);

		testResultComponent.addComponent(headerLayout);

		// Count layout
		CssLayout countLayout = testResultComponent.createCountLayout(true);
		testResultPositive = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPositive), CountElementStyle.CRITICAL);
		testResultComponent.addComponentToCountLayout(countLayout, testResultPositive);
		testResultNegative = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNegative), CountElementStyle.POSITIVE);
		testResultComponent.addComponentToCountLayout(countLayout, testResultNegative);
		testResultPending = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardPending), CountElementStyle.IMPORTANT);
		testResultComponent.addComponentToCountLayout(countLayout, testResultPending);
		testResultIndeterminate =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardIndeterminate), CountElementStyle.MINOR);
		testResultComponent.addComponentToCountLayout(countLayout, testResultIndeterminate);
		testResultComponent.addComponent(countLayout);

		return testResultComponent;
	}

	public void refresh() {
		Disease disease = this.dashboardDataProvider.getDisease();

		updateCaseComponent(disease);
		updateCaseFatalityComponent(disease);
		updateLastReportedDistrictComponent(disease);
		updateOutbreakDistrictComponent(disease);
		updateEventComponent(disease);
		updateTestResultComponent(disease);
	}

	private void updateCaseComponent(Disease disease) {
		List<DashboardCaseDto> cases = dashboardDataProvider.getCases();

		//caseDiseaseLabel.setValue("(" + disease.toString() + ")");
		caseCountLabel.setValue(Integer.toString(cases.size()).toString());

		int confirmedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
		int notACaseCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);
	}

	private void updateCaseFatalityComponent(Disease disease) {
		List<DashboardCaseDto> newCases = dashboardDataProvider.getCases();
		List<DashboardCaseDto> previousCases = dashboardDataProvider.getPreviousCases();

		int casesCount = newCases.size();
		Long fatalCasesCount = newCases.stream().filter((c) -> c.wasFatal()).count();
		long previousFatalCasesCount = previousCases.stream().filter((c) -> c.wasFatal()).count();
		long fatalCasesGrowth = fatalCasesCount - previousFatalCasesCount;
		float fatalityRate = 100 * ((float) fatalCasesCount / (float) (casesCount == 0 ? 1 : casesCount));
		fatalityRate = Math.round(fatalityRate * 100) / 100f;

		// count
		// current
		caseFatalityCountValue.setValue(fatalCasesCount.toString());
		// growth
		String chevronType = "";
		String criticalLevel = "";

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

	private void updateLastReportedDistrictComponent(Disease disease) {
		String district = dashboardDataProvider.getLastReportedDistrict();
		lastReportedDistrictLabel.setValue(DataHelper.isNullOrEmpty(district) ? I18nProperties.getString(Strings.none).toUpperCase() : district);
	}

	private void updateOutbreakDistrictComponent(Disease disease) {
		Long districtCount = dashboardDataProvider.getOutbreakDistrictCount();

		outbreakDistrictCountLabel.setValue(districtCount.toString());
	}

	private void updateEventComponent(Disease disease) {
		Map<EventStatus, Long> events = dashboardDataProvider.getEventCountByStatus();

		eventCountLabel.setValue(events.values().stream().collect(Collectors.summingLong(Long::longValue)).toString());

		eventStatusConfirmed.updateCountLabel(events.getOrDefault(EventStatus.CONFIRMED, 0L).toString());
		eventStatusPossible.updateCountLabel(events.getOrDefault(EventStatus.POSSIBLE, 0L).toString());
		eventStatusNotAnEvent.updateCountLabel(events.getOrDefault(EventStatus.NO_EVENT, 0L).toString());
	}

	private void updateTestResultComponent(Disease disease) {
		Map<PathogenTestResultType, Long> testResults = dashboardDataProvider.getTestResultCountByResultType();

		testResultCountLabel.setValue(testResults.values().stream().collect(Collectors.summingLong(Long::longValue)).toString());

		testResultPositive.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.POSITIVE, 0L).toString());
		testResultNegative.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.NEGATIVE, 0L).toString());
		testResultPending.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.PENDING, 0L).toString());
		testResultIndeterminate.updateCountLabel(testResults.getOrDefault(PathogenTestResultType.INDETERMINATE, 0L).toString());
	}
}
