package de.symeda.sormas.backend.patch;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.patch.alias.PathAliasHelper;

@ApplicationScoped
public class PatchFieldHelper {

	private final static Logger logger = LoggerFactory.getLogger(PatchFieldHelper.class);

	public static final String PATH_SEPARATOR = ".";

	public static final String CASE_DATA_PREFIX = "CaseData.";
	public static final String PERSON_PREFIX = "Person.";

	private static final String OPENING_PARENTHESIS = "(";
	private static final String CLOSING_PARENTHESIS = ")";
	private static final String PIPE = "|";

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	// TODO: must be twofold: enforced default fields : technical: uuid, user ... + custom config by admin
	private Set<String> forbiddenFields = Set.of("Person.birthdate", "Person.birthdateDD", "Person.birthdateMM", "Person.birthdateYYYY");

	@Inject
	private PathAliasHelper pathAliasHelper;

	public PatchFieldHelper() {
	}

	public PatchFieldHelper(PathAliasHelper pathAliasHelper) {
		this.pathAliasHelper = pathAliasHelper;
	}

	public Set<Tuple<String, PathFailureCause>> extractFieldTuples(Set<String> fields, Set<String> additionalSupportedPrefixes) {
		return fields.stream().map(path -> Tuple.of(path, checkIfPathIsInvalidImpl(path, additionalSupportedPrefixes))).flatMap(tuple -> {

			String originalPath = tuple.getFirst();

			if (tuple.getSecond() != null || !isMultipleFieldFormat(originalPath)) {
				return Stream.of(tuple);
			}

			return splitMultipleFieldsPath(originalPath).map(splitPath -> Tuple.of(splitPath, (PathFailureCause) null));

		}).collect(Collectors.toSet());
	}

	@NotNull
	private Stream<String> splitMultipleFieldsPath(String path) {
		int openingParenthesisIndex = path.indexOf("(");
		String prefix = path.substring(0, openingParenthesisIndex);

		int closeParen = path.indexOf(')');

		String restPath = path.substring(openingParenthesisIndex + 1, closeParen);

		return Arrays.stream(restPath.split("\\|")).map(suffix -> prefix + suffix);
	}

	@Nullable
	public PathFailureCause checkIfPathIsInvalid(String path) {
		return checkIfPathIsInvalidImpl(path, Set.of());
	}

	private PathFailureCause checkIfPathIsInvalidImpl(String path, Set<String> additionalSupportedPrefixes) {
		PathFailureCause dataPatchFailureCause = null;

		// TODO: check it would be required to distinguish read / write: per example Immunization can be read and write but write does not reach this code.
		if (!path.contains(PATH_SEPARATOR)) {
			dataPatchFailureCause = PathFailureCause.INVALID_PATH_FORMAT;
		} else if (!startsWithAllowedPrefix(path) || pathStartsWithPrefix(path, additionalSupportedPrefixes)) {
			dataPatchFailureCause = PathFailureCause.UNSUPPORTED_PREFIX;
		} else if (fieldIsForbidden(path)) {
			dataPatchFailureCause = PathFailureCause.FORBIDDEN_FIELD;
		} else if (fieldIsInvalidMultiField(path)) {
			dataPatchFailureCause = PathFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT;
		}
		return dataPatchFailureCause;
	}

	@NotNull
	public Tuple<String, PathFailureCause> resolveAlias(String pathWithPotentialAlias) {
		return pathAliasHelper.resolveAlias(pathWithPotentialAlias);
	}

	private boolean fieldIsForbidden(String path) {
		return forbiddenFields.contains(path);
	}

	private boolean startsWithAllowedPrefix(String path) {
		return pathStartsWithPrefix(path, pathAliasHelper.supportedPrefixes());
	}

	private static boolean pathStartsWithPrefix(String path, Set<String> strings) {
		return strings.stream().anyMatch(path::startsWith);
	}

	public boolean isMultipleFieldFormat(String path) {
		return path.contains(OPENING_PARENTHESIS) || path.contains(CLOSING_PARENTHESIS) || path.contains(PIPE);
	}

	private boolean fieldIsInvalidMultiField(String path) {
		if (!isMultipleFieldFormat(path)) {
			return false;
		}

		long openCount = path.chars().filter(c -> c == '(').count();
		long closeCount = path.chars().filter(c -> c == ')').count();
		long pipeCount = path.chars().filter(c -> c == '|').count();
		int openIndex = path.indexOf('(');
		int closeIndex = path.lastIndexOf(')');

		if (openCount != 1 || closeCount != 1) {
			logger.debug("Path must contain exactly one pair of parentheses: [" + path + "]");
			return true;
		}

		if (pipeCount == 0) {
			logger.debug("No pipe found [" + path + "]");
			return true;
		}

		if (openIndex > closeIndex) {
			logger.debug("Closing parenthesis appears before opening parenthesis: [" + path + "]");
			return true;
		}

		if (closeIndex != path.length() - 1) {
			logger.debug("Closing parenthesis must be at the end of the path: [" + path + "]");
			return true;
		}

		String alternatives = path.substring(openIndex + 1, closeIndex);

		if (alternatives.isBlank()) {
			logger.debug("Empty parentheses — nothing between '(' and ')': [" + path + "]");
			return true;
		}

		String[] parts = alternatives.split("\\|");
		for (String part : parts) {
			if (part.isBlank()) {
				logger.debug("Empty alternative found — consecutive or leading/trailing pipes: [" + path + "]");
				return true;
			}
		}

		return false;
	}
}
