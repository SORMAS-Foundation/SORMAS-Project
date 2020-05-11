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
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class ContactCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease disease;
	private CaseReferenceDto caze;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private UserReferenceDto contactOfficer;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date reportDateFrom;
	private Date reportDateTo;
	// Used to re-construct whether users have filtered by epi weeks or dates
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date followUpUntilFrom;
	private Date followUpUntilTo;
	/**
	 * If yes, the followUpUntilTo filter will search for strict matches instead of a period,
	 * even if a followUpUntilFrom is specified
	 */
	private Boolean followUpUntilToPrecise;
	private Date lastContactDateFrom;
	private Date lastContactDateTo;
	private Boolean deleted = Boolean.FALSE;
	private String nameUuidCaseLike;
	private EntityRelevanceStatus relevanceStatus;
	private Boolean onlyHighPriorityContacts;
	private ContactCategory contactCategory;
	private CaseClassification caseClassification;
	private QuarantineType quarantineType;
	private Date quarantineTo;
	private Boolean onlyQuarantineHelpNeeded;
	private Boolean quarantineOrderedVerbally;
	private Boolean quarantineOrderedOfficialDocument;
	private Boolean quarantineNotOrdered;

	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public ContactCriteria reportingUserRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public ContactCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public ContactCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public ContactCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public ContactCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public ContactCriteria contactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
		return this;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public ContactCriteria contactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
		return this;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public ContactCriteria contactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
		return this;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public ContactCriteria followUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
		return this;
	}

	public ContactCriteria reportDateBetween(Date reportDateFrom, Date reportDateTo) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		return this;
	}

	public ContactCriteria reportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
		return this;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public ContactCriteria reportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
		return this;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public ContactCriteria lastContactDateBetween(Date lastContactDateFrom, Date lastContactDateTo) {
		this.lastContactDateFrom = lastContactDateFrom;
		this.lastContactDateTo = lastContactDateTo;
		return this;
	}

	public ContactCriteria lastContactDateFrom(Date lastContactDateFrom) {
		this.lastContactDateFrom = lastContactDateFrom;
		return this;
	}

	public Date getLastContactDateFrom() {
		return lastContactDateFrom;
	}

	public ContactCriteria lastContactDateTo(Date lastContactDateTo) {
		this.lastContactDateTo = lastContactDateTo;
		return this;
	}

	public Date getLastContactDateTo() {
		return lastContactDateTo;
	}

	public ContactCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public ContactCriteria followUpUntilBetween(Date followUpUntilFrom, Date followUpUntilTo) {
		this.followUpUntilFrom = followUpUntilFrom;
		this.followUpUntilTo = followUpUntilTo;
		return this;
	}

	public ContactCriteria followUpUntilFrom(Date followUpUntilFrom) {
		this.followUpUntilFrom = followUpUntilFrom;
		return this;
	}

	public Date getFollowUpUntilFrom() {
		return followUpUntilFrom;
	}

	public ContactCriteria followUpUntilTo(Date followUpUntilTo) {
		this.followUpUntilTo = followUpUntilTo;
		return this;
	}

	public Date getFollowUpUntilTo() {
		return followUpUntilTo;
	}

	public Boolean getFollowUpUntilToPrecise() {
		return followUpUntilToPrecise;
	}

	public ContactCriteria followUpUntilToPrecise(Boolean followUpUntilToPrecise) {
		this.followUpUntilToPrecise = followUpUntilToPrecise;
		return this;
	}

	public ContactCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public ContactCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * returns all entries that match ALL of the passed words
	 */
	public ContactCriteria nameUuidCaseLike(String nameUuidCaseLike) {
		this.nameUuidCaseLike = nameUuidCaseLike;
		return this;
	}

	@IgnoreForUrl
	public String getNameUuidCaseLike() {
		return nameUuidCaseLike;
	}

	public Boolean getOnlyHighPriorityContacts() {
		return onlyHighPriorityContacts;
	}

	public ContactCriteria contactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
		return this;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public ContactCriteria onlyHighPriorityContacts(Boolean onlyHighPriorityContacts) {
		this.onlyHighPriorityContacts = onlyHighPriorityContacts;
		return this;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public ContactCriteria caseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public QuarantineType getQuarantineType() {
		return quarantineType;
	}

	public ContactCriteria quarantineType(QuarantineType quarantineType) {
		this.quarantineType = quarantineType;
		return this;
	}

	public Boolean getOnlyQuarantineHelpNeeded() {
		return onlyQuarantineHelpNeeded;
	}

	public ContactCriteria onlyQuarantineHelpNeeded(Boolean onlyQuarantineHelpNeeded) {
		this.onlyQuarantineHelpNeeded = onlyQuarantineHelpNeeded;
		return this;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public ContactCriteria quarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
		return this;
	}

	public Boolean getQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public ContactCriteria quarantineOrderedVerbally(Boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
		return this;
	}

	public Boolean getQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public ContactCriteria quarantineOrderedOfficialDocument(Boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
		return this;
	}

	public Boolean getQuarantineNotOrdered() {
		return quarantineNotOrdered;
	}

	public ContactCriteria quarantineNotOrdered(Boolean quarantineNotOrdered) {
		this.quarantineNotOrdered = quarantineNotOrdered;
		return this;
	}

}
