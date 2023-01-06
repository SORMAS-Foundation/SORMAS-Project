package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

public class DashboardContactFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207650L;
	@Schema(description = "Number of contacts under follow-up")
	private int followUpContactsCount;
	@Schema(description = "Number of contacts that were cooperative when visited")
	private int cooperativeContactsCount;
	@Schema(description = "Rounded percentage of contacts that were cooperative when visited")
	private int cooperativeContactsPercentage;
	@Schema(description = "Number of contacts that were uncooperative when visited")
	private int uncooperativeContactsCount;
	@Schema(description = "Rounded percentage of contacts that were uncooperative when visited")
	private int uncooperativeContactsPercentage;
	@Schema(description = "Number of contacts that were unavailable when visited")
	private int unavailableContactsCount;
	@Schema(description = "Rounded percentage of contacts that were unavailable when visited")
	private int unavailableContactsPercentage;
	@Schema(description = "Number of contacts that were never visited")
	private int neverVisitedContactsCount;
	@Schema(description = "Rounded percentage of contacts that were never visited")
	private int notVisitedContactsPercentage;

	@Schema(description = "Number of contacts that missed their latest visit date by one day")
	private int missedVisitsOneDay;
	@Schema(description = "Number of contacts that missed their latest visit date by two days")
	private int missedVisitsTwoDays;
	@Schema(description = "Number of contacts that missed their latest visit date by three days")
	private int missedVisitsThreeDays;
	@Schema(description = "Number of contacts that missed their latest visit date by more than three days")
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
