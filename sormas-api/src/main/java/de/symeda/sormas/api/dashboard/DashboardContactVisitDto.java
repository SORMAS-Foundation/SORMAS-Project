package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

public class DashboardContactVisitDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207651L;

	int visitsCount;
	int missedVisitsCount;
	int unavailableVisitsCount;
	int uncooperativeVisitsCount;
	int cooperativeVisitsCount;
	int previousMissedVisitsCount;
	int previousUnavailableVisitsCount;
	int previousUncooperativeVisitsCount;
	int previousCooperativeVisitsCount;

	public DashboardContactVisitDto(
		int visitsCount,
		int missedVisitsCount,
		int unavailableVisitsCount,
		int uncooperativeVisitsCount,
		int cooperativeVisitsCount,
		int previousMissedVisitsCount,
		int previousUnavailableVisitsCount,
		int previousUncooperativeVisitsCount,
		int previousCooperativeVisitsCount) {
		this.visitsCount = visitsCount;
		this.missedVisitsCount = missedVisitsCount;
		this.unavailableVisitsCount = unavailableVisitsCount;
		this.uncooperativeVisitsCount = uncooperativeVisitsCount;
		this.cooperativeVisitsCount = cooperativeVisitsCount;
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

	public int getUnavailableVisitsCount() {
		return unavailableVisitsCount;
	}

	public int getUncooperativeVisitsCount() {
		return uncooperativeVisitsCount;
	}

	public int getCooperativeVisitsCount() {
		return cooperativeVisitsCount;
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
