package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactStoppedFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207652L;

	private int stoppedFollowUpContacts;
	private int completedContacts;
	private int cancelledContacts;
	private int lostContacts;
	private int convertedContacts;

	public DashboardContactStoppedFollowUpDto(
		int stoppedFollowUpContacts,
		int completedContacts,
		int cancelledContacts,
		int lostContacts,
		int convertedContacts) {
		this.stoppedFollowUpContacts = stoppedFollowUpContacts;
		this.completedContacts = completedContacts;
		this.cancelledContacts = cancelledContacts;
		this.lostContacts = lostContacts;
		this.convertedContacts = convertedContacts;
	}

	public int getStoppedFollowUpContacts() {
		return stoppedFollowUpContacts;
	}

	public int getCompletedContacts() {
		return completedContacts;
	}

	public int getCancelledContacts() {
		return cancelledContacts;
	}

	public int getLostContacts() {
		return lostContacts;
	}

	public int getConvertedContacts() {
		return convertedContacts;
	}
}
