package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfigurationService;

public abstract class AbstractDeletableAdoService<ADO extends DeletableAdo> extends AdoServiceWithUserFilterAndJurisdiction<ADO> {

	private DeletableEntityType entityType;
	@EJB
	private DeletionConfigurationService deletionConfigurationService;

	public AbstractDeletableAdoService(Class<ADO> elementClass, DeletableEntityType entityType) {
		super(elementClass);
		this.entityType = entityType;
	}

	public void delete(ADO ado, DeletionDetails deletionDetails) {

		ado.setDeletionReason(deletionDetails.getDeletionReason());
		ado.setOtherDeletionReason(deletionDetails.getOtherDeletionReason());
		ado.setDeleted(true);
		em.persist(ado);
		em.flush();
	}

	public void restore(ADO ado) {

		ado.setDeletionReason(null);
		ado.setOtherDeletionReason(null);
		ado.setDeleted(false);
		em.persist(ado);
		em.flush();
	}

	public boolean isDeleted(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(cb.and(cb.isTrue(from.get(DeletableAdo.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected <C> Predicate changeDateFilter(CriteriaBuilder cb, Timestamp date, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}
		return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
	}

	public DeletionInfoDto getAutomaticDeletionInfo(String uuid) {
		DeletionConfiguration deletionConfiguration = deletionConfigurationService.getEntityTypeConfig(entityType);

		if (deletionConfiguration == null
			|| deletionConfiguration.getDeletionPeriod() == null
			|| deletionConfiguration.getDeletionReference() == null) {
			return null;
		}

		Date referenceDate = getDeletionReferenceDate(uuid, deletionConfiguration);
		Date deletiondate = DateHelper.addDays(referenceDate, deletionConfiguration.getDeletionPeriod());
		String deletionReferenceField = getDeleteReferenceField(deletionConfiguration.getDeletionReference());
		return new DeletionInfoDto(deletiondate, referenceDate, deletionConfiguration.getDeletionPeriod(), deletionReferenceField);
	}

	public DeletionInfoDto getManuallyDeletionInfo(String uuid) {

		DeletionConfiguration deletionConfiguration = deletionConfigurationService.getEntityTypeManualDeletionConfig(entityType);

		if (deletionConfiguration == null
			|| deletionConfiguration.getDeletionPeriod() == null
			|| deletionConfiguration.getDeletionReference() == null) {
			return null;
		}

		Date referenceDate = getDeletionReferenceDate(uuid, deletionConfiguration);
		Date deletiondate = DateHelper.addDays(referenceDate, deletionConfiguration.getDeletionPeriod());
		String deletionReferenceField = getDeleteReferenceField(deletionConfiguration.getDeletionReference());
		return new DeletionInfoDto(deletiondate, referenceDate, deletionConfiguration.getDeletionPeriod(), deletionReferenceField);
	}

	public List<String> getUuidsForAutomaticDeletion(DeletionConfiguration entityConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ADO> from = cq.from(getElementClass());

		Date referenceDeletionDate = DateHelper.subtractDays(new Date(), entityConfig.getDeletionPeriod());

		Predicate filter = cb.lessThanOrEqualTo(from.get(getDeleteReferenceField(entityConfig.getDeletionReference())), referenceDeletionDate);
		if (entityConfig.getDeletionReference() == DeletionReference.MANUAL_DELETION) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(DeletableAdo.DELETED)));
		}
		cq.where(filter);

		cq.select(from.get(DeletableAdo.UUID));
		cq.distinct(true);

		List<String> toDeleteUuids = em.createQuery(cq).getResultList();
		return toDeleteUuids;
	}

	private Date getDeletionReferenceDate(String uuid, DeletionConfiguration entityConfig) {

		if (entityConfig.getDeletionReference() == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<ADO> from = cq.from(getElementClass());

		cq.select(from.get(getDeleteReferenceField(entityConfig.getDeletionReference())));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuid));

		Object result = em.createQuery(cq).getSingleResult();
		return (Date) result;
	}

	protected String getDeleteReferenceField(DeletionReference deletionReference) {

		switch (deletionReference) {
		case CREATION:
			return AbstractDomainObject.CREATION_DATE;
		case END:
			return CoreAdo.END_OF_PROCESSING_DATE;
		case MANUAL_DELETION:
			return AbstractDomainObject.CHANGE_DATE;
		default:
			throw new IllegalArgumentException("deletion reference " + deletionReference + " not supported in " + getClass().getSimpleName());
		}
	}

}
