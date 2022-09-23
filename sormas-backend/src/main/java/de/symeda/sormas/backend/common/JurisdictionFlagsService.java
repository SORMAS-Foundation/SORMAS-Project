package de.symeda.sormas.backend.common;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

/**
 * Service that provide several jurisdictions flags.
 * 
 * @param <ADO>
 *            JPA entity managed by this Service.
 * @param <JF>
 *            An object (jurisdiction flags) that states if the entity and which relation of an entity
 *            are within the current users jurisdiction or owned by him.
 */
public interface JurisdictionFlagsService<ADO extends AbstractDomainObject, JF, QJ extends QueryJoins<ADO>, QC extends QueryContext<ADO, QJ>> {

	/**
	 * @param entity
	 *            The entity to fetch the jurisdictions flags for.
	 * @return Jurisdiction flags that states if {@code entity} and references are within the current users jurisdiction or owned by him.
	 */
	JF getJurisdictionFlags(ADO entity);

	/**
	 * @param entities
	 *            Entities to check for jurisdictions flags.
	 * @return Jurisdiction flags for each of the entities identified by the entity id.
	 */
	Map<Long, JF> getJurisdictionsFlags(List<ADO> entities);

	/**
	 * @param queryContext
	 *            The current {@link QueryContext} to build the selections.
	 * @return Selections of the jurisdiction flags to be fetched in a query.
	 */
	List<Selection<?>> getJurisdictionSelections(QC queryContext);

	/**
	 * @param queryContext
	 *            The current {@link QueryContext} to build the predicate.
	 * @return The predicate if the root entity is within the current users jurisdiction or owned by him.
	 */
	Predicate inJurisdictionOrOwned(QC queryContext);
}
