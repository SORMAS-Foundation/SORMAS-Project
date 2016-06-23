package de.symeda.sormas.api;

import java.io.Serializable;
import java.sql.Timestamp;

public abstract class DataTransferObject implements Serializable {

	private static final long serialVersionUID = -1L;

	public static final String UUID = "uuid";

	private Timestamp changeDate;
	private String uuid;

	public Timestamp getChangeDate() {
		return changeDate;
	}
	
	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
