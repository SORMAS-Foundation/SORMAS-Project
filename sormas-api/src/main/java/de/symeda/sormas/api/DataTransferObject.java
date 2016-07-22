package de.symeda.sormas.api;

import java.io.Serializable;
import java.sql.Timestamp;

public abstract class DataTransferObject implements Serializable {

	private static final long serialVersionUID = -1L;

	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String UUID = "uuid";

	private Timestamp creationDate;
	private Timestamp changeDate;
	private String uuid;

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (getUuid() != null && o instanceof DataTransferObject 
				&& ((DataTransferObject) o).getUuid() != null) {
			// this works, because we are using UUIDs
			DataTransferObject ado = (DataTransferObject) o;
			return getUuid().equals(ado.getUuid());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (getUuid() != null) {
			return getUuid().hashCode();
		}
		return 0;
	}
}
