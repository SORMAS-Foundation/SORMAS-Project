package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

public class DashboardContactStoppedFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207652L;

	@Schema(description = "Total number of contacts for which follow-ups have been stopped")
	private int stoppedFollowUpContacts;
	@Schema(description = "Number of contacts for which follow-ups have been completed")
	private int completedContacts;
	@Schema(description = "Rounded percentage of contacts for which follow-up has been completed")
	private int followUpCompletedPercentage;
	@Schema(description = "Number of contacts for which follow-ups have been cancelled")
	private int cancelledContacts;
	@Schema(description = "Rounded percentage of contacts for which follow-ups have been cancelled")
	private int followUpCanceledPercentage;
	@Schema(description = "Number of contacts that have been lost to follow-up")
	private int lostContacts;
	@Schema(description = "Rounded percentage of contacts that have been lost to follow-up")
	private int lostToFollowUpPercentage;
	@Schema(description = "Number of contacts that have been converted into cases")
	private int convertedContacts;
	@Schema(description = "Rounded percentage of contacts that have been converted into cases")
	private int contactStatusConvertedPercentage;

	public DashboardContactStoppedFollowUpDto(
		int stoppedFollowUpContacts,
		int completedContacts,
		int followUpCompletedPercentage,
		int cancelledContacts,
		int followUpCanceledPercentage,
		int lostContacts,
		int lostToFollowUpPercentage,
		int convertedContacts,
		int contactStatusConvertedPercentage) {
		this.stoppedFollowUpContacts = stoppedFollowUpContacts;
		this.completedContacts = completedContacts;
		this.followUpCompletedPercentage = followUpCompletedPercentage;
		this.cancelledContacts = cancelledContacts;
		this.followUpCanceledPercentage = followUpCanceledPercentage;
		this.lostContacts = lostContacts;
		this.lostToFollowUpPercentage = lostToFollowUpPercentage;
		this.convertedContacts = convertedContacts;
		this.contactStatusConvertedPercentage = contactStatusConvertedPercentage;
	}

	public int getStoppedFollowUpContacts() {
		return stoppedFollowUpContacts;
	}

	public int getCompletedContacts() {
		return completedContacts;
	}

	public int getFollowUpCompletedPercentage() {
		return followUpCompletedPercentage;
	}

	public int getCancelledContacts() {
		return cancelledContacts;
	}

	public int getFollowUpCanceledPercentage() {
		return followUpCanceledPercentage;
	}

	public int getLostContacts() {
		return lostContacts;
	}

	public int getLostToFollowUpPercentage() {
		return lostToFollowUpPercentage;
	}

	public int getConvertedContacts() {
		return convertedContacts;
	}

	public int getContactStatusConvertedPercentage() {
		return contactStatusConvertedPercentage;
	}
}
