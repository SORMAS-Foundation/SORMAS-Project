package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207650L;
	private int followUpContacts;
	private int cooperativeContacts;
	private int uncooperativeContacts;
	private int unavailableContacts;
	private int neverVisitedContacts;
	private int missedVisitsOneDay;
	private int missedVisitsTwoDays;
	private int missedVisitsThreeDays;
	private int missedVisitsGtThreeDays;

	public DashboardContactFollowUpDto(
		int followUpContacts,
		int cooperativeContacts,
		int uncooperativeContacts,
		int unavailableContacts,
		int neverVisitedContacts,
		int missedVisitsOneDay,
		int missedVisitsTwoDays,
		int missedVisitsThreeDays,
		int missedVisitsGtThreeDays) {

		this.followUpContacts = followUpContacts;
		this.cooperativeContacts = cooperativeContacts;
		this.uncooperativeContacts = uncooperativeContacts;
		this.unavailableContacts = unavailableContacts;
		this.neverVisitedContacts = neverVisitedContacts;
		this.missedVisitsOneDay = missedVisitsOneDay;
		this.missedVisitsTwoDays = missedVisitsTwoDays;
		this.missedVisitsThreeDays = missedVisitsThreeDays;
		this.missedVisitsGtThreeDays = missedVisitsGtThreeDays;

	}

	public int getFollowUpContacts() {
		return followUpContacts;
	}

	public int getCooperativeContacts() {
		return cooperativeContacts;
	}

	public int getUncooperativeContacts() {
		return uncooperativeContacts;
	}

	public int getUnavailableContacts() {
		return unavailableContacts;
	}

	public int getNeverVisitedContacts() {
		return neverVisitedContacts;
	}

	public int getMissedVisitsOneDay() {
		return missedVisitsOneDay;
	}

	public int getMissedVisitsTwoDays() {
		return missedVisitsTwoDays;
	}

	public int getMissedVisitsThreeDays() {
		return missedVisitsThreeDays;
	}

	public int getMissedVisitsGtThreeDays() {
		return missedVisitsGtThreeDays;
	}
}
