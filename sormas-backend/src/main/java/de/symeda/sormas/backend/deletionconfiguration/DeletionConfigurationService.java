package de.symeda.sormas.backend.deletionconfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DeletionConfigurationService extends BaseAdoService<DeletionConfiguration> {


	public DeletionConfigurationService() {
		super(DeletionConfiguration.class);
	}

	public DeletionConfiguration getCoreEntityTypeConfig(CoreEntityType coreEntityType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), coreEntityType));

		return em.createQuery(cq).getSingleResult();
	}

	public void createMissingDeletionConfiguration() {
		Map<CoreEntityType, DeletionConfiguration> configs = getServerDeletionConfigurations();
		Arrays.stream(CoreEntityType.values()).forEach(coreEntityType -> {
			DeletionConfiguration savedConfiguration = configs.get(coreEntityType);
			if (savedConfiguration == null) {
				DeletionConfiguration deletionConfiguration = DeletionConfiguration.build(coreEntityType);
				ensurePersisted(deletionConfiguration);
			}
		});
	}

	private Map<CoreEntityType, DeletionConfiguration> getServerDeletionConfigurations() {
		List<DeletionConfiguration> deletionConfigurations = getAll();
		Map<CoreEntityType, DeletionConfiguration> deletionConfigurationMap =
			deletionConfigurations.stream().collect(Collectors.toMap(DeletionConfiguration::getEntityType, Function.identity(), (e1, e2) -> e2));
		return deletionConfigurationMap;
	}
}
