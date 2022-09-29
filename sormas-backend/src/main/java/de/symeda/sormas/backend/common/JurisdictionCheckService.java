package de.symeda.sormas.backend.common;

import java.util.List;

/**
 * Service that provide the jurisdictions flag for the managed entity class.
 * 
 * @param <ADO>
 *            JPA entity managed by this Service.
 */
public interface JurisdictionCheckService<ADO extends AbstractDomainObject> {

	/**
	 * @param entity
	 *            The entity to fetch the jurisdictions flags for.
	 * @return {@code true}, if {@code entity} is within the current users jurisdiction or owned by him.
	 */
	boolean inJurisdictionOrOwned(ADO entity);

	/**
	 * @param entities
	 *            The entities to fetch the jurisdictions flags for.
	 * @return The ids of entities where the jurisdiction flag resulted {@code true}.
	 */
	List<Long> getInJurisdictionIds(List<ADO> entities);
}
