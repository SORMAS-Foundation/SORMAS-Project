package de.symeda.sormas.api.dashboard;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

public class DashboardContactVisitDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207651L;

	@Schema(description = "Total number of visits conducted")
	private int visitsCount;
	@Schema(description = "Number of visits that missed their final date to be conducted during the primary time period")
	private int missedVisitsCount;
	@Schema(
		description = "Growth of missed visits during the primary time period compared to the number of missed visits during the secondary time period in percent")
	private int missedVisitsGrowth;
	@Schema(description = "Number of unavailable visits during the primary time period")
	private int unavailableVisitsCount;
	@Schema(
		description = "Growth of unavailable visits during the primary time period compared to the number of unavailable visits during the secondary time period in percent")
	private int unavailableVisitsGrowth;
	@Schema(description = "Number of uncooperative visits during the primary time period")
	private int uncooperativeVisitsCount;
	@Schema(
		description = "Growth of uncooperative visits during the primary time period compared to the number of uncooperative visits during the secondary time period in percent")
	private int uncooperativeVisitsGrowth;
	@Schema(description = "Number of cooperative visits during the primary time period")
	private int cooperativeVisitsCount;
	@Schema(
		description = "Growth of cooperative visits during the primary time period compared to the number of cooperative visits during the secondary time period in percent")
	private int cooperativeVisitsGrowth;
	@Schema(description = "Number of visits that missed their final date to be conducted during the secondary time period")
	private int previousMissedVisitsCount;
	@Schema(description = "Number of unavailable visits during the secondary time period")
	private int previousUnavailableVisitsCount;
	@Schema(description = "Number of uncooperative visits during the secondary time period")
	private int previousUncooperativeVisitsCount;
	@Schema(description = "Number of cooperative visits during the secondary time period")
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
