package de.symeda.auditlog.api;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Interface zur Auditierung einer Entity.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public interface AuditedEntity extends HasUuid {

	/**
	 * Liefert den Zustand aller inspizierten Attribute zum Aufrufzeitpunkt. Wird verwendet, um Änderung im Lauf einer Transaktion
	 * festzustellen.
	 */
	ValueContainer inspectAttributes();

}
