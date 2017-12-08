package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Date;

public abstract class EntityDto implements Serializable, Cloneable, HasUuid {

	private static final long serialVersionUID = -1L;

	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String UUID = "uuid";

	private Date creationDate;
	private Date changeDate;
	private String uuid;

	public EntityDto() {
		
	}
	
	public EntityDto(Date creationDate, Date changeDate, String uuid) {
		this.creationDate = creationDate;
		this.changeDate = changeDate;
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getChangeDate() {
		return changeDate;
	}
	
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	
	@Override
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

		if (getUuid() != null && o instanceof HasUuid
				&& ((HasUuid) o).getUuid() != null) {
			// this works, because we are using UUIDs
			HasUuid ado = (HasUuid) o;
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
	
	@Override
	public EntityDto clone() throws CloneNotSupportedException {
		if (getUuid() == null) {
			throw new CloneNotSupportedException("DataTransferObject must have uuid in order to be cloneable");
		}
		return (EntityDto) super.clone();
	}
}


