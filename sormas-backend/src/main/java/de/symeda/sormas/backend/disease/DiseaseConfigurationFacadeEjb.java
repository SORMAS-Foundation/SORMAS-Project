package de.symeda.sormas.backend.disease;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DiseaseConfigurationFacade")
public class DiseaseConfigurationFacadeEjb implements DiseaseConfigurationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private DiseaseConfigurationService service;

	private Map<Disease, DiseaseConfigurationDto> diseaseConfigurationCache = new HashMap<>();

	@Override
	public DiseaseConfigurationDto getDiseaseConfiguration(Disease disease) {
		if (diseaseConfigurationCache.containsKey(disease)) {
			return diseaseConfigurationCache.get(disease);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseConfiguration> cq = cb.createQuery(DiseaseConfiguration.class);
		Root<DiseaseConfiguration> root = cq.from(DiseaseConfiguration.class);

		Predicate filter = cb.equal(root.get(DiseaseConfiguration.DISEASE), disease);
		if (filter == null) {
			return null;
		} else {
			cq.where(filter);
		}

		cq.select(root);

		try {
			DiseaseConfigurationDto diseaseConfiguration = toDto(em.createQuery(cq).getSingleResult());
			diseaseConfigurationCache.put(disease, diseaseConfiguration);
			return diseaseConfiguration;
		} catch (NoResultException e) {
			return null;
		}
	}

	public static DiseaseConfigurationDto toDto(DiseaseConfiguration source) {
		if (source == null) {
			return null;
		}

		DiseaseConfigurationDto target = new DiseaseConfigurationDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());

		return target;
	}

	public DiseaseConfiguration fromDto(@NotNull DiseaseConfigurationDto source) {
		DiseaseConfiguration target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new DiseaseConfiguration();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());

		return target;
	}

	@LocalBean
	@Stateless
	public static class DiseaseConfigurationFacadeEjbLocal extends DiseaseConfigurationFacadeEjb {

	}

}
