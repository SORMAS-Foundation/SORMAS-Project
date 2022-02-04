package de.symeda.sormas.backend.deletionconfiguration;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

public abstract class AbstractCoreEntityFacade<T extends CoreAdo> {

	private final Class<T> entityClass;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@Inject
	private DeletionConfigurationService deletionConfigurationService;

	@Inject
	protected AbstractCoreEntityFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public void executeAutomaticDeletion(DeletionConfiguration entityConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> from = cq.from(entityClass);

		Date referenceDeletionDate = DateHelper.subtractDays(new Date(), entityConfig.deletionPeriod);
		cq.where(cb.lessThanOrEqualTo(from.get(getDeleteReferenceField(entityConfig.deletionReference)), referenceDeletionDate));

		List<T> toDeleteEntities = QueryHelper.getResultList(em, cq, null, null);

		toDeleteEntities.forEach(this::delete);
	}

	public AutomaticDeletionInfoDto getAutomaticDeletionInfo(String uuid, CoreEntityType coreEntityType) {
		DeletionConfiguration deletionConfiguration = deletionConfigurationService.getCoreEntityTypeConfig(coreEntityType);
		if (deletionConfiguration.deletionPeriod == null || deletionConfiguration.deletionReference == null) {
			return null;
		}
		Object[] deletionData = getDeletionData(uuid, deletionConfiguration);
		Date referenceDate = (Date) deletionData[0];
		Date deletiondate = DateHelper.addDays(referenceDate, deletionConfiguration.deletionPeriod);
		return new AutomaticDeletionInfoDto(deletiondate, (Date) deletionData[1], deletionConfiguration.deletionPeriod);
	}

	protected abstract void delete(T entity);

	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		switch (deletionReference) {
		case CREATION:
			return AbstractDomainObject.CREATION_DATE;
		case END:
			return AbstractDomainObject.CHANGE_DATE;
		default:
			throw new IllegalArgumentException("deletion reference " + deletionReference + " not supported in " + getClass().getSimpleName());
		}
	}

	private Object[] getDeletionData(String uuid, DeletionConfiguration entityConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<T> from = cq.from(entityClass);

		cq.multiselect(from.get(getDeleteReferenceField(entityConfig.deletionReference)), from.get(AbstractDomainObject.CHANGE_DATE));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuid));

		return em.createQuery(cq).getSingleResult();
	}
}
