package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.PersonReferenceDto;

public class MapContact implements Serializable {

	private static final long serialVersionUID = -5840120135940125045L;
	
	private String uuid;
	private ContactClassification contactClassification;
	private Double reportLat;
	private Double reportLon;
	private Double addressLat;
	private Double addressLon;
	private Date lastVisitDateTime;
	private Date caseOnsetDate;
	private Date caseReportDate;
	private String personUuid;
	private PersonReferenceDto person;
	private String casePersonUuid;
	private PersonReferenceDto casePerson;
	
	public MapContact(String uuid, ContactClassification contactClassification, Double reportLat, Double reportLon, Double addressLat, Double addressLon, Date caseOnsetDate, Date caseReportDate, String personUuid, String casePersonUuid) {
		this.uuid = uuid;
		this.contactClassification = contactClassification;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.addressLat = addressLat;
		this.addressLon = addressLon;
		this.caseOnsetDate = caseOnsetDate;
		this.caseReportDate = caseReportDate;
		this.personUuid = personUuid;
		this.casePersonUuid = casePersonUuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}
	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
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

	public Double getAddressLat() {
		return addressLat;
	}

	public void setAddressLat(Double addressLat) {
		this.addressLat = addressLat;
	}

	public Double getAddressLon() {
		return addressLon;
	}

	public void setAddressLon(Double addressLon) {
		this.addressLon = addressLon;
	}

	public Date getLastVisitDateTime() {
		return lastVisitDateTime;
	}

	public void setLastVisitDateTime(Date lastVisitDateTime) {
		this.lastVisitDateTime = lastVisitDateTime;
	}

	public Date getCaseOnsetDate() {
		return caseOnsetDate;
	}

	public void setCaseOnsetDate(Date caseOnsetDate) {
		this.caseOnsetDate = caseOnsetDate;
	}

	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}
	
	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public String getCasePersonUuid() {
		return casePersonUuid;
	}

	public void setCasePersonUuid(String casePersonUuid) {
		this.casePersonUuid = casePersonUuid;
	}

	public PersonReferenceDto getCasePerson() {
		return casePerson;
	}

	public void setCasePerson(PersonReferenceDto casePerson) {
		this.casePerson = casePerson;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(person.getFirstName()).append(" ").append(person.getLastName().toUpperCase());
		builder.append(" to case ");
		builder.append(casePerson.getFirstName()).append(" ").append(casePerson.getLastName().toUpperCase());
		return builder.toString();
	}

}
