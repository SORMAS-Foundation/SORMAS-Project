package de.symeda.sormas.backend.disease;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DiseaseConfigurationFacade")
public class DiseaseConfigurationFacadeEjb implements DiseaseConfigurationFacade {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

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

	private Map<Disease, Boolean> extendedClassificationDiseases = new EnumMap<>(Disease.class);
	private Map<Disease, Boolean> extendedClassificationMultiDiseases = new EnumMap<>(Disease.class);

	private Map<Disease, Integer> followUpDurations = new EnumMap<>(Disease.class);
	private Map<Disease, Integer> caseFollowUpDurations = new EnumMap<>(Disease.class);
	private Map<Disease, Integer> eventParticipantFollowUpDurations = new EnumMap<>(Disease.class);

	@Override
	public List<DiseaseConfigurationDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream().map(d -> toDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<DiseaseConfigurationDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(d -> toDto(d)).collect(Collectors.toList());
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
	public List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseBased) {

		// not an ideal solution, but will be changed with #9629 anyway
		if (!caseBased && primary != null) {
			primary = null;
			logger.warn("primary should not be used for non-case-based diseases");
		}

		User currentUser = userService.getCurrentUser();

		Set<Disease> diseases = EnumSet.noneOf(Disease.class);

		if (caseBased) {
			if (currentUser.getLimitedDisease() != null) {
				Disease limitedDisease = currentUser.getLimitedDisease();
				diseases.add(limitedDisease);
			} else {
				diseases.addAll(caseBasedDiseases);
			}

			if (isTrue(primary)) {
				diseases.retainAll(primaryDiseases);
			} else if (isFalse(primary)) {
				diseases.retainAll(nonPrimaryDiseases);
			}
		} else {
			diseases.addAll(aggregateDiseases);
		}

		if (isTrue(active)) {
			diseases.retainAll(activeDiseases);
		} else if (isFalse(active)) {
			diseases.retainAll(inactiveDiseases);
		}

		return diseases.stream().sorted(Comparator.comparing(Disease::toString)).collect(Collectors.toList());
	}

	private static boolean isFalse(Boolean value) {
		return Boolean.FALSE.equals(value);
	}

	private static boolean isTrue(Boolean value) {
		return Boolean.TRUE.equals(value);
	}

	@Override
	public List<Disease> getAllDiseasesWithFollowUp(Boolean active, Boolean primary, boolean caseBased) {
		return getAllDiseases(active, primary, caseBased).stream().filter(d -> followUpEnabledDiseases.contains(d)).collect(Collectors.toList());
	}

	@Override
	public boolean usesExtendedClassification(Disease disease) {
		return extendedClassificationDiseases.get(disease);
	}

	@Override
	public boolean usesExtendedClassificationMulti(Disease disease) {
		return extendedClassificationMultiDiseases.get(disease);
	}

	@Override
	public List<Disease> getAllActiveDiseases() {
		return activeDiseases;
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
		target.setCaseFollowUpDuration(source.getCaseFollowUpDuration());
		target.setEventParticipantFollowUpDuration(source.getEventParticipantFollowUpDuration());
		target.setExtendedClassification(source.getExtendedClassification());
		target.setExtendedClassificationMulti(source.getExtendedClassificationMulti());
		target.setAgeGroups(source.getAgeGroups());

		return target;
	}

	@Override
	public int getCaseFollowUpDuration(Disease disease) {
		return caseFollowUpDurations.get(disease);
	}

	@Override
	public int getEventParticipantFollowUpDuration(Disease disease) {
		return eventParticipantFollowUpDurations.get(disease);
	}

	@Override
	public void saveDiseaseConfiguration(DiseaseConfigurationDto configuration) {
		service.ensurePersisted(fromDto(configuration, true));
	}

	@Override
	public Disease getDefaultDisease() {

		List<Disease> diseases =
			getAllDiseases(true, true, true).stream().filter(d -> d != Disease.OTHER && d != Disease.UNDEFINED).collect(Collectors.toList());

		if (diseases.size() == 1) {
			return diseases.get(0);
		}

		return null;
	}

	@Override
	public List<String> getAgeGroups(Disease disease) {
		return service.getDiseaseConfiguration(disease).getAgeGroups();
	}

	@Override
	public String getFirstAgeGroup(Disease disease) {
		return getAgeGroups(disease) != null ? getAgeGroups(disease).get(0) : null;
	}

	public DiseaseConfiguration fromDto(@NotNull DiseaseConfigurationDto source, boolean checkChangeDate) {

		DiseaseConfiguration target =
			DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), DiseaseConfiguration::new, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());
		target.setCaseFollowUpDuration(source.getCaseFollowUpDuration());
		target.setEventParticipantFollowUpDuration(source.getEventParticipantFollowUpDuration());
		target.setExtendedClassification(source.getExtendedClassification());
		target.setExtendedClassificationMulti(source.getExtendedClassificationMulti());
		target.setAgeGroups(source.getAgeGroups());

		return target;
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
		extendedClassificationDiseases.clear();
		extendedClassificationMultiDiseases.clear();
		caseFollowUpDurations.clear();
		eventParticipantFollowUpDurations.clear();

		for (DiseaseConfiguration configuration : service.getAll()) {
			Disease disease = configuration.getDisease();

			if (enabled(configuration.getActive(), disease.isDefaultActive())) {
				activeDiseases.add(disease);
			} else {
				inactiveDiseases.add(disease);
			}
			if (enabled(configuration.getPrimaryDisease(), disease.isDefaultPrimary())) {
				primaryDiseases.add(disease);
			} else {
				nonPrimaryDiseases.add(disease);
			}
			if (enabled(configuration.getCaseBased(), disease.isDefaultCaseBased())) {
				caseBasedDiseases.add(disease);
			} else {
				aggregateDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getFollowUpEnabled())
				|| (configuration.getFollowUpEnabled() == null && disease.isDefaultFollowUpEnabled())) {
				followUpEnabledDiseases.add(disease);
			}

			if (configuration.getExtendedClassification() == null) {
				extendedClassificationDiseases.put(disease, disease.isDefaultExtendedClassification());
			} else {
				extendedClassificationDiseases.put(disease, configuration.getExtendedClassification());
			}

			if (configuration.getExtendedClassificationMulti() == null) {
				extendedClassificationMultiDiseases.put(disease, disease.isDefaultExtendedClassificationMulti());
			} else {
				extendedClassificationMultiDiseases.put(disease, configuration.getExtendedClassificationMulti());
			}

			if (configuration.getFollowUpDuration() != null) {
				followUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				followUpDurations.put(disease, disease.getDefaultFollowUpDuration());
			}
			if (configuration.getCaseFollowUpDuration() != null) {
				caseFollowUpDurations.put(disease, configuration.getCaseFollowUpDuration());
			} else {
				caseFollowUpDurations.put(disease, followUpDurations.get(disease));
			}
			if (configuration.getFollowUpDuration() != null) {
				eventParticipantFollowUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				eventParticipantFollowUpDurations.put(disease, followUpDurations.get(disease));
			}
		}
	}

	private boolean enabled(Boolean configValue, boolean defaultValue) {
		return isTrue(configValue) || (configValue == null && defaultValue);
	}

	@LocalBean
	@Stateless
	public static class DiseaseConfigurationFacadeEjbLocal extends DiseaseConfigurationFacadeEjb {

	}
}
