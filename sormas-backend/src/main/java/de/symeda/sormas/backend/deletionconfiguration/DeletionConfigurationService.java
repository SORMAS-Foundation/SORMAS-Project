package de.symeda.sormas.backend.deletionconfiguration;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DeletionConfigurationService extends BaseAdoService<DeletionConfiguration> {

	public DeletionConfigurationService() {

		super(DeletionConfiguration.class);
	}

	/**
	 * Retrieves the deletion configuration for the specified deletable entity type with deletion reference != DELETION.
	 */
	public DeletionConfiguration getEntityTypeConfig(DeletableEntityType deletableEntityType) {

		return getEntityTypeConfig(deletableEntityType, false);
	}

	/**
	 * Retrieves the deletion configuration for the specified deletable entity type with deletion reference == DELETION.
	 */
	public DeletionConfiguration getEntityTypeManualDeletionConfig(DeletableEntityType deletableEntityType) {

		return getEntityTypeConfig(deletableEntityType, true);
	}

	public List<DeletionConfiguration> getEntityTypeConfigs(DeletableEntityType deletableEntityType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), deletableEntityType));

		return em.createQuery(cq).getResultList();
	}

	private DeletionConfiguration getEntityTypeConfig(DeletableEntityType deletableEntityType, boolean isManualDeletionConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(
			cb.and(
				cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), deletableEntityType),
				isManualDeletionConfig
					? cb.equal(from.get(DeletionConfiguration.DELETION_REFERENCE), DeletionReference.MANUAL_DELETION)
					: cb.notEqual(from.get(DeletionConfiguration.DELETION_REFERENCE), DeletionReference.MANUAL_DELETION)));

		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void createMissingDeletionConfigurations() {

		Map<DeletableEntityType, Map<String, DeletionConfiguration>> configs = getServerDeletionConfigurations();
		Arrays.stream(DeletableEntityType.values()).forEach(deletableEntityType -> {
			Map<String, DeletionConfiguration> savedConfigurations = configs.get(deletableEntityType);

			if (savedConfigurations == null || !savedConfigurations.containsKey(DeletionReference.MANUAL_DELETION.name())) {
				DeletionConfiguration deletionConfiguration = DeletionConfiguration.build(deletableEntityType, DeletionReference.MANUAL_DELETION);
				ensurePersisted(deletionConfiguration);
			}

			if (savedConfigurations == null
				|| savedConfigurations.isEmpty()
				|| savedConfigurations.containsKey(DeletionReference.MANUAL_DELETION.name()) && savedConfigurations.size() == 1) {
				DeletionConfiguration deletionConfiguration = DeletionConfiguration.build(deletableEntityType);
				ensurePersisted(deletionConfiguration);
			}
		});
	}

	private Map<DeletableEntityType, Map<String, DeletionConfiguration>> getServerDeletionConfigurations() {

		List<DeletionConfiguration> deletionConfigurations = getAll();
		return deletionConfigurations.stream()
			.collect(
				Collectors.groupingBy(
					DeletionConfiguration::getEntityType,
					toMap(config -> config.getDeletionReference() != null ? config.getDeletionReference().name() : "", Function.identity())));
	}
}
