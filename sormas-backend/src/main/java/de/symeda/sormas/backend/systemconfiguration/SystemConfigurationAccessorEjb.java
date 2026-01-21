package de.symeda.sormas.backend.systemconfiguration;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationAccessorFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Singleton(name = "SystemConfigurationAccessor")
@Startup
@DependsOn("StartupShutdownService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class SystemConfigurationAccessorEjb implements SystemConfigurationAccessorFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SystemConfigurationValueEjb systemConfigurationValueEjb;

	@Override
	public boolean isPresent(SystemConfigurationType config) {
		return getAsString(config).filter(StringUtils::isNotBlank).isPresent();
	}

	@Override
	public Optional<Integer> getAsInteger(SystemConfigurationType config) {
		return Optional.empty();
	}

	private <T> T parseProperty(String propertyName, T defaultValue, Function<String, T> parse) {

		String prop = systemConfigurationValueEjb.getValue(propertyName);
		if (prop == null) {
			return defaultValue;
		}

		try {
			if (prop.isEmpty()) {
				logger.debug("The property '" + propertyName + "' is set to empty value");
			}

			return parse.apply(prop);
		} catch (Exception e) {
			logger.error("Could not parse value of property '" + propertyName + "': " + e.getMessage());
			return defaultValue;
		}
	}

	@Override
	public Optional<Double> getAsDouble(SystemConfigurationType config) {
		return Optional.empty();
	}

	@Override
	public Optional<Long> getAsLong(SystemConfigurationType config) {
		return Optional.empty();
	}

	@Override
	public Optional<String> getAsString(SystemConfigurationType config) {
		return Optional.empty();
	}

	@Override
	public boolean getAsBoolean(SystemConfigurationType config) {
		return false;
	}

	@Override
	public boolean isConfiguredCountry(String countryCode) {
		String countryLocale = getAsStringOrThrow(SystemConfigurationType.COUNTRY_LOCALE);
		if (Pattern.matches(I18nProperties.FULL_COUNTRY_LOCALE_PATTERN, countryLocale)) {
			return StringUtils.endsWithIgnoreCase(countryLocale, countryCode);
		} else {
			return StringUtils.startsWithIgnoreCase(countryLocale, countryCode);
		}
	}

	@Override
	public String getCountryCode() {
		String locale = getAsStringOrThrow(SystemConfigurationType.COUNTRY_LOCALE);
		String normalizedLocale = normalizeLocaleString(locale);

		if (normalizedLocale.contains("-")) {
			return normalizedLocale.substring(normalizedLocale.lastIndexOf("-") + 1);
		} else {
			return normalizedLocale;
		}
	}

	@Override
	public Set<String> getAllowedFileExtensions() {
		return Arrays.stream(getAsStringOrThrow(SystemConfigurationType.ALLOWED_FILE_EXTENSIONS).split(",")).collect(Collectors.toSet());
	}

	@Override
	public String getSormasInstanceName() {
		return getAsBoolean(SystemConfigurationType.CUSTOM_BRANDING) ? getAsStringOrThrow(SystemConfigurationType.CUSTOM_BRANDING_NAME) : "SORMAS";
	}

	private String normalizeLocaleString(String locale) {
		locale = locale.trim();
		int pos = Math.max(locale.indexOf('-'), locale.indexOf('_'));
		if (pos < 0) {
			locale = locale.toLowerCase();
		} else {
			locale = locale.substring(0, pos).toLowerCase(Locale.ENGLISH) + '-' + locale.substring(pos + 1).toUpperCase(Locale.ENGLISH);
		}
		return locale;
	}

	@Inject
	public void setSystemConfigurationValueEjb(SystemConfigurationValueEjb systemConfigurationValueEjb) {
		this.systemConfigurationValueEjb = systemConfigurationValueEjb;
	}

}
