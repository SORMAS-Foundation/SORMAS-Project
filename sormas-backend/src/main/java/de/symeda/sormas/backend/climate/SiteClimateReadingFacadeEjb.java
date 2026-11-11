/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.climate.SiteClimateReadingFacade;
import de.symeda.sormas.api.climate.SiteClimateReadingValidation;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SiteClimateReadingFacade")
public class SiteClimateReadingFacadeEjb implements SiteClimateReadingFacade {

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private SiteClimateReadingService siteClimateReadingService;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private UserService userService;

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<SiteClimateReadingDto> getReadings(EnvironmentReferenceDto environmentRef, LocalDate from, LocalDate to) {

		if (environmentRef == null || from == null || to == null || from.isAfter(to)) {
			throw new IllegalArgumentException("Site readings are listed for an environment and a period");
		}
		Environment environment = environmentService.getByUuid(environmentRef.getUuid());
		if (environment == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.climateEntryEnvironmentNotFound, environmentRef.getUuid()));
		}
		return siteClimateReadingService.getByEnvironment(environment, from, to)
			.stream()
			.map(SiteClimateReadingFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_EDIT)
	public SiteClimateReadingDto saveReading(SiteClimateReadingDto dto) {

		checkClimateEnabled();
		String error = SiteClimateReadingValidation.validate(dto, new Date());
		if (error != null) {
			throw new ValidationRuntimeException(error);
		}

		SiteClimateReading reading = siteClimateReadingService.getByUuid(dto.getUuid());
		Environment environment = environmentService.getByUuid(dto.getEnvironment().getUuid());
		if (environment == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.climateEntryEnvironmentNotFound, dto.getEnvironment().getUuid()));
		}
		if (reading != null && !reading.getEnvironment().getId().equals(environment.getId())) {
			// a reading stays with the environment it was taken at
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateReadingEnvironment));
		}
		checkEditAllowed(environment);

		if (reading == null) {
			reading = new SiteClimateReading();
			reading.setUuid(StringUtils.isNotBlank(dto.getUuid()) ? dto.getUuid() : null);
			reading.setEnvironment(environment);
			reading.setReportingUser(userService.getCurrentUser());
		}
		reading.setReadingDateTime(dto.getReadingDateTime());
		reading.setAirTemperature(dto.getAirTemperature());
		reading.setRainfall(dto.getRainfall());
		reading.setRainfallPeriodHours(dto.getRainfall() != null ? dto.getRainfallPeriodHours() : null);
		reading.setRelativeHumidity(dto.getRelativeHumidity());
		reading.setNotes(StringUtils.trimToNull(dto.getNotes()));
		siteClimateReadingService.ensurePersisted(reading);
		return toDto(reading);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_EDIT)
	public void deleteReading(String readingUuid) {

		checkClimateEnabled();
		SiteClimateReading reading = siteClimateReadingService.getByUuid(readingUuid);
		if (reading == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateReadingNotFound));
		}
		checkEditAllowed(reading.getEnvironment());
		siteClimateReadingService.deletePermanent(reading);
	}

	/**
	 * Readings follow the environment's own edit rules: in the user's jurisdiction, not archived (unless editing archived
	 * entities is switched on) and not deleted.
	 */
	private void checkEditAllowed(Environment environment) {
		if (environment.isDeleted() || !environmentService.isEditAllowed(environment)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateReadingNotEditable));
		}
	}

	private void checkClimateEnabled() {
		if (!climateSettings.isClimateEnabled()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateFeatureOff));
		}
	}

	static SiteClimateReadingDto toDto(SiteClimateReading reading) {
		SiteClimateReadingDto dto = new SiteClimateReadingDto();
		dto.setUuid(reading.getUuid());
		dto.setCreationDate(reading.getCreationDate());
		dto.setChangeDate(reading.getChangeDate());
		dto.setEnvironment(EnvironmentFacadeEjb.toReferenceDto(reading.getEnvironment()));
		dto.setReadingDateTime(reading.getReadingDateTime());
		dto.setAirTemperature(reading.getAirTemperature());
		dto.setRainfall(reading.getRainfall());
		dto.setRainfallPeriodHours(reading.getRainfallPeriodHours());
		dto.setRelativeHumidity(reading.getRelativeHumidity());
		dto.setNotes(reading.getNotes());
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(reading.getReportingUser()));
		return dto;
	}

	@LocalBean
	@Stateless
	public static class SiteClimateReadingFacadeEjbLocal extends SiteClimateReadingFacadeEjb {
	}
}
