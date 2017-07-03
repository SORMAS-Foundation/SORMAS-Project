package de.symeda.auditlog.api;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Interface for entity auditing.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public interface AuditedEntity extends HasUuid {

	/**
	 * Provides the state of all inspected attributes at the time of calling. Used to detect changes in the course of a transaction.
	 */
	ValueContainer inspectAttributes();

}
