package de.symeda.sormas.backend.patch.alias;

import static de.symeda.sormas.backend.patch.PatchFieldHelper.PATH_SEPARATOR;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.patch.PathFailureCause;

/**
 * Users want to be able to use multiple root objects, to avoid drilling through properties.
 * Therefore, this class performs this manual conversion.
 * <p>
 * FieldId from Data dictionary to DTO's physical path.
 */
@ApplicationScoped
public class PathAliasHelper {

	private final static Logger logger = LoggerFactory.getLogger(PathAliasHelper.class);

	public static final Map<String, String> DEFAULT_ALIAS_DICTIONARY = buildDefaultAliasDictionary();

	/**
	 * Can be used as OUT: for displaying purposes but not for in.
	 */
	public static final Map<String, Set<String>> DEFAULT_FORBIDDEN_ALIASES_DICTIONARY = Map.of(
		"Location",
		Set.of(toFieldName(PersonDto.I18N_PREFIX, PersonDto.ADDRESS), toFieldName(ExposureDto.I18N_PREFIX, ExposureDto.LOCATION)));

	/**
	 * Meant for fields that are only references from another entity.
	 */
	public static final Map<String, String> REFERENCE_TO_ROOT_DICTIONARY = Map.of(
		toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON),
		PersonDto.I18N_PREFIX);

	private static @NotNull HashMap<String, String> buildDefaultAliasDictionary() {
		HashMap<String, String> dictionary = new HashMap<>();

		dictionary.put(SymptomsDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS));
		dictionary.put(HealthConditionsDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_CONDITIONS));
		dictionary.put(HospitalizationDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.HOSPITALIZATION));
		dictionary.put(
			PreviousHospitalizationDto.I18N_PREFIX,
			toFieldName(toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.HOSPITALIZATION), HospitalizationDto.PREVIOUS_HOSPITALIZATIONS));
		dictionary.put(PersonContactDetailDto.I18N_PREFIX, toFieldName(PersonDto.I18N_PREFIX, PersonDto.PERSON_CONTACT_DETAILS));
		dictionary.put(FacilityDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
		dictionary.put(PointOfEntryDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.POINT_OF_ENTRY));
		dictionary.put(RegionDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_REGION));
		dictionary.put(DistrictDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT));
		dictionary.put(CommunityDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_COMMUNITY));
		dictionary.put(CountryDto.I18N_PREFIX, toFieldName(PersonDto.I18N_PREFIX, PersonDto.BIRTH_COUNTRY));
		dictionary.put(SubcontinentDto.I18N_PREFIX, toFieldName(toFieldName(PersonDto.I18N_PREFIX, PersonDto.ADDRESS), LocationDto.SUB_CONTINENT));
		dictionary.put(ContinentDto.I18N_PREFIX, toFieldName(toFieldName(PersonDto.I18N_PREFIX, PersonDto.ADDRESS), LocationDto.CONTINENT));
		dictionary.put(UserDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER));

		dictionary.put(EpiDataDto.I18N_PREFIX, toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA));

		// TODO: list seem to not work well | will probably be removed to only match singular and covered by businessDtoFacade
		dictionary.put(ExposureDto.I18N_PREFIX, toFieldName(toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA), EpiDataDto.EXPOSURES));
		dictionary.put(ActivityAsCaseDto.I18N_PREFIX, toFieldName(toFieldName(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA), EpiDataDto.ACTIVITIES_AS_CASE));

		return dictionary;
	}

	private static String toFieldName(String prefix, String fieldName) {
		return prefix + '.' + fieldName;
	}

	@NotNull
	public Tuple<String, PathFailureCause> resolveAlias(String pathWithPotentialAlias) {
		int firstPathSeparatorIndex = pathWithPotentialAlias.indexOf(PATH_SEPARATOR);

		if (firstPathSeparatorIndex == -1) {
			return tupleWithoutFailure(pathWithPotentialAlias);
		}

		String aliasCandidate = pathWithPotentialAlias.substring(0, firstPathSeparatorIndex);

		Set<String> collisions = DEFAULT_FORBIDDEN_ALIASES_DICTIONARY.get(aliasCandidate);
		if (CollectionUtils.isNotEmpty(collisions)) {
			logger.info("Alias [{}] with collisions: [{}] used for path as [{}]", pathWithPotentialAlias, collisions, aliasCandidate);
			return tupleWithFailure(PathFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS);
		}

		String pathWithFixedRootObjectReferences = REFERENCE_TO_ROOT_DICTIONARY.values()
			.stream()
			.reduce(
				pathWithPotentialAlias,
				(path, replacement) -> path.replace(
					REFERENCE_TO_ROOT_DICTIONARY.entrySet().stream().filter(entry -> replacement.equals(entry.getValue())).findFirst().get().getKey(),
					replacement));

		String physicalPathPrefix = DEFAULT_ALIAS_DICTIONARY.get(aliasCandidate);
		if (physicalPathPrefix != null) {
			return tupleWithoutFailure(physicalPathPrefix + pathWithFixedRootObjectReferences.substring(firstPathSeparatorIndex));
		}

		return tupleWithoutFailure(pathWithFixedRootObjectReferences);
	}

	/**
	 * Objective is to retrieve a path WITH an alias to get the Field ID as in the data dictionary.
	 *
	 * @param path
	 *            field that may or may not contain a "physical-path" that must be "shortened" to the Field ID.
	 * @return path that is "shortened" to a path using the Field ID (alias).
	 */
	public String toAliasPath(String path) {
		Set<Map.Entry<String, String>> reduce = Stream.concat(
			DEFAULT_ALIAS_DICTIONARY.entrySet().stream(),
			DEFAULT_FORBIDDEN_ALIASES_DICTIONARY.entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream().map(replacementPath -> new AbstractMap.SimpleEntry<>(entry.getKey(), replacementPath))))
			.collect(Collectors.toSet());

		for (Map.Entry<String, String> entry : reduce) {
			path = path.replace(entry.getValue(), entry.getKey());
		}

		for (Map.Entry<String, String> entry : REFERENCE_TO_ROOT_DICTIONARY.entrySet()) {
			path = path.replace(entry.getKey(), entry.getValue());
		}

		return path;
	}

	private static @NotNull Tuple<String, PathFailureCause> tupleWithFailure(PathFailureCause forbiddenNonUniqueAlias) {
		return new Tuple<>(null, forbiddenNonUniqueAlias);
	}

	private static @NotNull Tuple<String, PathFailureCause> tupleWithoutFailure(String pathWithPotentialAlias) {
		return new Tuple<>(pathWithPotentialAlias, null);
	}

	public Set<String> supportedPrefixes() {
		return Stream
			.concat(
				Stream.concat(REFERENCE_TO_ROOT_DICTIONARY.values().stream(), Stream.of(CaseDataDto.I18N_PREFIX))
					.map(prefix -> prefix + PATH_SEPARATOR),
				DEFAULT_ALIAS_DICTIONARY.keySet().stream())
			.collect(Collectors.toSet());

	}
}
