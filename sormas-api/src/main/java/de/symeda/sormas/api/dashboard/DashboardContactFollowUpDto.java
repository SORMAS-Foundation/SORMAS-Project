package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207650L;
	private int followUpContactsCount;
	private int cooperativeContactsCount;
	private int cooperativeContactsPercentage;
	private int uncooperativeContactsCount;
	private int uncooperativeContactsPercentage;
	private int unavailableContactsCount;
	private int unavailableContactsPercentage;
	private int neverVisitedContactsCount;
	private int notVisitedContactsPercentage;

	private int missedVisitsOneDay;
	private int missedVisitsTwoDays;
	private int missedVisitsThreeDays;
	private int missedVisitsGtThreeDays;

	public DashboardContactFollowUpDto(
		int followUpContactsCount,
		int cooperativeContactsCount,
		int cooperativeContactsPercentage,
		int uncooperativeContactsCount,
		int uncooperativeContactsPercentage,
		int unavailableContactsCount,
		int unavailableContactsPercentage,
		int neverVisitedContactsCount,
		int notVisitedContactsPercentage,
		int missedVisitsOneDay,
		int missedVisitsTwoDays,
		int missedVisitsThreeDays,
		int missedVisitsGtThreeDays) {
		this.followUpContactsCount = followUpContactsCount;
		this.cooperativeContactsCount = cooperativeContactsCount;
		this.cooperativeContactsPercentage = cooperativeContactsPercentage;
		this.uncooperativeContactsCount = uncooperativeContactsCount;
		this.uncooperativeContactsPercentage = uncooperativeContactsPercentage;
		this.unavailableContactsCount = unavailableContactsCount;
		this.unavailableContactsPercentage = unavailableContactsPercentage;
		this.neverVisitedContactsCount = neverVisitedContactsCount;
		this.notVisitedContactsPercentage = notVisitedContactsPercentage;
		this.missedVisitsOneDay = missedVisitsOneDay;
		this.missedVisitsTwoDays = missedVisitsTwoDays;
		this.missedVisitsThreeDays = missedVisitsThreeDays;
		this.missedVisitsGtThreeDays = missedVisitsGtThreeDays;
	}

	public int getFollowUpContactsCount() {
		return followUpContactsCount;
	}

	public int getCooperativeContactsCount() {
		return cooperativeContactsCount;
	}

	public int getCooperativeContactsPercentage() {
		return cooperativeContactsPercentage;
	}

	public int getUncooperativeContactsCount() {
		return uncooperativeContactsCount;
	}

	public int getUncooperativeContactsPercentage() {
		return uncooperativeContactsPercentage;
	}

	public int getUnavailableContactsCount() {
		return unavailableContactsCount;
	}

	public int getUnavailableContactsPercentage() {
		return unavailableContactsPercentage;
	}

	public int getNeverVisitedContactsCount() {
		return neverVisitedContactsCount;
	}

	public int getNotVisitedContactsPercentage() {
		return notVisitedContactsPercentage;
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
