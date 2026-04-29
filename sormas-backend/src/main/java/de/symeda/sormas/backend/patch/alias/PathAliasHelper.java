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

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
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
	public static final Map<String, Set<String>> DEFAULT_FORBIDDEN_ALIASES_DICTIONARY =
		Map.of("Location", Set.of("Person.address", "Exposure.location"));

	public static final Map<String, String> REFERENCE_TO_ROOT_DICTIONARY = Map.of("CaseData.person", PersonDto.I18N_PREFIX);

	private static @NotNull HashMap<String, String> buildDefaultAliasDictionary() {
		HashMap<String, String> dictionary = new HashMap<>();

		dictionary.put("Symptoms", "CaseData.symptoms");
		dictionary.put("HealthConditions", "CaseData.healthConditions");
		dictionary.put("Hospitalization", "CaseData.hospitalization");
		dictionary.put("PreviousHospitalization", "CaseData.hospitalization.previousHospitalizations");
		dictionary.put("PersonContactDetail", "Person.personContactDetails");
		dictionary.put("Facility", "CaseData.healthFacility");
		dictionary.put("PointOfEntry", "CaseData.pointOfEntry");
		dictionary.put("Region", "CaseData.responsibleRegion");
		dictionary.put("District", "CaseData.responsibleDistrict");
		dictionary.put("Community", "CaseData.responsibleCommunity");
		dictionary.put("Country", "Person.birthCountry");
		dictionary.put("Subcontinent", "Person.address.subcontinent");
		dictionary.put("Continent", "Person.address.continent");
		dictionary.put("User", "CaseData.followUpStatusChangeUser");

		return dictionary;
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
	 * @param pathWithoutAlias
	 *            field that may or may not contain a "physical-path" that must be "shortened" to the Field ID.
	 * @return path that is "shortened" to a path using the Field ID (alias).
	 */
	public String toAliasPath(String pathWithoutAlias) {
		Set<Map.Entry<String, String>> reduce = Stream.concat(
			DEFAULT_ALIAS_DICTIONARY.entrySet().stream(),
			DEFAULT_FORBIDDEN_ALIASES_DICTIONARY.entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream().map(replacementPath -> new AbstractMap.SimpleEntry<>(entry.getKey(), replacementPath))))
			.collect(Collectors.toSet());

		for (Map.Entry<String, String> entry : reduce) {
			pathWithoutAlias = pathWithoutAlias.replace(entry.getValue(), entry.getKey());
		}

		for (Map.Entry<String, String> entry : REFERENCE_TO_ROOT_DICTIONARY.entrySet()) {
			pathWithoutAlias = pathWithoutAlias.replace(entry.getKey(), entry.getValue());
		}

		return pathWithoutAlias;
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
