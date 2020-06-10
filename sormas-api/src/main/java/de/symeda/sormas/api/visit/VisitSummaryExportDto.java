package de.symeda.sormas.api.visit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitSummaryExportDto implements Serializable {

	private static final long serialVersionUID = 7066530434713936967L;

	public static final String I18N_PREFIX = "ContactVisitExport";

	private Long contactId;
	private Integer maximumFollowUpVisits;
	private String uuid;
	private String firstName;
	private String lastName;
	private Date lastContactDate;
	private Date followUpUntil;
	private List<VisitSummaryExportDetailsDto> visitDetails = new ArrayList<>();

	public VisitSummaryExportDto(String uuid, Long contactId, String firstName, String lastName, Date lastContactDate, Date followUpUntil) {
		this.uuid = uuid;
		this.contactId = contactId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.lastContactDate = lastContactDate;
		this.followUpUntil = followUpUntil;
	}

	public Long getContactId() {
		return contactId;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public Integer getMaximumFollowUpVisits() {
		return maximumFollowUpVisits;
	}

	public void setMaximumFollowUpVisits(Integer maximumFollowUpVisits) {
		this.maximumFollowUpVisits = maximumFollowUpVisits;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<VisitSummaryExportDetailsDto> getVisitDetails() {
		return visitDetails;
	}

	public void setVisitDetails(List<VisitSummaryExportDetailsDto> visitDetails) {
		this.visitDetails = visitDetails;
	}
}
