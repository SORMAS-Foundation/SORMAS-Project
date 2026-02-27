package de.symeda.sormas.backend.patch;

import java.util.Set;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;

@ApplicationScoped
public class PatchFieldHelper {

	private final static Logger logger = LoggerFactory.getLogger(PatchFieldHelper.class);

	public static final String CASE_DATA_PREFIX = "CaseData.";
	public static final String PERSON_PREFIX = "Person.";

	private static final String OPENING_PARENTHESIS = "(";
	private static final String CLOSING_PARENTHESIS = ")";
	private static final String PIPE = "|";

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	// TODO: must be twofold: enforced default fields : technical: uuid, user ... + custom config by admin
	private Set<String> forbiddenFields = Set.of("Person.birthdate", "Person.birthdateDD", "Person.birthdateMM", "Person.birthdateYYYY");

	private Set<String> allowedPrefixes = Set.of(PERSON_PREFIX, CASE_DATA_PREFIX);

	@Nullable
	public DataPatchFailureCause checkIfPathIsInvalid(String path) {
		DataPatchFailureCause dataPatchFailureCause = null;

		if (!startsWithAllowedPrefix(path)) {
			dataPatchFailureCause = DataPatchFailureCause.UNSUPPORTED_PREFIX;
		} else if (fieldIsForbidden(path)) {
			dataPatchFailureCause = DataPatchFailureCause.FORBIDDEN_FIELD;
		} else if (fieldIsInvalidMultiField(path)) {
			dataPatchFailureCause = DataPatchFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT;
		}
		return dataPatchFailureCause;
	}

	private boolean fieldIsForbidden(String path) {
		return forbiddenFields.contains(path);
	}

	private boolean startsWithAllowedPrefix(String path) {
		return allowedPrefixes.stream().anyMatch(path::startsWith);
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
