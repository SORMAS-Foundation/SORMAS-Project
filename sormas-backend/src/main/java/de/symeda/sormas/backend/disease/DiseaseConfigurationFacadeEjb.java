package de.symeda.sormas.backend.disease;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
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

	private List<Disease> activeDiseases = new ArrayList<>();
	private List<Disease> primaryDiseases = new ArrayList<>();
	private List<Disease> activePrimaryDiseases = new ArrayList<>();
	private List<Disease> followUpEnabledDiseases = new ArrayList<>();
	private Map<Disease, Integer> followUpDurations = new HashMap<>();

	@Override
	public List<DiseaseConfigurationDto> getAllAfter(Date date) {
		return service.getAllAfter(date, null)
				.stream()
				.map(d -> toDto(d))
				.collect(Collectors.toList());
	}

	@Override
	public List<DiseaseConfigurationDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids)
				.stream()
				.map(d -> toDto(d))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids(null);
	}

	@Override
	public boolean isActiveDisease(Disease disease) {
		return activeDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllActiveDiseases() {
		return activeDiseases;
	}
	
	@Override
	public List<Disease> getAllActiveDiseases(Disease includedDisease) {
		if (includedDisease == null || activeDiseases.contains(includedDisease)) {
			return activeDiseases;
		}
		
		List<Disease> diseases = new ArrayList<>(activeDiseases);
		diseases.add(includedDisease);
		return diseases;
	}

	@Override
	public boolean isPrimaryDisease(Disease disease) {
		return primaryDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllPrimaryDiseases() {
		return primaryDiseases;
	}

	@Override
	public List<Disease> getAllActivePrimaryDiseases() {
		return activePrimaryDiseases;
	}

	@Override
	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseasesWithFollowUp() {
		return followUpEnabledDiseases;
	}

	@Override
	public int getFollowUpDuration(Disease disease) {
		return followUpDurations.get(disease);
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

	private DiseaseConfigurationDto getDiseaseConfiguration(Disease disease) {
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
			return diseaseConfiguration;
		} catch (NoResultException e) {
			return null;
		}
	}

	@PostConstruct
	private void loadData() {
		activeDiseases.clear();
		primaryDiseases.clear();
		activePrimaryDiseases.clear();
		followUpEnabledDiseases.clear();
		followUpDurations.clear();
		
		for (Disease disease : Disease.values()) {
			DiseaseConfigurationDto configuration = getDiseaseConfiguration(disease);

			boolean diseaseActive = false;
			boolean diseasePrimary = false;
			if (Boolean.TRUE.equals(configuration.getActive()) 
					|| (configuration.getActive() == null && disease.isDefaultActive())) {
				activeDiseases.add(disease);
				diseaseActive = true;
			}
			if (Boolean.TRUE.equals(configuration.getPrimaryDisease())
					|| (configuration.getPrimaryDisease() == null && disease.isDefaultPrimary())) {
				primaryDiseases.add(disease);
				diseasePrimary = true;
			}
			if (diseaseActive && diseasePrimary) {
				activePrimaryDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getFollowUpEnabled()) 
					|| (configuration.getFollowUpEnabled() == null && disease.isDefaultFollowUpEnabled())) {
				followUpEnabledDiseases.add(disease);
			}
			if (configuration.getFollowUpDuration() != null) {
				followUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				followUpDurations.put(disease, disease.getDefaultFollowUpDuration());
			}
		}
	}

	@LocalBean
	@Stateless
	public static class DiseaseConfigurationFacadeEjbLocal extends DiseaseConfigurationFacadeEjb {

	}

}
