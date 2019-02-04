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

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Required;

public class ContactDto extends EntityDto {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "Contact";
	
	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String CASE_DISEASE = "caseDisease";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String RESULTING_CASE_USER = "resultingCaseUser";
	
	@Required
	private Date reportDateTime;
	@Required
	private UserReferenceDto reportingUser;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;

	@Required
	private PersonReferenceDto person;
	@Required
	private CaseReferenceDto caze;
	private Disease caseDisease;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private FollowUpStatus followUpStatus;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private String followUpComment;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private Date followUpUntil;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private UserReferenceDto contactOfficer;
	private String description;
	private ContactRelation relationToCase;
	
	private CaseReferenceDto resultingCase; // read-only now, but editable long-term
	private UserReferenceDto resultingCaseUser;

	public static ContactDto build(CaseReferenceDto caze) {
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());

		contact.setCaze(caze);

		contact.setReportDateTime(new Date());
		contact.setContactClassification(ContactClassification.UNCONFIRMED);
		contact.setContactStatus(ContactStatus.ACTIVE);

		return contact;
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	public CaseReferenceDto getCaze() {
		return caze;
	}
	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}
	public Date getLastContactDate() {
		return lastContactDate;
	}
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
	public ContactProximity getContactProximity() {
		return contactProximity;
	}
	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}
	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}
	public ContactClassification getContactClassification() {
		return contactClassification;
	}
	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}
	public ContactStatus getContactStatus() {
		return contactStatus;
	}
	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}
	public Date getFollowUpUntil() {
		return followUpUntil;
	}
	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}
	public ContactRelation getRelationToCase() {
		return relationToCase;
	}
	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}
	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}
	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}
	public Double getReportLat() {
		return reportLat;
	}
	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}
	public Double getReportLon() {
		return reportLon;
	}
	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}
	public String getFollowUpComment() {
		return followUpComment;
	}
	public void setFollowUpComment(String followUpComment) {
		this.followUpComment = followUpComment;
	}
	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}
	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}
	
	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid());
	}
	public UserReferenceDto getResultingCaseUser() {
		return resultingCaseUser;
	}
	public void setResultingCaseUser(UserReferenceDto resultingCaseUser) {
		this.resultingCaseUser = resultingCaseUser;
	}
	public Disease getCaseDisease() {
		return caseDisease;
	}
	/**
	 * Read-only
	 */
	public void setCaseDisease(Disease caseDisease) {
		this.caseDisease = caseDisease;
	}
	
}
