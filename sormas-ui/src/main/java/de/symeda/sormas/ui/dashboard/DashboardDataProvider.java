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
package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.outbreak.DashboardOutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;

public class DashboardDataProvider {

	private DashboardType dashboardType;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOption dateFilterOption;
	private Date fromDate;
	private Date toDate;
	private Date previousFromDate;
	private Date previousToDate;

	private List<DashboardCaseDto> cases = new ArrayList<>();
	private List<DashboardCaseDto> previousCases = new ArrayList<>();
	private List<DashboardEventDto> events = new ArrayList<>();
	private List<DashboardEventDto> previousEvents = new ArrayList<>();
	private List<DashboardTestResultDto> testResults = new ArrayList<>();
	private List<DashboardTestResultDto> previousTestResults = new ArrayList<>();
	private List<DashboardSampleDto> samples = new ArrayList<>();
	private List<DashboardTaskDto> tasks = new ArrayList<>();
	private List<DashboardTaskDto> pendingTasks = new ArrayList<>();
	private List<DashboardContactDto> contacts = new ArrayList<>();
	private List<DashboardContactDto> previousContacts = new ArrayList<>();
	private List<DiseaseBurdenDto> diseasesBurden = new ArrayList<>();
	private List<DashboardOutbreakDto> outbreaks = new ArrayList<>();

	public void refreshData() {
		// Update the entities lists according to the filters
		String userUuid = UserProvider.getCurrent().getUuid();

		int period = DateHelper.getDaysBetween(fromDate, toDate);
		previousFromDate = DateHelper.getStartOfDay(DateHelper.subtractDays(fromDate, period));
		previousToDate = DateHelper.getEndOfDay(DateHelper.subtractDays(toDate, period));

		// Samples
		setSamples(FacadeProvider.getSampleFacade().getNewSamplesForDashboard(region, district, disease, fromDate,
				toDate, userUuid));
		// Tasks
		setTasks(FacadeProvider.getTaskFacade().getAllByUserForDashboard(null,
				DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(new Date())),
				DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(new Date())), userUuid));
		setPendingTasks(
				FacadeProvider.getTaskFacade().getAllByUserForDashboard(TaskStatus.PENDING, null, null, userUuid));
		
		// Contacts
		setContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, fromDate,
				toDate, userUuid));
		setPreviousContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease,
				previousFromDate, previousToDate, userUuid));
		
		// Disease burden
		setDiseasesBurden(FacadeProvider.getDiseaseFacade().getDiseaseBurdenForDashboard(region, district, fromDate,
				toDate, previousFromDate, previousToDate, userUuid));
		
		if (this.disease != null)
			this.refreshDataForSelectedDisease();
	}

	private void refreshDataForSelectedDisease () {
		// Update the entities lists according to the filters
		String userUuid = UserProvider.getCurrent().getUuid();

		int period = DateHelper.getDaysBetween(fromDate, toDate);
		previousFromDate = DateHelper.getStartOfDay(DateHelper.subtractDays(fromDate, period));
		previousToDate = DateHelper.getEndOfDay(DateHelper.subtractDays(toDate, period));
		
		// Cases
		setCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(region, district, disease, fromDate, toDate,
				userUuid));
		setPreviousCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(region, district, disease,
				previousFromDate, previousToDate, userUuid));
		
		// Events
		setEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(region, district, disease, fromDate, toDate,
				userUuid));
		setPreviousEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(region, district, disease,
				previousFromDate, previousToDate, userUuid));
		
		// Test results
		setTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(region, district, disease,
				fromDate, toDate, userUuid));
		setPreviousTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(region, district,
				disease, previousFromDate, previousToDate, userUuid));
		
		// Outbreaks
		setOutbreaks(FacadeProvider.getOutbreakFacade().getOutbreaksForDashboard(region, district, disease, fromDate, toDate,
				userUuid));
	}
	
	public List<DashboardCaseDto> getCases() {
		return cases;
	}

	public void setCases(List<DashboardCaseDto> cases) {
		this.cases = cases;
	}

	public List<DashboardCaseDto> getPreviousCases() {
		return previousCases;
	}

	public void setPreviousCases(List<DashboardCaseDto> previousCases) {
		this.previousCases = previousCases;
	}

	public List<DashboardEventDto> getEvents() {
		return events;
	}

	public void setEvents(List<DashboardEventDto> events) {
		this.events = events;
	}

	public List<DashboardEventDto> getPreviousEvents() {
		return previousEvents;
	}

	public void setPreviousEvents(List<DashboardEventDto> previousEvents) {
		this.previousEvents = previousEvents;
	}

	public List<DashboardTestResultDto> getTestResults() {
		return testResults;
	}

	public void setTestResults(List<DashboardTestResultDto> testResults) {
		this.testResults = testResults;
	}

	public List<DashboardTestResultDto> getPreviousTestResults() {
		return previousTestResults;
	}

	public void setPreviousTestResults(List<DashboardTestResultDto> previousTestResults) {
		this.previousTestResults = previousTestResults;
	}

	public List<DashboardSampleDto> getSamples() {
		return samples;
	}

	public void setSamples(List<DashboardSampleDto> samples) {
		this.samples = samples;
	}

	public List<DashboardTaskDto> getTasks() {
		return tasks;
	}

	public void setTasks(List<DashboardTaskDto> tasks) {
		this.tasks = tasks;
	}

	public List<DashboardTaskDto> getPendingTasks() {
		return pendingTasks;
	}

	public void setPendingTasks(List<DashboardTaskDto> pendingTasks) {
		this.pendingTasks = pendingTasks;
	}

	public List<DashboardContactDto> getContacts() {
		return contacts;
	}

	public void setContacts(List<DashboardContactDto> contacts) {
		this.contacts = contacts;
	}

	public List<DashboardContactDto> getPreviousContacts() {
		return previousContacts;
	}

	public void setPreviousContacts(List<DashboardContactDto> previousContacts) {
		this.previousContacts = previousContacts;
	}

	public List<DiseaseBurdenDto> getDiseasesBurden() {
		return diseasesBurden;
	}

	public void setDiseasesBurden(List<DiseaseBurdenDto> diseasesBurden) {
		this.diseasesBurden = diseasesBurden;
	}

	public List<DashboardOutbreakDto> getOutbreaks() {
		return outbreaks;
	}

	public void setOutbreaks(List<DashboardOutbreakDto> outbreaks) {
		this.outbreaks = outbreaks;
	}	

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
		
		this.refreshDataForSelectedDisease();
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getPreviousFromDate() {
		return previousFromDate;
	}

	public void setPreviousFromDate(Date previousFromDate) {
		this.previousFromDate = previousFromDate;
	}

	public Date getPreviousToDate() {
		return previousToDate;
	}

	public void setPreviousToDate(Date previousToDate) {
		this.previousToDate = previousToDate;
	}

	public DashboardType getDashboardType() {
		return dashboardType;
	}

	public void setDashboardType(DashboardType dashboardType) {
		this.dashboardType = dashboardType;
	}

}
