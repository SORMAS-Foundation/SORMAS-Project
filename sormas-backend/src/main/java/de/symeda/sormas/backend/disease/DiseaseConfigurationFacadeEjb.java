/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditIgnore;
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

	private static final List<Disease> activeDiseases = new ArrayList<>();
	private static final List<Disease> inactiveDiseases = new ArrayList<>();
	private static final List<Disease> primaryDiseases = new ArrayList<>();
	private static final List<Disease> nonPrimaryDiseases = new ArrayList<>();
	private static final List<Disease> caseSurveillanceDiseases = new ArrayList<>();
	private static final List<Disease> aggregateReportingDiseases = new ArrayList<>();
	private static final List<Disease> followUpEnabledDiseases = new ArrayList<>();

	private static final Map<Disease, Boolean> extendedClassificationDiseases = new EnumMap<>(Disease.class);
	private static final Map<Disease, Boolean> extendedClassificationMultiDiseases = new EnumMap<>(Disease.class);

	private static final Map<Disease, Integer> followUpDurations = new EnumMap<>(Disease.class);
	private static final Map<Disease, Integer> caseFollowUpDurations = new EnumMap<>(Disease.class);
	private static final Map<Disease, Integer> eventParticipantFollowUpDurations = new EnumMap<>(Disease.class);
	private static final Map<Disease, Integer> automaticSampleAssignmentThresholds = new EnumMap<>(Disease.class);

	@Override
	@PermitAll
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
	@AuditIgnore
	public List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance) {
		return getAllDiseases(active, primary, caseSurveillance, false);
	}

	@Override
	public List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance, boolean aggregateReporting) {

		User currentUser = userService.getCurrentUser();

		Set<Disease> diseases = EnumSet.noneOf(Disease.class);

		if (caseSurveillance) {
			if (CollectionUtils.isNotEmpty(currentUser.getLimitedDiseases())) {
				diseases.addAll(currentUser.getLimitedDiseases());
			} else {
				diseases.addAll(caseSurveillanceDiseases);
			}

			if (isTrue(primary)) {
				diseases.retainAll(primaryDiseases);
			} else if (isFalse(primary)) {
				diseases.retainAll(nonPrimaryDiseases);
			}
		}

		if (aggregateReporting) {
			diseases.addAll(aggregateReportingDiseases);
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
	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseasesWithFollowUp() {

		User currentUser = userService.getCurrentUser();
		if (CollectionUtils.isNotEmpty(currentUser.getLimitedDiseases())) {
			return followUpEnabledDiseases.stream().filter(currentUser.getLimitedDiseases()::contains).collect(Collectors.toList());
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
		target.setCaseSurveillanceEnabled(source.getCaseSurveillanceEnabled());
		target.setAggregateReportingEnabled(source.getAggregateReportingEnabled());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());
		target.setCaseFollowUpDuration(source.getCaseFollowUpDuration());
		target.setEventParticipantFollowUpDuration(source.getEventParticipantFollowUpDuration());
		target.setExtendedClassification(source.getExtendedClassification());
		target.setExtendedClassificationMulti(source.getExtendedClassificationMulti());
		target.setAgeGroups(source.getAgeGroups());
		target.setAutomaticSampleAssignmentThreshold(source.getAutomaticSampleAssignmentThreshold());

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
	public Integer getAutomaticSampleAssignmentThreshold(Disease disease) {
		return automaticSampleAssignmentThresholds.get(disease);
	}

	@Override
	public void saveDiseaseConfiguration(DiseaseConfigurationDto configuration) {
		DiseaseConfiguration existingDiseaseConfiguration = service.getByUuid(configuration.getUuid());
		service.ensurePersisted(fillOrBuildEntity(configuration, existingDiseaseConfiguration, true));
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

	public DiseaseConfiguration fillOrBuildEntity(@NotNull DiseaseConfigurationDto source, DiseaseConfiguration target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, DiseaseConfiguration::new, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseSurveillanceEnabled(source.getCaseSurveillanceEnabled());
		target.setAggregateReportingEnabled(source.getAggregateReportingEnabled());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());
		target.setCaseFollowUpDuration(source.getCaseFollowUpDuration());
		target.setEventParticipantFollowUpDuration(source.getEventParticipantFollowUpDuration());
		target.setExtendedClassification(source.getExtendedClassification());
		target.setExtendedClassificationMulti(source.getExtendedClassificationMulti());
		target.setAgeGroups(source.getAgeGroups());
		target.setAutomaticSampleAssignmentThreshold(source.getAutomaticSampleAssignmentThreshold());

		return target;
	}

	@PostConstruct
	public void loadData() {
		activeDiseases.clear();
		inactiveDiseases.clear();
		primaryDiseases.clear();
		nonPrimaryDiseases.clear();
		caseSurveillanceDiseases.clear();
		aggregateReportingDiseases.clear();
		followUpEnabledDiseases.clear();
		followUpDurations.clear();
		extendedClassificationDiseases.clear();
		extendedClassificationMultiDiseases.clear();
		caseFollowUpDurations.clear();
		eventParticipantFollowUpDurations.clear();
		automaticSampleAssignmentThresholds.clear();

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
			if (enabled(configuration.getCaseSurveillanceEnabled(), disease.isDefaultCaseSurveillanceEnabled())) {
				caseSurveillanceDiseases.add(disease);
			}
			if (enabled(configuration.getAggregateReportingEnabled(), disease.isDefaultAggregateReportingEnabled())) {
				aggregateReportingDiseases.add(disease);
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
			if (configuration.getAutomaticSampleAssignmentThreshold() != null) {
				automaticSampleAssignmentThresholds.put(disease, configuration.getAutomaticSampleAssignmentThreshold());
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
