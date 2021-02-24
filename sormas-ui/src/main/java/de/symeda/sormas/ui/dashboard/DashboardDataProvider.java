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
package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;

// FIXME: 06/08/2020 this should be refactored into two specific data providers for case and contact dashboards
public class DashboardDataProvider {

	private DashboardType dashboardType;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Date fromDate;
	private Date toDate;
	private Date previousFromDate;
	private Date previousToDate;

	private NewCaseDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;

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

	private Long contactsInQuarantineCount = 0L;
	private Long contactsPlacedInQuarantineCount = 0L;
	private Long casesInQuarantineCount = 0L;
	private Long casesPlacedInQuarantineCount = 0L;
	private Long contactsConvertedToCaseCount = 0L;

	public void refreshData() {

		// Update the entities lists according to the filters
		// Disease burden
		setDiseasesBurden(
			FacadeProvider.getDiseaseFacade()
				.getDiseaseBurdenForDashboard(region, district, fromDate, toDate, previousFromDate, previousToDate, newCaseDateType));

		this.refreshDataForSelectedDisease();
	}

	private void refreshDataForQuarantinedContacts() {

		List<DashboardQuarantineDataDto> contactsInQuarantineDtos =
			FacadeProvider.getContactFacade().getQuarantineDataForDashBoard(region, district, disease, fromDate, toDate);

		setContactsInQuarantineCount((long) contactsInQuarantineDtos.size());

		Long dashboardContactsPlacedInQuarantineCount = getPlacedInQuarantine(contactsInQuarantineDtos);

		setContactsPlacedInQuarantineCount(dashboardContactsPlacedInQuarantineCount);
	}

	private void refreshDataForQuarantinedCases() {

		List<DashboardQuarantineDataDto> casesInQuarantineDtos =
			FacadeProvider.getCaseFacade().getQuarantineDataForDashBoard(region, district, disease, fromDate, toDate);

		setCasesInQuarantineCount((long) casesInQuarantineDtos.size());

		Long dashboardCasesPlacedInQuarantineCount = getPlacedInQuarantine(casesInQuarantineDtos);

		setCasesPlacedInQuarantineCount(dashboardCasesPlacedInQuarantineCount);
	}

	private Long getPlacedInQuarantine(List<DashboardQuarantineDataDto> contactsInQuarantineDtos) {
		return contactsInQuarantineDtos.stream()
			.filter(
				dashboardQuarantineDataDto -> (dashboardQuarantineDataDto.getQuarantineFrom() != null
					&& fromDate.before(DateUtils.addDays(dashboardQuarantineDataDto.getQuarantineFrom(), 1))
					&& dashboardQuarantineDataDto.getQuarantineFrom().before(toDate)))
			.count();
	}

	private void refreshDataForConvertedContactsToCase() {
		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.region(region).district(district).disease(disease).newCaseDateBetween(fromDate, toDate, null);

		setContactsConvertedToCaseCount(FacadeProvider.getCaseFacade().countCasesConvertedFromContacts(caseCriteria));

	}

	private void refreshDataForSelectedDisease() {

		// Update the entities lists according to the filters

		if (getDashboardType() == DashboardType.CONTACTS) {
			// Contacts
			setContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, fromDate, toDate));
			setPreviousContacts(
				FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, previousFromDate, previousToDate));

			this.refreshDataForQuarantinedContacts();
		}

		if (getDashboardType() == DashboardType.CONTACTS || this.disease != null) {
			// Cases
			CaseCriteria caseCriteria = new CaseCriteria();
			caseCriteria.region(region).district(district).disease(disease).newCaseDateBetween(fromDate, toDate, newCaseDateType);
			setCases(FacadeProvider.getCaseFacade().getCasesForDashboard(caseCriteria));
			setLastReportedDistrict(FacadeProvider.getCaseFacade().getLastReportedDistrictName(caseCriteria, true, true));

			caseCriteria.newCaseDateBetween(previousFromDate, previousToDate, newCaseDateType);
			setPreviousCases(FacadeProvider.getCaseFacade().getCasesForDashboard(caseCriteria));

			if (getDashboardType() != DashboardType.CONTACTS) {
				if (getCases().size() > 0) {
					setTestResultCountByResultType(
						FacadeProvider.getSampleFacade()
							.getNewTestResultCountByResultType(getCases().stream().map(c -> c.getId()).collect(Collectors.toList())));
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

		setOutbreakDistrictCount(
			FacadeProvider.getOutbreakFacade()
				.getOutbreakDistrictCount(
					new OutbreakCriteria().region(region).district(district).disease(disease).reportedBetween(fromDate, toDate)));

		refreshDataForQuarantinedCases();
		refreshDataForConvertedContactsToCase();
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

	public void setOutbreakDistrictCount(Long districtCount) {
		this.outbreakDistrictCount = districtCount;
	}

	public String getLastReportedDistrict() {
		return this.lastReportedDistrict;
	}

	public void setLastReportedDistrict(String district) {
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

	public NewCaseDateType getNewCaseDateType() {
		if (newCaseDateType == null) {
			return NewCaseDateType.MOST_RELEVANT;
		}
		return newCaseDateType;
	}

	public void setNewCaseDateType(NewCaseDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
	}

	public DashboardType getDashboardType() {
		return dashboardType;
	}

	public void setDashboardType(DashboardType dashboardType) {
		this.dashboardType = dashboardType;
	}

	public Long getContactsInQuarantineCount() {
		return contactsInQuarantineCount;
	}

	public void setContactsInQuarantineCount(Long contactsInQuarantineCount) {
		this.contactsInQuarantineCount = contactsInQuarantineCount;
	}

	public Long getContactsPlacedInQuarantineCount() {
		return contactsPlacedInQuarantineCount;
	}

	public void setContactsPlacedInQuarantineCount(Long contactsPlacedInQuarantineCount) {
		this.contactsPlacedInQuarantineCount = contactsPlacedInQuarantineCount;
	}

	public Long getCasesInQuarantineCount() {
		return casesInQuarantineCount;
	}

	public void setCasesInQuarantineCount(Long casesInQuarantineCount) {
		this.casesInQuarantineCount = casesInQuarantineCount;
	}

	public Long getCasesPlacedInQuarantineCount() {
		return casesPlacedInQuarantineCount;
	}

	public void setCasesPlacedInQuarantineCount(Long casesPlacedInQuarantineCount) {
		this.casesPlacedInQuarantineCount = casesPlacedInQuarantineCount;
	}

	public Long getContactsConvertedToCaseCount() {
		return contactsConvertedToCaseCount;
	}

	public void setContactsConvertedToCaseCount(Long contactsConvertedToCaseCount) {
		this.contactsConvertedToCaseCount = contactsConvertedToCaseCount;
	}
}
