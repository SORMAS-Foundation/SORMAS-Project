package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.visit.VisitReferenceDto;

public class ContactMapDto extends ContactReferenceDto {
	
	private static final long serialVersionUID = -7764607075875188799L;
	
	public static final String I18N_PREFIX = "Contact";
	
	public static final String LAST_VISIT = "lastVisit";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	
	private VisitReferenceDto lastVisit;
	private ContactClassification contactClassification;
	private Float reportLat;
	private Float reportLon;
	
	public VisitReferenceDto getLastVisit() {
		return lastVisit;
	}
	public void setLastVisit(VisitReferenceDto lastVisit) {
		this.lastVisit = lastVisit;
	}
		
	public ContactClassification getContactClassification() {
		return contactClassification;
	}
	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}
	
	public Float getReportLat() {
		return reportLat;
	}
	public void setReportLat(Float reportLat) {
		this.reportLat = reportLat;
	}
	
	public Float getReportLon() {
		return reportLon;
	}
	public void setReportLon(Float reportLon) {
		this.reportLon = reportLon;
	}

}
