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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsDiseaseElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsGrowthElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsPercentageElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.dashboard.statistics.SvgCircleElement;
import de.symeda.sormas.ui.dashboard.statistics.SvgCircleElement.SvgCircleElementPart;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardSurveillanceStatisticsSubComponent extends AbstractDashboardStatisticsComponent {
	
	// "My Tasks" elements
	private DashboardStatisticsCountElement taskPriorityHigh;
	private DashboardStatisticsCountElement taskPriorityNormal;
	private DashboardStatisticsCountElement taskPriorityLow;
	private DashboardStatisticsCountElement taskStatusPending;
	private DashboardStatisticsCountElement taskStatusDone;
	private DashboardStatisticsPercentageElement taskStatusPendingPercentage;
	private DashboardStatisticsPercentageElement taskStatusDonePercentage;
	private DashboardStatisticsPercentageElement taskStatusRemovedPercentage;
	private DashboardStatisticsPercentageElement taskStatusNotExecutablePercentage;
	private SvgCircleElement taskStatusCircleGraph;

	// "New Cases" elements
	private DashboardStatisticsCountElement caseClassificationConfirmed;
	private DashboardStatisticsCountElement caseClassificationProbable;
	private DashboardStatisticsCountElement caseClassificationSuspect;
	private DashboardStatisticsCountElement caseClassificationNotACase;
	private DashboardStatisticsCountElement caseClassificationNotYetClassified;
	private DashboardStatisticsGrowthElement caseInvestigationStatusDone;
	private DashboardStatisticsGrowthElement caseInvestigationStatusDiscarded;
	private DashboardStatisticsGrowthElement caseFatalities;
	private Label caseFatalityRateLabel;
	private Label caseFatalityRateCaption;
	
	// "New Events" elements
	private DashboardStatisticsCountElement eventStatusConfirmed;
	private DashboardStatisticsCountElement eventStatusPossible;
	private DashboardStatisticsCountElement eventStatusNotAnEvent;	
	private SvgCircleElement eventTypeRumorCircleGraph;
	private SvgCircleElement eventTypeOutbreakCircleGraph;
	private DashboardStatisticsPercentageElement eventStatusConfirmedPercentage;
	private DashboardStatisticsPercentageElement eventStatusPossiblePercentage;
	private DashboardStatisticsPercentageElement eventStatusNotAnEventPercentage;

	// "New Test Results" elements
	private DashboardStatisticsCountElement testResultPositive;
	private DashboardStatisticsCountElement testResultNegative;
	private DashboardStatisticsCountElement testResultPending;
	private DashboardStatisticsCountElement testResultIndeterminate;
	private SvgCircleElement testResultShippedCircleGraph;
	private SvgCircleElement testResultReceivedCircleGraph;
	private DashboardStatisticsPercentageElement testResultPositivePercentage;
	private DashboardStatisticsPercentageElement testResultNegativePercentage;
	private DashboardStatisticsPercentageElement testResultPendingPercentage;
	private DashboardStatisticsPercentageElement testResultIndeterminatePercentage;

	public DashboardSurveillanceStatisticsSubComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected void addFirstComponent() {
		firstComponent = new DashboardStatisticsSubComponent();

		// Header
		firstComponent.addHeader("My Tasks", null, true);

		// Count layout
		CssLayout countLayout = firstComponent.createCountLayout(true);
		taskPriorityHigh = new DashboardStatisticsCountElement("High", CountElementStyle.CRITICAL);
		firstComponent.addComponentToCountLayout(countLayout, taskPriorityHigh);
		taskPriorityNormal = new DashboardStatisticsCountElement("Normal", CountElementStyle.NEUTRAL);
		firstComponent.addComponentToCountLayout(countLayout, taskPriorityNormal);
		taskPriorityLow = new DashboardStatisticsCountElement("Low", CountElementStyle.MINOR);
		firstComponent.addComponentToCountLayout(countLayout, taskPriorityLow);

		Label separator = new Label();
		separator.setHeight(100, Unit.PERCENTAGE);
		CssStyles.style(separator, CssStyles.VR);
		firstComponent.addComponentToCountLayout(countLayout, separator);

		taskStatusPending = new DashboardStatisticsCountElement("Pending", CountElementStyle.IMPORTANT);
		firstComponent.addComponentToCountLayout(countLayout, taskStatusPending);
		taskStatusDone = new DashboardStatisticsCountElement("Done", CountElementStyle.POSITIVE);
		firstComponent.addComponentToCountLayout(countLayout, taskStatusDone);
		firstComponent.addComponent(countLayout);

		// Content
		firstComponent.addTwoColumnsMainContent(false, 60);
		taskStatusPendingPercentage = new DashboardStatisticsPercentageElement("Pending", CssStyles.SVG_FILL_IMPORTANT);
		firstComponent.addComponentToLeftContentColumn(taskStatusPendingPercentage);
		taskStatusDonePercentage = new DashboardStatisticsPercentageElement("Done", CssStyles.SVG_FILL_POSITIVE);
		firstComponent.addComponentToLeftContentColumn(taskStatusDonePercentage);
		taskStatusRemovedPercentage = new DashboardStatisticsPercentageElement("Removed", CssStyles.SVG_FILL_CRITICAL);
		firstComponent.addComponentToLeftContentColumn(taskStatusRemovedPercentage);
		taskStatusNotExecutablePercentage = new DashboardStatisticsPercentageElement("Not Executable", CssStyles.SVG_FILL_MINOR);
		firstComponent.addComponentToLeftContentColumn(taskStatusNotExecutablePercentage);

		taskStatusCircleGraph = new SvgCircleElement(false);
		firstComponent.addComponentToRightContentColumn(taskStatusCircleGraph);

		subComponentsLayout.addComponent(firstComponent, FIRST_LOC);
	}

	@Override
	protected void updateFirstComponent(int visibleDiseasesCount) {
		List<DashboardTaskDto> dashboardTaskDtos = dashboardDataProvider.getTasks();
		List<DashboardTaskDto> dashboardPendingTasks = dashboardDataProvider.getPendingTasks();

		int pendingTasksCount = dashboardPendingTasks.size();		
		firstComponent.updateCountLabel(pendingTasksCount);

		int highPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count();
		taskPriorityHigh.updateCountLabel(highPriorityCount);
		int normalPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.NORMAL).count();
		taskPriorityNormal.updateCountLabel(normalPriorityCount);
		int lowPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.LOW).count();
		taskPriorityLow.updateCountLabel(lowPriorityCount);

		int doneCount = (int) dashboardTaskDtos.stream().filter(t -> t.getTaskStatus() == TaskStatus.DONE).count();
		int removedCount = (int) dashboardTaskDtos.stream().filter(t -> t.getTaskStatus() == TaskStatus.REMOVED).count();
		int notExecutableCount = (int) dashboardTaskDtos.stream().filter(t -> t.getTaskStatus() == TaskStatus.NOT_EXECUTABLE).count();
		int totalCount = pendingTasksCount + doneCount + removedCount + notExecutableCount;
		int pendingPercentage = totalCount == 0 ? 0 : 
			new BigDecimal(pendingTasksCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int donePercentage = totalCount == 0 ? 0 :
			new BigDecimal(doneCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int removedPercentage = totalCount == 0 ? 0 :
			new BigDecimal(removedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int notExecutablePercentage = totalCount == 0 ? 0 :
			new BigDecimal(notExecutableCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();

		taskStatusPending.updateCountLabel(pendingTasksCount);
		taskStatusDone.updateCountLabel(doneCount);

		taskStatusPendingPercentage.updatePercentageValue(pendingPercentage);
		taskStatusDonePercentage.updatePercentageValue(donePercentage);
		taskStatusRemovedPercentage.updatePercentageValue(removedPercentage);
		taskStatusNotExecutablePercentage.updatePercentageValue(notExecutablePercentage);

		SvgCircleElementPart pendingPart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_IMPORTANT, pendingPercentage);
		SvgCircleElementPart donePart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_POSITIVE, donePercentage);
		SvgCircleElementPart removedPart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_CRITICAL, removedPercentage);
		SvgCircleElementPart notExecutablePart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_MINOR, notExecutablePercentage);
		taskStatusCircleGraph.updateSvg(pendingPart, donePart, removedPart, notExecutablePart);
	}

	@Override
	protected void addSecondComponent() {
		secondComponent = new DashboardStatisticsSubComponent();

		// Header
		secondComponent.addHeader("New Cases", null, true);

		// Count layout
		CssLayout countLayout = secondComponent.createCountLayout(true);
		caseClassificationConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.CRITICAL);
		secondComponent.addComponentToCountLayout(countLayout, caseClassificationConfirmed);
		caseClassificationProbable = new DashboardStatisticsCountElement("Probable", CountElementStyle.IMPORTANT);
		secondComponent.addComponentToCountLayout(countLayout, caseClassificationProbable);
		caseClassificationSuspect = new DashboardStatisticsCountElement("Suspect", CountElementStyle.RELEVANT);
		secondComponent.addComponentToCountLayout(countLayout, caseClassificationSuspect);
		caseClassificationNotACase = new DashboardStatisticsCountElement("Not A Case", CountElementStyle.POSITIVE);
		secondComponent.addComponentToCountLayout(countLayout, caseClassificationNotACase);
		caseClassificationNotYetClassified = new DashboardStatisticsCountElement("Not Yet Classified", CountElementStyle.MINOR);
		secondComponent.addComponentToCountLayout(countLayout, caseClassificationNotYetClassified);
		secondComponent.addComponent(countLayout);

		// Content
		secondComponent.addMainContent();
		caseInvestigationStatusDone = new DashboardStatisticsGrowthElement("Investigated", CssStyles.LABEL_SECONDARY, Alignment.MIDDLE_LEFT);
		caseInvestigationStatusDiscarded = new DashboardStatisticsGrowthElement("Discarded", CssStyles.LABEL_SECONDARY, Alignment.MIDDLE_LEFT);
		caseFatalities = new DashboardStatisticsGrowthElement("Fatalities", CssStyles.LABEL_CRITICAL, Alignment.MIDDLE_RIGHT);
		caseFatalityRateLabel = new Label();
		CssStyles.style(caseFatalityRateLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_BOLD);
		caseFatalityRateCaption = new Label("Case Fatality Rate");
		CssStyles.style(caseFatalityRateCaption, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_BOLD);

		subComponentsLayout.addComponent(secondComponent, SECOND_LOC);
	}

	@Override
	protected void updateSecondComponent(int visibleDiseasesCount) {
		List<DashboardCaseDto> dashboardCaseDtos = dashboardDataProvider.getCases();
		List<DashboardCaseDto> previousDashboardCases = dashboardDataProvider.getPreviousCases();

		int newCasesCount = dashboardCaseDtos.size();
		secondComponent.updateCountLabel(newCasesCount);

		int confirmedCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
		int notACaseCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			secondComponent.removeAllComponentsFromContent();
			secondComponent.addComponentToContent(caseInvestigationStatusDone);
			secondComponent.addComponentToContent(caseInvestigationStatusDiscarded);
			Label separator = new Label("<hr/>", ContentMode.HTML);
			secondComponent.addComponentToContent(separator);
			HorizontalLayout fatalityLayout = new HorizontalLayout();
			fatalityLayout.setWidth(100, Unit.PERCENTAGE);
			VerticalLayout fatalityRateLayout = new VerticalLayout();
			fatalityRateLayout.addComponent(caseFatalityRateLabel);
			HorizontalLayout fatalityCaptionLayout = new HorizontalLayout();
			fatalityCaptionLayout.addComponent(caseFatalityRateCaption);
			Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			infoLabel.setSizeUndefined();
			infoLabel.setDescription("The fatality rate is calculated based on the number of confirmed, suspect and probable cases.");
			CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.HSPACE_LEFT_4, "cfr-info-button", CssStyles.LABEL_SECONDARY);
			fatalityCaptionLayout.addComponent(infoLabel);
			fatalityCaptionLayout.setComponentAlignment(infoLabel, Alignment.TOP_RIGHT);
			fatalityRateLayout.addComponent(fatalityCaptionLayout);
			fatalityLayout.addComponent(fatalityRateLayout);
			fatalityLayout.addComponent(caseFatalities);
			secondComponent.addComponentToContent(fatalityLayout);
		}

		if (currentDisease == null) {
			// Remove all children of the content layout
			secondComponent.removeAllComponentsFromContent();

			// Create a map with all diseases as keys and their respective case counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardCaseDtos.stream().filter(c -> c.getDisease() == disease).count());
			}

			// Create a list from this map that sorts the entries by case counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);

			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by case count
			for (int i = 0; i < visibleDiseasesCount; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardCases.stream().filter(c -> c.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement = new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				secondComponent.addComponentToContent(diseaseElement);
			}
		} else {
			int investigatedCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DONE).count();
			int previousInvestigatedCasesCount = (int) previousDashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DONE).count();
			int discardedCasesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DISCARDED).count();
			int previousDiscardedCasesCount = (int) previousDashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DISCARDED).count();
			int fatalitiesCount = (int) dashboardCaseDtos.stream().filter(c -> c.getCasePersonCondition() == PresentCondition.DEAD && 
					c.getCauseOfDeathDisease() != null && c.getCauseOfDeathDisease() == c.getDisease()).count();
			int previousFatalitiesCount = (int) previousDashboardCases.stream().filter(c -> c.getCasePersonCondition() == PresentCondition.DEAD).count();
			
			int investigatedCasesGrowth = calculateGrowth(investigatedCasesCount, previousInvestigatedCasesCount);
			int discardedCasesGrowth = calculateGrowth(discardedCasesCount, previousDiscardedCasesCount);
			int fatalitiesGrowth = calculateGrowth(fatalitiesCount, previousFatalitiesCount);
			int fatalityRateRelevantCasesCount = confirmedCasesCount + suspectCasesCount + probableCasesCount;
			float fatalityRate = fatalitiesCount == 0 ? 0 : newCasesCount == 0 ? 0 : fatalityRateRelevantCasesCount == 0 ? 0 :
				new BigDecimal(fatalitiesCount).multiply(new BigDecimal(100)).divide(new BigDecimal(fatalityRateRelevantCasesCount), 1, RoundingMode.HALF_UP).floatValue();
			
			caseInvestigationStatusDone.update(investigatedCasesCount, investigatedCasesGrowth, true);
			caseInvestigationStatusDiscarded.update(discardedCasesCount, discardedCasesGrowth, false);
			caseFatalities.update(fatalitiesCount, fatalitiesGrowth, false);
			caseFatalityRateLabel.setValue("CFR " + (fatalityRate % 1.0 != 0 ? String.format("%s", Float.toString(fatalityRate)) + "%" : String.format("%.0f", fatalityRate) + "%"));
		}
	}

	@Override
	protected void addThirdComponent() {
		thirdComponent = new DashboardStatisticsSubComponent();

		// Header
		thirdComponent.addHeader("New Events", null, true);

		// Count layout
		CssLayout countLayout = thirdComponent.createCountLayout(true);
		eventStatusConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.CRITICAL);
		thirdComponent.addComponentToCountLayout(countLayout, eventStatusConfirmed);
		eventStatusPossible = new DashboardStatisticsCountElement("Possible", CountElementStyle.IMPORTANT);
		thirdComponent.addComponentToCountLayout(countLayout, eventStatusPossible);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement("Not An Event", CountElementStyle.POSITIVE);
		thirdComponent.addComponentToCountLayout(countLayout, eventStatusNotAnEvent);
		thirdComponent.addComponent(countLayout);

		// Content
		thirdComponent.addMainContent();
		eventTypeRumorCircleGraph = new SvgCircleElement(true);
		eventTypeOutbreakCircleGraph = new SvgCircleElement(true);
		eventStatusConfirmedPercentage = new DashboardStatisticsPercentageElement("Confirmed", CssStyles.SVG_FILL_CRITICAL);
		eventStatusPossiblePercentage = new DashboardStatisticsPercentageElement("Possible", CssStyles.SVG_FILL_IMPORTANT);
		eventStatusNotAnEventPercentage = new DashboardStatisticsPercentageElement("Not An Event", CssStyles.SVG_FILL_MINOR);

		subComponentsLayout.addComponent(thirdComponent, THIRD_LOC);
	}

	@Override
	protected void updateThirdComponent(int visibleDiseasesCount) {
		List<DashboardEventDto> dashboardEventDtos = dashboardDataProvider.getEvents();
		List<DashboardEventDto> previousDashboardEvents = dashboardDataProvider.getPreviousEvents();

		int newEventsCount = dashboardEventDtos.size();
		thirdComponent.updateCountLabel(newEventsCount);

		int confirmedEventsCount = (int) dashboardEventDtos.stream().filter(e -> e.getEventStatus() == EventStatus.CONFIRMED).count();
		eventStatusConfirmed.updateCountLabel(confirmedEventsCount);
		int possibleEventsCount = (int) dashboardEventDtos.stream().filter(e -> e.getEventStatus() == EventStatus.POSSIBLE).count();
		eventStatusPossible.updateCountLabel(possibleEventsCount);
		int notAnEventEventsCount = (int) dashboardEventDtos.stream().filter(e -> e.getEventStatus() == EventStatus.NO_EVENT).count();
		eventStatusNotAnEvent.updateCountLabel(notAnEventEventsCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			thirdComponent.removeContent();
			if (currentDisease != null) {
				thirdComponent.addTwoColumnsMainContent(true, 20);
				thirdComponent.addComponentToLeftContentColumn(eventTypeRumorCircleGraph);
				Label rumorCircleLabel = new Label("Rumor");
				rumorCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(rumorCircleLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				thirdComponent.addComponentToLeftContentColumn(rumorCircleLabel);
				thirdComponent.addComponentToLeftContentColumn(eventTypeOutbreakCircleGraph);
				Label outbreakCircleLabel = new Label("Outbreak");
				outbreakCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(outbreakCircleLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				thirdComponent.addComponentToLeftContentColumn(outbreakCircleLabel);
				thirdComponent.addComponentToRightContentColumn(eventStatusConfirmedPercentage);
				thirdComponent.addComponentToRightContentColumn(eventStatusPossiblePercentage);
				thirdComponent.addComponentToRightContentColumn(eventStatusNotAnEventPercentage);
			} else {
				thirdComponent.addMainContent();
			}
		}

		if (currentDisease == null) {
			// Remove all children of the content layout
			thirdComponent.removeAllComponentsFromContent();
	
			// Create a map with all diseases as keys and their respective event counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardEventDtos.stream().filter(e -> e.getDisease() == disease).count());
			}
	
			// Create a list from this map that sorts the entries by event counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);
	
			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by event count
			for (int i = 0; i < visibleDiseasesCount; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardEvents.stream().filter(e -> e.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement = new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				thirdComponent.addComponentToContent(diseaseElement);
			}
		} else {
			int rumorsCount = (int) dashboardEventDtos.stream().filter(e -> e.getEventType() == EventType.RUMOR).count();
			int outbreaksCount = (int) dashboardEventDtos.stream().filter(e -> e.getEventType() == EventType.OUTBREAK).count();
			int totalTypeCount = rumorsCount + outbreaksCount;
			int rumorsPercentage = totalTypeCount == 0 ? 0 : 
				new BigDecimal(rumorsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalTypeCount), RoundingMode.HALF_UP).intValue();
			int outbreaksPercentage = totalTypeCount == 0 ? 0 :
				new BigDecimal(outbreaksCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalTypeCount), RoundingMode.HALF_UP).intValue();
			int totalStatusCount = confirmedEventsCount + possibleEventsCount + notAnEventEventsCount;
			int confirmedEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(confirmedEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			int possibleEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(possibleEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			int notAnEventEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(notAnEventEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			
			SvgCircleElementPart rumorsPart = eventTypeRumorCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_IMPORTANT, rumorsPercentage);
			SvgCircleElementPart rumorsBackgroundPart = eventTypeRumorCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - rumorsPercentage);
			eventTypeRumorCircleGraph.updateSvg(rumorsPercentage, rumorsPart, rumorsBackgroundPart);
			SvgCircleElementPart outbreaksPart = eventTypeOutbreakCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_CRITICAL, outbreaksPercentage);
			SvgCircleElementPart outbreaksBackgroundPart = eventTypeOutbreakCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - outbreaksPercentage);
			eventTypeOutbreakCircleGraph.updateSvg(outbreaksPercentage, outbreaksPart, outbreaksBackgroundPart);
			eventStatusConfirmedPercentage.updatePercentageValue(confirmedEventsPercentage);
			eventStatusPossiblePercentage.updatePercentageValue(possibleEventsPercentage);
			eventStatusNotAnEventPercentage.updatePercentageValue(notAnEventEventsPercentage);
		}
	}

	@Override
	protected void addFourthComponent() {
		fourthComponent = new DashboardStatisticsSubComponent();

		// Header
		fourthComponent.addHeader("New Test Results", null, true);

		// Count layout
		CssLayout countLayout = fourthComponent.createCountLayout(true);
		testResultPositive = new DashboardStatisticsCountElement("Positive", CountElementStyle.CRITICAL);
		fourthComponent.addComponentToCountLayout(countLayout, testResultPositive);
		testResultNegative = new DashboardStatisticsCountElement("Negative", CountElementStyle.POSITIVE);
		fourthComponent.addComponentToCountLayout(countLayout, testResultNegative);
		testResultPending = new DashboardStatisticsCountElement("Pending", CountElementStyle.IMPORTANT);
		fourthComponent.addComponentToCountLayout(countLayout, testResultPending);
		testResultIndeterminate = new DashboardStatisticsCountElement("Indeterminate", CountElementStyle.MINOR);
		fourthComponent.addComponentToCountLayout(countLayout, testResultIndeterminate);
		fourthComponent.addComponent(countLayout);

		// Content
		fourthComponent.addMainContent();
		testResultShippedCircleGraph = new SvgCircleElement(true);
		testResultReceivedCircleGraph = new SvgCircleElement(true);
		testResultPositivePercentage = new DashboardStatisticsPercentageElement("Positive", CssStyles.SVG_FILL_CRITICAL);
		testResultNegativePercentage = new DashboardStatisticsPercentageElement("Negative", CssStyles.SVG_FILL_POSITIVE);
		testResultPendingPercentage = new DashboardStatisticsPercentageElement("Pending", CssStyles.SVG_FILL_IMPORTANT);
		testResultIndeterminatePercentage = new DashboardStatisticsPercentageElement("Indeterminate", CssStyles.SVG_FILL_MINOR);

		subComponentsLayout.addComponent(fourthComponent, FOURTH_LOC);
	}

	@Override
	protected void updateFourthComponent(int visibleDiseasesCount) {
		List<DashboardTestResultDto> dashboardTestResultDtos = dashboardDataProvider.getTestResults();
		List<DashboardTestResultDto> previousDashboardTestResults = dashboardDataProvider.getPreviousTestResults();

		int newTestResultsCount = dashboardTestResultDtos.size();
		fourthComponent.updateCountLabel(newTestResultsCount);

		int positiveTestResultsCount = (int) dashboardTestResultDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE).count();
		testResultPositive.updateCountLabel(positiveTestResultsCount);
		int negativeTestResultsCount = (int) dashboardTestResultDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.NEGATIVE).count();
		testResultNegative.updateCountLabel(negativeTestResultsCount);
		int pendingTestResultsCount = (int) dashboardTestResultDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.PENDING).count();
		testResultPending.updateCountLabel(pendingTestResultsCount);
		int indeterminateTestResultsCount = (int) dashboardTestResultDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.INDETERMINATE).count();
		testResultIndeterminate.updateCountLabel(indeterminateTestResultsCount);

		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			fourthComponent.removeContent();
			if (currentDisease != null) {
				fourthComponent.addTwoColumnsMainContent(true, 20);
				fourthComponent.addComponentToLeftContentColumn(testResultShippedCircleGraph);
				Label shippedCircleLabel = new Label("Shipped");
				shippedCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(shippedCircleLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				fourthComponent.addComponentToLeftContentColumn(shippedCircleLabel);
				fourthComponent.addComponentToLeftContentColumn(testResultReceivedCircleGraph);
				Label receivedCircleLabel = new Label("Received");
				receivedCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(receivedCircleLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				fourthComponent.addComponentToLeftContentColumn(receivedCircleLabel);
				fourthComponent.addComponentToRightContentColumn(testResultPositivePercentage);
				fourthComponent.addComponentToRightContentColumn(testResultNegativePercentage);
				fourthComponent.addComponentToRightContentColumn(testResultPendingPercentage);
				fourthComponent.addComponentToRightContentColumn(testResultIndeterminatePercentage);
			} else {
				fourthComponent.addMainContent();
			}
		}
		
		if (currentDisease == null) {
			// Remove all children of the content layout
			fourthComponent.removeAllComponentsFromContent();
	
			// Create a map with all diseases as keys and their respective positive test result counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardTestResultDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE && r.getDisease() == disease).count());
			}
	
			// Create a list from this map that sorts the entries by test result counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);
	
			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by test result count
			for (int i = 0; i < visibleDiseasesCount; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardTestResults.stream().filter(r -> r.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement = new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				fourthComponent.addComponentToContent(diseaseElement);
			}
		} else {
			List<DashboardSampleDto> dashboardSampleDtos = dashboardDataProvider.getSamples();
			int newSamplesCount = dashboardSampleDtos.size();
			
			int shippedCount = (int) dashboardSampleDtos.stream().filter(s -> s.isShipped()).count();
			int receivedCount = (int) dashboardSampleDtos.stream().filter(s -> s.isReceived()).count();
			int shippedPercentage = newSamplesCount == 0 ? 0 :
				new BigDecimal(shippedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newSamplesCount), RoundingMode.HALF_UP).intValue();
			int receivedPercentage = newSamplesCount == 0 ? 0 :
				new BigDecimal(receivedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newSamplesCount), RoundingMode.HALF_UP).intValue();
			int positiveTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(positiveTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int negativeTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(negativeTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int pendingTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(pendingTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int indeterminateTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(indeterminateTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			
			SvgCircleElementPart shippedPart = testResultShippedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_PRIMARY, shippedPercentage);
			SvgCircleElementPart shippedBackgroundPart = testResultShippedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - shippedPercentage);
			testResultShippedCircleGraph.updateSvg(shippedPercentage, shippedPart, shippedBackgroundPart);
			SvgCircleElementPart receivedPart = testResultReceivedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_PRIMARY, receivedPercentage);
			SvgCircleElementPart receivedBackgroundPart = testResultReceivedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - receivedPercentage);
			testResultReceivedCircleGraph.updateSvg(receivedPercentage, receivedPart, receivedBackgroundPart);
			testResultPositivePercentage.updatePercentageValue(positiveTestResultsPercentage);
			testResultNegativePercentage.updatePercentageValue(negativeTestResultsPercentage);
			testResultPendingPercentage.updatePercentageValue(pendingTestResultsPercentage);
			testResultIndeterminatePercentage.updatePercentageValue(indeterminateTestResultsPercentage);
		}
	}
	
	@Override
	protected int getFullHeight() {
		return 430;
	}
	
	@Override
	protected int getNormalHeight() {
		return 320;
	}

	@Override
	protected int getFilteredHeight() {
		return 370;
	}
	
}