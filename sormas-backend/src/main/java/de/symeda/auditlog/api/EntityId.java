package de.symeda.auditlog.api;

/**
 * Klasse zur eindeutigen Unterscheidung von verschiedenen Entities, sowohl hinsichtlich des Entity-Typs als zur Unterscheidung
 * verschiedener Instanzen desselben Entity-Typs.
 *
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class EntityId {

	private final Class<?> entityClass;
	private final String entityUuid;

	public EntityId(Class<?> entityClass, String entityUuid) {
		this.entityClass = entityClass;
		this.entityUuid = entityUuid;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getEntityUuid() {
		return entityUuid;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityClass == null) ? 0 : entityClass.hashCode());
		result = prime * result + ((entityUuid == null) ? 0 : entityUuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EntityId other = (EntityId) obj;
		if (entityClass == null) {
			if (other.entityClass != null) {
				return false;
			}
		} else if (!entityClass.equals(other.entityClass)) {
			return false;
		}
		if (entityUuid == null) {
			if (other.entityUuid != null) {
				return false;
			}
		} else if (!entityUuid.equals(other.entityUuid)) {
			return false;
		}

		return true;
	}

}