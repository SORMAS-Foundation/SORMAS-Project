package de.symeda.sormas.api.deletionconfiguration;

import java.io.Serializable;
import java.util.Date;

public class AutomaticDeletionInfoDto implements Serializable {

	private Date deletionDate;
	private Date endOfProcessing;
	private int deletionPeriod;

	public AutomaticDeletionInfoDto(Date deletionDate, Date endOfProcessing, int deletionPeriod) {
		this.deletionDate = deletionDate;
		this.endOfProcessing = endOfProcessing;
		this.deletionPeriod = deletionPeriod;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public Date getEndOfProcessing() {
		return endOfProcessing;
	}

	public void setEndOfProcessing(Date endOfProcessing) {
		this.endOfProcessing = endOfProcessing;
	}

	public int getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(int deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}
}
