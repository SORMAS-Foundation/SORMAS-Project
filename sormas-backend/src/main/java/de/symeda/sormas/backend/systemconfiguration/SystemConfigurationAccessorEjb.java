package de.symeda.sormas.backend.systemconfiguration;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.systemconfiguration.SystemConfiguration;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationAccessorFacade;
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
	public boolean isPresent(SystemConfiguration config) {
		return getAsString(config).filter(StringUtils::isNotBlank).isPresent();
	}

	@Override
	public Optional<Integer> getAsInteger(SystemConfiguration config) {
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
	public Optional<Double> getAsDouble(SystemConfiguration config) {
		return Optional.empty();
	}

	@Override
	public Optional<Long> getAsLong(SystemConfiguration config) {
		return Optional.empty();
	}

	@Override
	public Optional<String> getAsString(SystemConfiguration config) {
		return Optional.empty();
	}

	@Override
	public boolean getAsBoolean(SystemConfiguration config) {
		return false;
	}

	@Override
	public String getCountryCode() {
		String locale = getAsStringOrThrow(SystemConfiguration.COUNTRY_LOCALE);
		String normalizedLocale = normalizeLocaleString(locale);

		if (normalizedLocale.contains("-")) {
			return normalizedLocale.substring(normalizedLocale.lastIndexOf("-") + 1);
		} else {
			return normalizedLocale;
		}
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

	public SystemConfigurationValueEjb getSystemConfigurationValueEjb() {
		return systemConfigurationValueEjb;
	}

	public void setSystemConfigurationValueEjb(SystemConfigurationValueEjb systemConfigurationValueEjb) {
		this.systemConfigurationValueEjb = systemConfigurationValueEjb;
	}

// more complex configurations

//    @Override
//    public boolean isConfiguredCountry(String countryCode) {
//        return false;
//    }
//
//    @Override
//    public GeoLatLon getCountryCenter() {
//        return null;
//    }
//
//    @Override
//    public SymptomJournalConfig getSymptomJournalConfig() {
//        return null;
//    }
//
//    @Override
//    public PatientDiaryConfig getPatientDiaryConfig() {
//        return null;
//    }
//
//    @Override
//    public SormasToSormasConfig getS2SConfig() {
//        return null;
//    }
//
//    @Override
//    public CaseClassificationCalculationMode getCaseClassificationCalculationMode(Disease disease) {
//        return null;
//    }

}
