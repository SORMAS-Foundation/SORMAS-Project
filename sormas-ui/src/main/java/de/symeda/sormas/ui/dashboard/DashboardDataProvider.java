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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCountType;

public class DashboardDataProvider {

	private DashboardType dashboardType;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Date fromDate;
	private Date toDate;
	private Date previousFromDate;
	private Date previousToDate;

	// overall
	private List<DiseaseBurdenDto> diseasesBurden = new ArrayList<>();

	// TODO make disease specific when contact dashboard is updated
	private List<DashboardContactDto> contacts = new ArrayList<>();
	private List<DashboardContactDto> previousContacts = new ArrayList<>();

	// disease specific
	private List<DashboardCaseDto> cases = new ArrayList<>();
	private List<DashboardCaseDto> previousCases = new ArrayList<>();
	private Long outbreakDistrictCount = 0L;
	private String lastReportedDistrict = "";
	private List<DashboardEventDto> events = new ArrayList<>();
	private List<DashboardEventDto> previousEvents = new ArrayList<>();
	private Map<PathogenTestResultType, Long> testResultCountByResultType;
	private Map<EventStatus, Long> eventCountByStatus;
	private List<DashboardTestResultDto> testResults = new ArrayList<>();
	private List<DashboardTestResultDto> previousTestResults = new ArrayList<>();
	private Map<SampleCountType, Long> sampleCount = new HashMap<SampleCountType, Long>();

	public void refreshData() {
		// Update the entities lists according to the filters
		// Disease burden
		setDiseasesBurden(FacadeProvider.getDiseaseFacade().getDiseaseBurdenForDashboard(region, district, fromDate,
				toDate, previousFromDate, previousToDate));

		this.refreshDataForSelectedDisease();
	}

	private void refreshDataForSelectedDisease () {
		// Update the entities lists according to the filters

		if (getDashboardType() == DashboardType.CONTACTS) {
			// Contacts
			setContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, fromDate,
					toDate));
			setPreviousContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease,
					previousFromDate, previousToDate));
		}
		
		if (getDashboardType() == DashboardType.SAMPLES) {
			//Samples counts
			setSampleCount(FacadeProvider.getSampleFacade().getSampleCount(region, district, disease, fromDate,
					toDate) );
		}

		if (getDashboardType() == DashboardType.CONTACTS || this.disease != null) {
			// Cases
			CaseCriteria caseCriteria = new CaseCriteria();
			caseCriteria.region(region).district(district).disease(disease).newCaseDateBetween(fromDate, toDate, NewCaseDateType.MOST_RELEVANT);
			setCases(FacadeProvider.getCaseFacade().getCasesForDashboard(caseCriteria));
			setLastReportedDistrict(FacadeProvider.getCaseFacade().getLastReportedDistrictName(caseCriteria, true, true));

			caseCriteria.newCaseDateBetween(previousFromDate, previousToDate, NewCaseDateType.MOST_RELEVANT);
			setPreviousCases(FacadeProvider.getCaseFacade().getCasesForDashboard(caseCriteria));

			if (getDashboardType() != DashboardType.CONTACTS) {
				if (getCases().size() > 0) {
					setTestResultCountByResultType(FacadeProvider.getSampleFacade().getNewTestResultCountByResultType(getCases().stream().map(c -> c.getId()).collect(Collectors.toList())));
				} else {
					setTestResultCountByResultType(new HashMap<>());
				}
			}
		}

		if (this.disease == null || getDashboardType() == DashboardType.CONTACTS) {
			return;
		}

		// Events
		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.region(region).district(district).disease(disease).reportedBetween(fromDate, toDate);	
		setEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(eventCriteria));

		eventCriteria.reportedBetween(previousFromDate, previousToDate);
		setPreviousEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(eventCriteria));

		eventCriteria.reportedBetween(fromDate, toDate);
		setEventCountByStatus(FacadeProvider.getEventFacade().getEventCountByStatus(eventCriteria));

		// Test results
		//		setTestResults(FacadeProvider.getPathogenTestFacade().getNewTestResultsForDashboard(region, district, disease,
		//				fromDate, toDate, userUuid));
		//		setPreviousTestResults(FacadeProvider.getPathogenTestFacade().getNewTestResultsForDashboard(region, district,
		//				disease, previousFromDate, previousToDate, userUuid));

		setOutbreakDistrictCount(FacadeProvider.getOutbreakFacade().getOutbreakDistrictCount(new OutbreakCriteria().region(region).district(district).disease(disease).reportedBetween(fromDate, toDate)));
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

	public Map<EventStatus, Long> getEventCountByStatus() {
		return eventCountByStatus;
	}

	public void setEventCountByStatus(Map<EventStatus, Long> events) {
		this.eventCountByStatus = events;
	}

	public Map<PathogenTestResultType, Long> getTestResultCountByResultType() {
		return testResultCountByResultType;
	}

	public void setTestResultCountByResultType(Map<PathogenTestResultType, Long> testResults) {
		this.testResultCountByResultType = testResults;
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

	public void setPreviousTestResults(List<DashboardTestResultDto> testResults) {
		this.previousTestResults = testResults;
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

	public Long getOutbreakDistrictCount() {
		return outbreakDistrictCount;
	}

	public void setOutbreakDistrictCount (Long districtCount) {
		this.outbreakDistrictCount = districtCount;
	}	

	public String getLastReportedDistrict () {
		return this.lastReportedDistrict;
	}

	public void setLastReportedDistrict (String district) {
		this.lastReportedDistrict = district;
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

	public Map<SampleCountType, Long> getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(Map<SampleCountType, Long> sampleCount) {
		this.sampleCount = sampleCount;
	}
}
