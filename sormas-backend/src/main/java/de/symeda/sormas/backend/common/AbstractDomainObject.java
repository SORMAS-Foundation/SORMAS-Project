package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.utils.DataHelper;

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
public abstract class AbstractDomainObject implements Serializable, Cloneable {

	private static final long serialVersionUID = 3957437214306161226L;

	private static final String SEQ_JPA_NAME = "Entity_seq";
	private static final String SEQ_SQL_NAME = "entity_seq";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";

	private Long id;
	private String uuid;
	private Timestamp creationDate;
	private Timestamp changeDate;

	@Override
	public AbstractDomainObject clone() {
		try {
			return (AbstractDomainObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Id
	@SequenceGenerator(name = SEQ_JPA_NAME, allocationSize = 1, sequenceName = SEQ_SQL_NAME)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_JPA_NAME)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic(optional = false)
	@Size(min = 29, max = 29)
	@Column(nullable = false, unique = true, length = 36)
	public String getUuid() {
		if (uuid == null) {
			uuid = DataHelper.createUuid();
		}
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(nullable = false)
	public Timestamp getCreationDate() {
		if (creationDate == null) {
			creationDate = Timestamp.from(Instant.now());
		}
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Version
	@Column(nullable = false)
	public Timestamp getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
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
}
