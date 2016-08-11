package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.utils.DateAdapter;

public abstract class DataTransferObject implements Serializable {

	private static final long serialVersionUID = -1L;

	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String UUID = "uuid";

	private Date creationDate;
	private Date changeDate;
	private String uuid;

	public Date getCreationDate() {
		return creationDate;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getChangeDate() {
		return changeDate;
	}
	
	@XmlJavaTypeAdapter(DateAdapter.class)
	public void setChangeDate(Date changeDate) {
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


