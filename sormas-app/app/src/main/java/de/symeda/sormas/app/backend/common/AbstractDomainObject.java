package de.symeda.sormas.app.backend.common;

import android.databinding.BaseObservable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * TODO: Übersetzung und UUID anpassen
 * 
 * Neue Objekte sollen automatisch eine UUID erhalten. Diese soll schon vor dem Speichern über getUuid() zurückgegeben werden
 * können. Die Erzeugung von UUIDs ist relativ zeitaufwändig. Die meisten Objekte werden aus der Datenbank geladen. Daher
 * sollte für diese Objekte keine UUIDs erzeugt werden. Außerdem verträgt sich das nicht mit lazy instance loading: Die UUIDs
 * werden später nicht überschrieben. Lösung: getUuid() erstellt ggf. eine UUID & der ADO-Interceptor ruft vor dem Speichern
 * getUuid() auf. Dann kann das auch gleich mit getDate() gemacht werden.
 * 
 * @author reise
 */
@MappedSuperclass
public abstract class AbstractDomainObject extends BaseObservable implements Serializable, Cloneable, DomainObject  {

	public static final String ID = "id";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String LOCAL_CHANGE_DATE = "localChangeDate";

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * uniqueCombo is used, because we plan to keep a second copy to be able to merge this
	 * TODO really use this
	 * for now this is only used to determine which data needs to be send to the server
	 */
	@DatabaseField(uniqueCombo=true)
	private boolean modified;

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
	 * Date when the entity was last updated from the server
	 * or locally modified.
	 */
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false, version = true)
	private Date localChangeDate;

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

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	@Override
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

	@Override
	public Date getCreation() {
		return getCreationDate();
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
	public Date getVersion() {
		return getLocalChangeDate();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (o instanceof DomainObject) {
			// this works, because we are using UUIDs
			DomainObject ado = (DomainObject) o;
			return getUuid().equals(ado.getUuid());

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}
}
