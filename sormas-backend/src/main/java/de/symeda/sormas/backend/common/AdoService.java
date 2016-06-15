package de.symeda.sormas.backend.common;

import java.util.List;

import javax.persistence.EntityExistsException;

import de.symeda.sormas.backend.common.AbstractDomainObject;

public interface AdoService<ADO extends AbstractDomainObject> {

	List<ADO> getAll();

	ADO getById(long id);

	ADO getByUuid(String uuid);

//	/**
//	 * @deprecated Das ist ein Hibernate-spezifisches Feature
//	 * @param saveme
//	 */
//	@Deprecated
//	void saveOrUpdate(ADO saveme);

	/**
	 * <b>DELETES</b> an entity from the database!
	 * 
	 * @param deleteme
	 */
	void delete(ADO deleteme);

	/**
	 * @deprecated re-attachen eines detachten Entities ist eher die Ausnahme
	 * @param mergeme
	 * @return
	 */
	@Deprecated
	ADO merge(ADO mergeme);

	/**
	 * Speichert ein neues Objekt in der Datenbank.
	 * Es darf vorher keine id haben und ist danch attacht.
	 */
	void persist(ADO persistme);

	/**
	 * Zum Speichern, wenn das ado neu oder direkt aus der Datenbank ist.<br/>
	 * Ruft persist() für neue Entities auf,
	 * bei attachten wird nichts gemacht.
	 * <br/>
	 * Das ado ist nach dem Aufruf attacht.
	 * 
	 * @param ado
	 * @throws EntityExistsException
	 *             wenn das ado detacht ist
	 */
	void ensurePersisted(ADO ado) throws EntityExistsException;

	/**
	 * JPA-Session flushen
	 */
	void doFlush();

}