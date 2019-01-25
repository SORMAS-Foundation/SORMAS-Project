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
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsGrowthElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsPercentageElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.dashboard.statistics.SvgCircleElement;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseCarouselSurveillanceComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	
	// "Outbreak Districts" elements
	private DashboardStatisticsSubComponent outbreakDistrictComponent;
	private Label outbreakDistrictCountLabel;
	
	// "New Cases" elements
	private DashboardStatisticsSubComponent caseComponent;
	private Label caseCountLabel;
	private Label caseDiseaseLabel;
	private DashboardStatisticsCountElement caseClassificationConfirmed;
	private DashboardStatisticsCountElement caseClassificationProbable;
	private DashboardStatisticsCountElement caseClassificationSuspect;
	private DashboardStatisticsCountElement caseClassificationNotACase;
	private DashboardStatisticsCountElement caseClassificationNotYetClassified;
	
	
	// "New Events" elements
	private DashboardStatisticsSubComponent eventComponent;
	private Label eventCountLabel;
	private DashboardStatisticsCountElement eventStatusConfirmed;
	private DashboardStatisticsCountElement eventStatusPossible;
	private DashboardStatisticsCountElement eventStatusNotAnEvent;	

	// "New Test Results" elements
	private DashboardStatisticsSubComponent testResultComponent;
	private Label testResultCountLabel;
	private DashboardStatisticsCountElement testResultPositive;
	private DashboardStatisticsCountElement testResultNegative;
	private DashboardStatisticsCountElement testResultPending;
	private DashboardStatisticsCountElement testResultIndeterminate;

	
	public DiseaseCarouselSurveillanceComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		
		//layout
		setWidth(100, Unit.PERCENTAGE);
		setHeight(400, Unit.PIXELS);
		setMargin(true);
		setSpacing(false);
		setSizeFull();
		
		createOutbreakDistrictComponent();
		addComponent(outbreakDistrictComponent);
		
		createCaseComponent();
		addComponent(caseComponent);
		
		createEventComponent();
		addComponent(eventComponent);
		
		createTestResultComponent();
		addComponent(testResultComponent);
	}
	
	private void createOutbreakDistrictComponent () {
		outbreakDistrictComponent = new DashboardStatisticsSubComponent();
		
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
			//count
		outbreakDistrictCountLabel = new Label();
		CssStyles.style(outbreakDistrictCountLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XXXLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(outbreakDistrictCountLabel);
			//title
		Label titleLabel = new Label("Outbreak Districts");
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(titleLabel);
		
		outbreakDistrictComponent.addComponent(headerLayout);
	}
	
	private void createCaseComponent () {
		caseComponent = new DashboardStatisticsSubComponent();
		
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
			//count
		caseCountLabel = new Label();
		CssStyles.style(caseCountLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XXXLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(caseCountLabel);
			//title
		caseDiseaseLabel = new Label("New Cases");
		CssStyles.style(caseDiseaseLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(caseDiseaseLabel);
		
		caseComponent.addComponent(headerLayout);
		
		// Count layout
		CssLayout countLayout = caseComponent.createCountLayout(true);
		caseClassificationConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.CRITICAL);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationConfirmed);
		caseClassificationProbable = new DashboardStatisticsCountElement("Probable", CountElementStyle.IMPORTANT);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationProbable);
		caseClassificationSuspect = new DashboardStatisticsCountElement("Suspect", CountElementStyle.RELEVANT);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationSuspect);
		caseClassificationNotACase = new DashboardStatisticsCountElement("Not A Case", CountElementStyle.POSITIVE);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationNotACase);
		caseClassificationNotYetClassified = new DashboardStatisticsCountElement("Not Yet Classified", CountElementStyle.MINOR);
		caseComponent.addComponentToCountLayout(countLayout, caseClassificationNotYetClassified);
		caseComponent.addComponent(countLayout);
	}
	
	private void createEventComponent () {
		eventComponent = new DashboardStatisticsSubComponent();
		
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
			//count
		eventCountLabel = new Label();
		CssStyles.style(eventCountLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XXXLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(eventCountLabel);
			//title
		Label titleLabel = new Label("New Events");
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(titleLabel);
		
		eventComponent.addComponent(headerLayout);
		
		// Count layout
		CssLayout countLayout = eventComponent.createCountLayout(true);
		eventStatusConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.CRITICAL);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusConfirmed);
		eventStatusPossible = new DashboardStatisticsCountElement("Possible", CountElementStyle.IMPORTANT);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusPossible);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement("Not An Event", CountElementStyle.POSITIVE);
		eventComponent.addComponentToCountLayout(countLayout, eventStatusNotAnEvent);
		eventComponent.addComponent(countLayout);
	}
	
	private void createTestResultComponent () {
		testResultComponent = new DashboardStatisticsSubComponent();
		
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
			//count
		testResultCountLabel = new Label();
		CssStyles.style(testResultCountLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XXXLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(testResultCountLabel);
			//title
		Label titleLabel = new Label("New Test Results");
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(titleLabel);
		
		testResultComponent.addComponent(headerLayout);
		
		// Count layout
		CssLayout countLayout = testResultComponent.createCountLayout(true);
		testResultPositive = new DashboardStatisticsCountElement("Positive", CountElementStyle.CRITICAL);
		testResultComponent.addComponentToCountLayout(countLayout, testResultPositive);
		testResultNegative = new DashboardStatisticsCountElement("Negative", CountElementStyle.POSITIVE);
		testResultComponent.addComponentToCountLayout(countLayout, testResultNegative);
		testResultPending = new DashboardStatisticsCountElement("Pending", CountElementStyle.IMPORTANT);
		testResultComponent.addComponentToCountLayout(countLayout, testResultPending);
		testResultIndeterminate = new DashboardStatisticsCountElement("Indeterminate", CountElementStyle.MINOR);
		testResultComponent.addComponentToCountLayout(countLayout, testResultIndeterminate);
		testResultComponent.addComponent(countLayout);
	}

	public void refresh() {
//		List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
//		List<DashboardCaseDto> previousCases = dashboardDataProvider.getPreviousCases();
//		List<DashboardEventDto> events = dashboardDataProvider.getEvents();
		
		//todo: get selected disease
		Disease disease = Disease.values()[0];
		
		
//		events = events.stream().filter(c -> c.getDisease() == disease).collect(Collectors.toList());
		
		//List<DiseaseBurdenDto> diseasesBurden = new ArrayList<>();
		
		//build diseases burden
		//for (Disease disease : Disease.values()) {
//			Disease disease = Disease.values()[0];
//			
//			DiseaseBurdenDto diseaseBurden = new DiseaseBurdenDto(disease);
//			
//			List<DashboardCaseDto> _cases = cases.stream().filter(c -> c.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
//			diseaseBurden.setCaseCount(Long.valueOf(_cases.size()));
//			diseaseBurden.setOutbreakDistrictCount(_cases.stream().map((c) -> c.getDistrict().getUuid()).distinct().count());
//			diseaseBurden.setCaseDeathCount(_cases.stream().filter(c -> c.getCauseOfDeathDisease() != null).count());
//			
//			_cases = previousCases.stream().filter(c -> c.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
//			diseaseBurden.setPreviousCaseCount(Long.valueOf(_cases.size()));
//			
//			List<DashboardEventDto> _events = events.stream().filter(e -> e.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
//			diseaseBurden.setEventCount(Long.valueOf(_events.size()));
			
			//diseasesBurden.add(diseaseBurden);
		//}
		
		updateOutbreakDistrictComponent(disease);
		updateCaseComponent(disease);
		updateEventComponent(disease);
		updateTestResultComponent(disease);
	}
	
	private void updateOutbreakDistrictComponent (Disease disease) {
		List<DashboardTestResultDto> testResults = dashboardDataProvider.getTestResults();
		testResults = testResults.stream().filter(c -> c.getDisease() == disease).collect(Collectors.toList());
		
		outbreakDistrictCountLabel.setValue(Integer.toString(testResults.size()).toString());
	}
	
	private void updateCaseComponent (Disease disease) {
		List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
		cases = cases.stream().filter(c -> c.getDisease() == disease).collect(Collectors.toList());
		
		caseDiseaseLabel.setValue("New Cases (" + disease.toString() + ")");
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
	
	private void updateEventComponent (Disease disease) {
		List<DashboardEventDto> events = dashboardDataProvider.getEvents();
		events = events.stream().filter(c -> c.getDisease() == disease).collect(Collectors.toList());
		
		eventCountLabel.setValue(Integer.toString(events.size()).toString());
		
		int confirmedEventsCount = (int) events.stream().filter(e -> e.getEventStatus() == EventStatus.CONFIRMED).count();
		eventStatusConfirmed.updateCountLabel(confirmedEventsCount);
		int possibleEventsCount = (int) events.stream().filter(e -> e.getEventStatus() == EventStatus.POSSIBLE).count();
		eventStatusPossible.updateCountLabel(possibleEventsCount);
		int notAnEventEventsCount = (int) events.stream().filter(e -> e.getEventStatus() == EventStatus.NO_EVENT).count();
		eventStatusNotAnEvent.updateCountLabel(notAnEventEventsCount);
	}
	
	private void updateTestResultComponent (Disease disease) {
		List<DashboardTestResultDto> testResults = dashboardDataProvider.getTestResults();
		testResults = testResults.stream().filter(c -> c.getDisease() == disease).collect(Collectors.toList());
		
		testResultCountLabel.setValue(Integer.toString(testResults.size()).toString());
		
		int positiveTestResultsCount = (int) testResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE).count();
		testResultPositive.updateCountLabel(positiveTestResultsCount);
		int negativeTestResultsCount = (int) testResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.NEGATIVE).count();
		testResultNegative.updateCountLabel(negativeTestResultsCount);
		int pendingTestResultsCount = (int) testResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.PENDING).count();
		testResultPending.updateCountLabel(pendingTestResultsCount);
		int indeterminateTestResultsCount = (int) testResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.INDETERMINATE).count();
		testResultIndeterminate.updateCountLabel(indeterminateTestResultsCount);
	}
}
