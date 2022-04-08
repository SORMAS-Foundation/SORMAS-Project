package de.symeda.sormas.backend.deletionconfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DeletionConfigurationService extends BaseAdoService<DeletionConfiguration> {

	public DeletionConfigurationService() {

		super(DeletionConfiguration.class);
	}

	/**
	 * Retrieves the deletion configuration for the specified core entity type with deletion reference != DELETION.
	 */
	public DeletionConfiguration getCoreEntityTypeConfig(CoreEntityType coreEntityType) {

		return getCoreEntityTypeConfig(coreEntityType, false);
	}

	/**
	 * Retrieves the deletion configuration for the specified core entity type with deletion reference == DELETION.
	 */
	public DeletionConfiguration getCoreEntityTypeManualDeletionConfig(CoreEntityType coreEntityType) {

		return getCoreEntityTypeConfig(coreEntityType, true);
	}

	public List<DeletionConfiguration> getCoreEntityTypeConfigs(CoreEntityType coreEntityType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), coreEntityType));

		return em.createQuery(cq).getResultList();
	}

	private DeletionConfiguration getCoreEntityTypeConfig(CoreEntityType coreEntityType, boolean isManualDeletionConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(
			cb.and(
				cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), coreEntityType),
				isManualDeletionConfig
					? cb.equal(from.get(DeletionConfiguration.DELETION_REFERENCE), DeletionReference.MANUAL_DELETION)
					: cb.notEqual(from.get(DeletionConfiguration.DELETION_REFERENCE), DeletionReference.MANUAL_DELETION)));

		return em.createQuery(cq).getSingleResult();
	}

	public void createMissingDeletionConfigurations() {

		Map<CoreEntityType, Map<String, DeletionConfiguration>> configs = getServerDeletionConfigurations();
		Arrays.stream(CoreEntityType.values()).forEach(coreEntityType -> {
			Map<String, DeletionConfiguration> savedConfigurations = configs.get(coreEntityType);

			if (savedConfigurations == null || !savedConfigurations.containsKey(DeletionReference.MANUAL_DELETION.name())) {
				DeletionConfiguration deletionConfiguration = DeletionConfiguration.build(coreEntityType, DeletionReference.MANUAL_DELETION);
				ensurePersisted(deletionConfiguration);
			}

			if (savedConfigurations == null
				|| savedConfigurations.isEmpty()
				|| savedConfigurations.containsKey(DeletionReference.MANUAL_DELETION.name()) && savedConfigurations.size() == 1) {
				DeletionConfiguration deletionConfiguration = DeletionConfiguration.build(coreEntityType);
				ensurePersisted(deletionConfiguration);
			}
		});
	}

	private Map<CoreEntityType, Map<String, DeletionConfiguration>> getServerDeletionConfigurations() {

		List<DeletionConfiguration> deletionConfigurations = getAll();
		return deletionConfigurations.stream()
			.collect(
				Collectors.toMap(
					DeletionConfiguration::getEntityType,
					config -> Map.of(config.getDeletionReference() != null ? config.getDeletionReference().name() : "", config)));
	}
}
