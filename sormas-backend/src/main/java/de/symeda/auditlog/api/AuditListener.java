package de.symeda.auditlog.api;

/**
 * Interface to build the entity listener for JPA entities.
 * 
 * @author Oliver Milke
 * @since 14.04.2016
 */
public interface AuditListener {

	/**
	 * To be used for
	 * <ul>
	 * <li>PrePersist</li>
	 * <li>PreUpdate</li>
	 * </ul>
	 * Performs the comparison of the entity with its original state.
	 * 
	 * @param o
	 * 			The entity to be audited.
	 */
	void prePersist(HasUuid o);

	/**
	 * To be used for
	 * <ul>
	 * <li>PostLoad</li>
	 * </ul>
	 * Saves the original state of an entity for later comparison.
	 * 
	 * @param o
	 * 			The entity to be audited.
	 */
	void postLoad(HasUuid o);

	/**
	 * To be used for
	 * <ul>
	 * <li>PreRemove</li>
	 * </ul>
	 * Logs the deleting of an entity.
	 * 
	 * @param o
	 * 			The entity to be audited.
	 */
	void preRemove(HasUuid o);

}