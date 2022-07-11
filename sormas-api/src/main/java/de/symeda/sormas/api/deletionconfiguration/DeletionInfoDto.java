package de.symeda.sormas.api.deletionconfiguration;

import java.io.Serializable;
import java.util.Date;

public class DeletionInfoDto implements Serializable {

	private Date deletionDate;
	private Date referenceDate;
	private int deletionPeriod;

	public DeletionInfoDto(Date deletionDate, Date referenceDate, int deletionPeriod) {
		this.deletionDate = deletionDate;
		this.referenceDate = referenceDate;
		this.deletionPeriod = deletionPeriod;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public int getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(int deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}
}
