package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactVisitDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207651L;

	private int visitsCount;
	private int missedVisitsCount;
	private int missedVisitsGrowth;
	private int unavailableVisitsCount;
	private int unavailableVisitsGrowth;
	private int uncooperativeVisitsCount;
	private int uncooperativeVisitsGrowth;
	private int cooperativeVisitsCount;
	private int cooperativeVisitsGrowth;
	private int previousMissedVisitsCount;
	private int previousUnavailableVisitsCount;
	private int previousUncooperativeVisitsCount;
	private int previousCooperativeVisitsCount;

	public DashboardContactVisitDto(
		int visitsCount,
		int missedVisitsCount,
		int missedVisitsGrowth,
		int unavailableVisitsCount,
		int unavailableVisitsGrowth,
		int uncooperativeVisitsCount,
		int uncooperativeVisitsGrowth,
		int cooperativeVisitsCount,
		int cooperativeVisitsGrowth,
		int previousMissedVisitsCount,
		int previousUnavailableVisitsCount,
		int previousUncooperativeVisitsCount,
		int previousCooperativeVisitsCount) {
		this.visitsCount = visitsCount;
		this.missedVisitsCount = missedVisitsCount;
		this.missedVisitsGrowth = missedVisitsGrowth;
		this.unavailableVisitsCount = unavailableVisitsCount;
		this.unavailableVisitsGrowth = unavailableVisitsGrowth;
		this.uncooperativeVisitsCount = uncooperativeVisitsCount;
		this.uncooperativeVisitsGrowth = uncooperativeVisitsGrowth;
		this.cooperativeVisitsCount = cooperativeVisitsCount;
		this.cooperativeVisitsGrowth = cooperativeVisitsGrowth;
		this.previousMissedVisitsCount = previousMissedVisitsCount;
		this.previousUnavailableVisitsCount = previousUnavailableVisitsCount;
		this.previousUncooperativeVisitsCount = previousUncooperativeVisitsCount;
		this.previousCooperativeVisitsCount = previousCooperativeVisitsCount;
	}

	public int getVisitsCount() {
		return visitsCount;
	}

	public int getMissedVisitsCount() {
		return missedVisitsCount;
	}

	public int getMissedVisitsGrowth() {
		return missedVisitsGrowth;
	}

	public int getUnavailableVisitsCount() {
		return unavailableVisitsCount;
	}

	public int getUnavailableVisitsGrowth() {
		return unavailableVisitsGrowth;
	}

	public int getUncooperativeVisitsCount() {
		return uncooperativeVisitsCount;
	}

	public int getUncooperativeVisitsGrowth() {
		return uncooperativeVisitsGrowth;
	}

	public int getCooperativeVisitsCount() {
		return cooperativeVisitsCount;
	}

	public int getCooperativeVisitsGrowth() {
		return cooperativeVisitsGrowth;
	}

	public int getPreviousMissedVisitsCount() {
		return previousMissedVisitsCount;
	}

	public int getPreviousUnavailableVisitsCount() {
		return previousUnavailableVisitsCount;
	}

	public int getPreviousUncooperativeVisitsCount() {
		return previousUncooperativeVisitsCount;
	}

	public int getPreviousCooperativeVisitsCount() {
		return previousCooperativeVisitsCount;
	}
}
