package de.symeda.auditlog.api;

/**
 * Interface, um den Entity-Listener für JPA-Entities zu bauen.
 * 
 * @author Oliver Milke
 * @since 14.04.2016
 */
public interface AuditListener {

	/**
	 * Anzuwenden bei
	 * <ul>
	 * <li>PrePersist</li>
	 * <li>PreUpdate</li>
	 * </ul>
	 * Führt den Vergleich des Entity zu einem ursprünglichen Zustand durch.
	 * 
	 * @param o
	 *            Das Entity, das auditiert werden soll.
	 */
	void prePersist(HasUuid o);

	/**
	 * Anzuwenden bei
	 * <ul>
	 * <li>PostLoad</li>
	 * </ul>
	 * Speichert den ursprünglichen Zustand eines Entity für den späteren Vergleich.
	 * 
	 * @param o
	 *            Das Entity, das auditiert werden soll.
	 */
	void postLoad(HasUuid o);

	/**
	 * Anzuwenden bei
	 * <ul>
	 * <li>PreRemove</li>
	 * </ul>
	 * Loggt das Löschen eines Entity.
	 * 
	 * @param o
	 *            Das Entity, das auditiert werden soll.
	 */
	void preRemove(HasUuid o);

}