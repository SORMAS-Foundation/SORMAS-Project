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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDefinition;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardContactDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardQuarantineDataDto;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.sample.PathogenTestResultType;

// FIXME: 06/08/2020 this should be refactored into two specific data providers for case and contact dashboards
public class DashboardDataProvider extends AbstractDashboardDataProvider<DashboardCriteria> {

	private DashboardType dashboardType;

	private NewCaseDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;

	// overall
	private List<DiseaseBurdenDto> diseasesBurden = new ArrayList<>();

	// TODO make disease specific when contact dashboard is updated
	private List<DashboardContactDto> contacts = new ArrayList<>();
	private List<DashboardContactDto> previousContacts = new ArrayList<>();

	// disease specific
	private List<DashboardCaseDto> cases = new ArrayList<>();
	private List<DashboardCaseDto> previousCases = new ArrayList<>();
	private Map<CaseClassification, Integer> casesCountByClassification = new HashMap<>();
	private Long outbreakDistrictCount = 0L;
	private String lastReportedDistrict = "";
	private List<DashboardEventDto> events = new ArrayList<>();
	private Map<PathogenTestResultType, Long> newCasesFinalLabResultCountByResultType;
	private Map<EventStatus, Long> eventCountByStatus;

	private Long contactsInQuarantineCount = 0L;
	private Long contactsPlacedInQuarantineCount = 0L;
	private Long casesInQuarantineCount = 0L;
	private Long casesPlacedInQuarantineCount = 0L;
	private Long contactsConvertedToCaseCount = 0L;
	private Long caseWithReferenceDefinitionFulfilledCount = 0L;

	public void refreshData() {

		// Update the entities lists according to the filters
		// Disease burden
		setDiseasesBurden(
			FacadeProvider.getDashboardFacade()
				.getDiseaseBurden(region, district, fromDate, toDate, previousFromDate, previousToDate, newCaseDateType));

		this.refreshDataForSelectedDisease();
	}

	@Override
	protected DashboardCriteria newCriteria() {
		return new DashboardCriteria();
	}

	private void refreshDataForQuarantinedContacts() {

		List<DashboardQuarantineDataDto> contactsInQuarantineDtos = getContacts().stream()
			.map(DashboardContactDto::getDashboardQuarantineDataDto)
			.filter(quarantineData(fromDate, toDate))
			.collect(Collectors.toList());

		setContactsInQuarantineCount((long) contactsInQuarantineDtos.size());

		Long dashboardContactsPlacedInQuarantineCount = getPlacedInQuarantine(contactsInQuarantineDtos);

		setContactsPlacedInQuarantineCount(dashboardContactsPlacedInQuarantineCount);
	}

	private void refreshDataForQuarantinedCases() {

		List<DashboardQuarantineDataDto> casesInQuarantineDtos = getCases().stream()
			.map(DashboardCaseDto::getDashboardQuarantineDataDto)
			.filter(quarantineData(fromDate, toDate))
			.collect(Collectors.toList());

		setCasesInQuarantineCount((long) casesInQuarantineDtos.size());

		Long dashboardCasesPlacedInQuarantineCount = getPlacedInQuarantine(casesInQuarantineDtos);

		setCasesPlacedInQuarantineCount(dashboardCasesPlacedInQuarantineCount);
	}

	private Predicate<DashboardQuarantineDataDto> quarantineData(Date fromDate, Date toDate) {

		return p -> {
			Date quarantineFrom = p.getQuarantineFrom();
			Date quarantineTo = p.getQuarantineTo();

			if (fromDate != null && toDate != null) {
				if (quarantineFrom != null && quarantineTo != null) {
					return quarantineTo.after(fromDate) && quarantineFrom.before(toDate);
				} else if (quarantineFrom != null) {
					return quarantineFrom.after(fromDate) && quarantineFrom.before(toDate);
				} else if (quarantineTo != null) {
					return quarantineTo.after(fromDate) && quarantineTo.before(toDate);
				}
			} else if (fromDate != null) {
				if (quarantineFrom != null) {
					return quarantineFrom.after(fromDate);
				} else if (quarantineTo != null) {
					return quarantineTo.after(fromDate);
				}
			} else if (toDate != null) {
				if (quarantineFrom != null) {
					return quarantineFrom.before(toDate);
				} else if (quarantineTo != null) {
					return quarantineTo.before(toDate);
				}
			}

			return false;
		};
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
		DashboardCriteria dashboardCriteria = buildDashboardCriteriaWithDates();
		setContactsConvertedToCaseCount(FacadeProvider.getDashboardFacade().countCasesConvertedFromContacts(dashboardCriteria));
	}

