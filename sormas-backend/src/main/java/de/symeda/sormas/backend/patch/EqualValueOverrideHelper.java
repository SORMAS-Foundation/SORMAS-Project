package de.symeda.sormas.backend.patch;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.jetbrains.annotations.NotNull;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.util.StringNormalizer;

/**
 * Due to values being sometime set by external sources (Lab messages) in a non-optimal manner,
 * some fields have "fallback"-values that should be allowed to be overridden.
 */
@ApplicationScoped
public class EqualValueOverrideHelper {

	public static final String ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY = "ALLOWED_EQUALITY_VALUE_OVERRIDE";

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	/**
	 * If current value and new value to patch are different, (depending on config, see below) it must be checked if override is still
	 * allowed.
	 * {@link de.symeda.sormas.api.patch.DataReplacementStrategy#IF_NOT_ALREADY_PRESENT}
	 * 
	 * @param value
	 *            **current** value that is different from the new value in the system.
	 * @return true: means the value even can be overridden, false: cannot be overridden because not part of exceptions.
	 */
	public boolean allowedOverride(Object value) {
		String valueAsString = StringNormalizer.normalize(value.toString());

		Set<String> allowedEqualityOverrides = resolveConfiguredForbiddenFields();

		return allowedEqualityOverrides.contains(valueAsString)
			|| allowedEqualityOverrides.contains(getValueAsStringWithTypePrefix(value, valueAsString));
	}

	private static @NotNull String getValueAsStringWithTypePrefix(Object value, String valueAsString) {
		return StringNormalizer.normalize(String.format("%s___%s", value.getClass().getSimpleName(), valueAsString));
	}

	private Set<String> resolveConfiguredForbiddenFields() {
		String configValue = systemConfigurationValueFacade.getValue(ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY);
		return Optional.ofNullable(configValue)
			.filter(candidate -> !candidate.isBlank())
			.map(
				candidate -> Arrays.stream(candidate.split(","))
					.map(StringNormalizer::normalize)
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toSet()))
			.orElse(Collections.EMPTY_SET);
	}
}
