package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

public class MapContactDto implements Serializable {

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
	private String personFirstName;
	private String personLastName;
	private String casePersonFirstName;
	private String casePersonLastName;
	
	public MapContactDto(String uuid, ContactClassification contactClassification, Double reportLat, Double reportLon, Double addressLat, Double addressLon, Date caseOnsetDate, Date caseReportDate,
			String personFirstName, String personLastName, String casePersonFirstName, String casePersonLastName) {
		this.uuid = uuid;
		this.contactClassification = contactClassification;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.addressLat = addressLat;
		this.addressLon = addressLon;
		this.caseOnsetDate = caseOnsetDate;
		this.caseReportDate = caseReportDate;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.casePersonFirstName = casePersonFirstName;
		this.casePersonLastName = casePersonLastName;
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
	
	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	public String getCasePersonFirstName() {
		return casePersonFirstName;
	}

	public void setCasePersonFirstName(String casePersonFirstName) {
		this.casePersonFirstName = casePersonFirstName;
	}

	public String getCasePersonLastName() {
		return casePersonLastName;
	}

	public void setCasePersonLastName(String casePersonLastName) {
		this.casePersonLastName = casePersonLastName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(personFirstName).append(" ").append(personLastName.toUpperCase());
		builder.append(" to case ");
		builder.append(casePersonFirstName).append(" ").append(casePersonLastName.toUpperCase());
		return builder.toString();
	}

}
