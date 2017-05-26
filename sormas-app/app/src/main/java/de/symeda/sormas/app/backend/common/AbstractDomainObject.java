package de.symeda.sormas.app.backend.common;

import android.databinding.BaseObservable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractDomainObject extends BaseObservable implements Serializable, Cloneable  {

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String SNAPSHOT = "snapshot";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String LOCAL_CHANGE_DATE = "localChangeDate";
	public static final String MODIFIED = "modified";

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * This marks the snapshot of a modified entity that was created for merging
	 */
	@DatabaseField(uniqueCombo=true)
	private boolean snapshot = false;

	@DatabaseField(uniqueCombo=true, canBeNull = false, width = 29)
	private String uuid;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date creationDate;

	/**
	 * Date when the entity was last modified on the server
	 */
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date changeDate;

	/**
	 * Date when the entity was last updated from the server or locally modified.
	 */
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false, version = true)
	private Date localChangeDate;

	/**
	 * This entity or any embedded (not referenced) was modified and needs to be send to the server
	 */
	@DatabaseField
	private boolean modified = false;

	@Override
	public AbstractDomainObject clone() {
		try {
			return (AbstractDomainObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
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

	public Date getLocalChangeDate() {
		return localChangeDate;
	}

	public void setLocalChangeDate(Date localChangeDate) {
		this.localChangeDate = localChangeDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (o instanceof AbstractDomainObject) {
			// this works, because we are using UUIDs
			AbstractDomainObject ado = (AbstractDomainObject) o;
			return getUuid().equals(ado.getUuid());

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}
}
