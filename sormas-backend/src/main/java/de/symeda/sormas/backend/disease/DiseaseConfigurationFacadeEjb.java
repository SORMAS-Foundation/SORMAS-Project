package de.symeda.sormas.backend.disease;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DiseaseConfigurationFacade")
public class DiseaseConfigurationFacadeEjb implements DiseaseConfigurationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private DiseaseConfigurationService service;
	@EJB
	private UserService userService;

	private List<Disease> activeDiseases = new ArrayList<>();
	private List<Disease> inactiveDiseases = new ArrayList<>();
	private List<Disease> primaryDiseases = new ArrayList<>();
	private List<Disease> nonPrimaryDiseases = new ArrayList<>();
	private List<Disease> caseBasedDiseases = new ArrayList<>();
	private List<Disease> aggregateDiseases = new ArrayList<>();
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
		return service.getAllUuids();
	}

	@Override
	public boolean isActiveDisease(Disease disease) {
		return activeDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseases(Boolean active, Boolean primary, Boolean caseBased) {
		User currentUser = userService.getCurrentUser();

		Set<Disease> diseases = new HashSet<>();

		if (currentUser.getLimitedDisease() == null) {
			if (Boolean.TRUE.equals(active)) {
				diseases.addAll(activeDiseases);
			} else if (Boolean.FALSE.equals(active)) {
				diseases.addAll(inactiveDiseases);
			}

			if (Boolean.TRUE.equals(primary)) {
				diseases.retainAll(primaryDiseases);
			} else if (Boolean.FALSE.equals(primary)) {
				diseases.retainAll(nonPrimaryDiseases);
			}

			if (Boolean.TRUE.equals(caseBased)) {
				diseases.retainAll(caseBasedDiseases);
			} else if (Boolean.FALSE.equals(caseBased)) {
				diseases.retainAll(aggregateDiseases);
			}
		} else if (active != null || primary != null || caseBased != null) {
			Disease limitedDisease = currentUser.getLimitedDisease();
			if ((active == null || (Boolean.TRUE.equals(active) && activeDiseases.contains(limitedDisease)) || (Boolean.FALSE.equals(active) && inactiveDiseases.contains(limitedDisease)))
					&& (primary == null || (Boolean.TRUE.equals(primary) && primaryDiseases.contains(limitedDisease)) || (Boolean.FALSE.equals(primary) && nonPrimaryDiseases.contains(limitedDisease)))
					&& (caseBased == null || (Boolean.TRUE.equals(caseBased) && caseBasedDiseases.contains(limitedDisease)) || (Boolean.FALSE.equals(caseBased) && aggregateDiseases.contains(limitedDisease)))) {
				diseases.add(limitedDisease);
			}
		}

		List<Disease> diseaseList = new ArrayList<>(diseases);
		diseaseList.sort((d1, d2) -> d1.toString().compareTo(d2.toString()));
		return diseaseList;
	}
	
	@Override
	public List<Disease> getAllDiseasesWithFollowUp(Boolean active, Boolean primary, Boolean caseBased) {
		return getAllDiseases(active, primary, caseBased).stream().filter(d -> followUpEnabledDiseases.contains(d)).collect(Collectors.toList());
	}

	@Override
	public List<Disease> getAllActiveDiseases() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return activeDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return activeDiseases;
		}
	}

	@Override
	public boolean isPrimaryDisease(Disease disease) {
		return primaryDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllPrimaryDiseases() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return primaryDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return primaryDiseases;
		}
	}

	@Override
	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseasesWithFollowUp() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return followUpEnabledDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return followUpEnabledDiseases;
		}
	}

	@Override
	public int getFollowUpDuration(Disease disease) {
		return followUpDurations.get(disease);
	}

	@Override
	public void saveDiseaseConfiguration(DiseaseConfigurationDto configuration) {
		service.ensurePersisted(fromDto(configuration));
	}
	
	@Override
	public Disease getDefaultDisease() {
		List<Disease> diseases = getAllDiseases(true, true, true).stream()
				.filter(d -> d != Disease.OTHER && d != Disease.UNDEFINED)
				.collect(Collectors.toList());
		
		if (diseases.size() == 1) {
			return diseases.get(0);
		}
		
		return null;
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
		target.setCaseBased(source.getCaseBased());
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
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());

		return target;
	}

	public DiseaseConfigurationDto getDiseaseConfiguration(Disease disease) {
		return toDto(service.getDiseaseConfiguration(disease));
	}

	@PostConstruct
	public void loadData() {
		activeDiseases.clear();
		inactiveDiseases.clear();
		primaryDiseases.clear();
		nonPrimaryDiseases.clear();
		caseBasedDiseases.clear();
		aggregateDiseases.clear();
		followUpEnabledDiseases.clear();
		followUpDurations.clear();

		for (Disease disease : Disease.values()) {
			DiseaseConfigurationDto configuration = getDiseaseConfiguration(disease);

			if (Boolean.TRUE.equals(configuration.getActive()) 
					|| (configuration.getActive() == null && disease.isDefaultActive())) {
				activeDiseases.add(disease);
			} else {
				inactiveDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getPrimaryDisease())
					|| (configuration.getPrimaryDisease() == null && disease.isDefaultPrimary())) {
				primaryDiseases.add(disease);
			} else {
				nonPrimaryDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getCaseBased())
					|| (configuration.getCaseBased() == null && disease.isDefaultCaseBased())) {
				caseBasedDiseases.add(disease);
			} else {
				aggregateDiseases.add(disease);
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
