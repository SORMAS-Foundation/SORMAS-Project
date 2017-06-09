package de.symeda.auditlog.api;

import javax.persistence.Transient;

public interface HasUuid {

	/**
	 * Returns an identification possibility for this entity type so that objects of this type can be uniquely differentiated from each other.
	 */
	String getUuid();

	/**
	 * Returns the object ID to differentiate various entity types and instances of the same entity type.
	 * <p/>
	 * Uses {@link #getClass()} and {@link #getUuid()} in the default mode.
	 * @return
	 */
	@Transient
	default EntityId getOid() {

		return new EntityId(this.getClass(), this.getUuid());
	}

}
