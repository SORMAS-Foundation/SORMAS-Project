package de.symeda.auditlog.api;

import javax.persistence.Transient;

public interface HasUuid {

	/**
	 * Liefert eine Identifikationsmöglichkeit für diesen Entity-Typ, damit Objekte dieses Typs eindeutig voneinander unterschieden werden
	 * können.
	 */
	String getUuid();

	/**
	 * Liefert die Object-Id zur Unterscheidung von unterschiedlichen Entity-Typen und unterschiedlichen Instanzen desselben Entity-Typs.
	 * <p/>
	 * Verwendet im Default-Modus {@link #getClass()} und {@link #getUuid()}.
	 */
	@Transient
	default EntityId getOid() {

		return new EntityId(this.getClass(), this.getUuid());
	}

}