	private void refreshDataForCasesWithReferenceDefinitionFulfilled() {
		List<DashboardCaseDto> casesWithReferenceDefinitionFulfilled =
			getCases().stream().filter(cases -> cases.getCaseReferenceDefinition() == CaseReferenceDefinition.FULFILLED).collect(Collectors.toList());
		setCaseWithReferenceDefinitionFulfilledCount(Long.valueOf(casesWithReferenceDefinitionFulfilled.size()));
	}

	public void refreshDataForSelectedDisease() {

		// Update the entities lists according to the filters

		if (getDashboardType() == DashboardType.CONTACTS) {
			// Contacts
			setContacts(FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, fromDate, toDate));
			setPreviousContacts(
				FacadeProvider.getContactFacade().getContactsForDashboard(region, district, disease, previousFromDate, previousToDate));

			this.refreshDataForQuarantinedContacts();
		}

		if (getDashboardType() == DashboardType.CONTACTS || this.disease != null) {
			DashboardCriteria caseDashboardCriteria = buildDashboardCriteria(fromDate, toDate);

			// Cases
			setCases(FacadeProvider.getDashboardFacade().getCases(caseDashboardCriteria));
			setLastReportedDistrict(FacadeProvider.getDashboardFacade().getLastReportedDistrictName(caseDashboardCriteria));
			setCasesCountByClassification(
				FacadeProvider.getDashboardFacade()
					.getCasesCountByClassification(buildDashboardCriteria(fromDate, toDate).includeNotACaseClassification(true)));

			setPreviousCases(FacadeProvider.getDashboardFacade().getCases(buildDashboardCriteria(previousFromDate, previousToDate)));

			// test results
			if (getDashboardType() != DashboardType.CONTACTS) {
				setNewCasesFinalLabResultCountByResultType(
					FacadeProvider.getDashboardFacade().getNewCasesFinalLabResultCountByResultType(caseDashboardCriteria));
			}
		}

		if (this.disease == null || getDashboardType() == DashboardType.CONTACTS) {
			return;
		}

		// Events
		DashboardCriteria eventDashboardCriteria = buildDashboardCriteriaWithDates();
		setEvents(FacadeProvider.getDashboardFacade().getNewEvents(eventDashboardCriteria));
		setEventCountByStatus(FacadeProvider.getDashboardFacade().getEventCountByStatus(eventDashboardCriteria));

		setOutbreakDistrictCount(
			FacadeProvider.getOutbreakFacade()
				.getOutbreakDistrictCount(
					new OutbreakCriteria().region(region).district(district).disease(disease).reportedBetween(fromDate, toDate)));

		refreshDataForQuarantinedCases();
		refreshDataForConvertedContactsToCase();
		refreshDataForCasesWithReferenceDefinitionFulfilled();
	}

	private DashboardCriteria buildDashboardCriteria(Date fromDate, Date toDate) {
		return buildDashboardCriteria().newCaseDateType(newCaseDateType).dateBetween(fromDate, toDate);
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

	public Map<CaseClassification, Integer> getCasesCountByClassification() {
		return casesCountByClassification;
	}

	public void setCasesCountByClassification(Map<CaseClassification, Integer> casesCountByClassification) {
		this.casesCountByClassification = casesCountByClassification;
	}

	public List<DashboardEventDto> getEvents() {
		return events;
	}

	public void setEvents(List<DashboardEventDto> events) {
		this.events = events;
	}

	public Map<EventStatus, Long> getEventCountByStatus() {
		return eventCountByStatus;
	}

	public void setEventCountByStatus(Map<EventStatus, Long> events) {
		this.eventCountByStatus = events;
	}

	public Map<PathogenTestResultType, Long> getNewCasesFinalLabResultCountByResultType() {
		return newCasesFinalLabResultCountByResultType;
	}

	public void setNewCasesFinalLabResultCountByResultType(Map<PathogenTestResultType, Long> labResults) {
		this.newCasesFinalLabResultCountByResultType = labResults;
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

	public Long getCaseWithReferenceDefinitionFulfilledCount() {
		return caseWithReferenceDefinitionFulfilledCount;
	}

	public void setCaseWithReferenceDefinitionFulfilledCount(Long caseWithReferenceDefinitionFulfilledCount) {
		this.caseWithReferenceDefinitionFulfilledCount = caseWithReferenceDefinitionFulfilledCount;
	}
}
