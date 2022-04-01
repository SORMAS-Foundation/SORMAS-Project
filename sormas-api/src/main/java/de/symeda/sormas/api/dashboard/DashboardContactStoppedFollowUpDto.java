package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactStoppedFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207652L;

	private int stoppedFollowUpContacts;
	private int completedContacts;
	private int followUpCompletedPercentage;
	private int cancelledContacts;
	private int followUpCanceledPercentage;
	private int lostContacts;
	private int lostToFollowUpPercentage;
	private int convertedContacts;
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
