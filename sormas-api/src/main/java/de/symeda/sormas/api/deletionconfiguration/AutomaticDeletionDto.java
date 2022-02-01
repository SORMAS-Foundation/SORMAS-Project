package de.symeda.sormas.api.deletionconfiguration;

import java.io.Serializable;

public class AutomaticDeletionDto implements Serializable {

	private String deletionDate;
	private String endOfProcessing;
	private String deletionPeriod;

	public AutomaticDeletionDto(String deletionDate, String endOfProcessing, String deletionPeriod) {
		this.deletionDate = deletionDate;
		this.endOfProcessing = endOfProcessing;
		this.deletionPeriod = deletionPeriod;
	}

	public String getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(String deletionDate) {
		this.deletionDate = deletionDate;
	}

	public String getEndOfProcessing() {
		return endOfProcessing;
	}

	public void setEndOfProcessing(String endOfProcessing) {
		this.endOfProcessing = endOfProcessing;
	}

	public String getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(String deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}
}
