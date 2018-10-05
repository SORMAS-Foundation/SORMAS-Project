package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;

public class ContactCriteria implements Serializable {

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease caseDisease;
	private CaseReferenceDto caze;
	private RegionReferenceDto caseRegion;
	private DistrictReferenceDto caseDistrict;
	private FacilityReferenceDto caseFacility;
	private UserReferenceDto contactOfficer;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date reportDateFrom;
	private Date reportDateTo;

	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public ContactCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public Disease getCaseDisease() {
		return caseDisease;
	}

	public ContactCriteria caseDiseaseEquals(Disease disease) {
		this.caseDisease = disease;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public ContactCriteria caseEquals(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public RegionReferenceDto getCaseRegion() {
		return caseRegion;
	}

	public ContactCriteria caseRegion(RegionReferenceDto caseRegion) {
		this.caseRegion = caseRegion;
		return this;
	}

	public DistrictReferenceDto getCaseDistrict() {
		return caseDistrict;
	}

	public ContactCriteria caseDistrict(DistrictReferenceDto caseDistrict) {
		this.caseDistrict = caseDistrict;
		return this;
	}

	public FacilityReferenceDto getCaseFacility() {
		return caseFacility;
	}

	public ContactCriteria caseFacility(FacilityReferenceDto caseFacility) {
		this.caseFacility = caseFacility;
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

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}
	
}
