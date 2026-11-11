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

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.feature.ServerFeatureChangedEvent;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;

/**
 * Reacts to the climate feature being switched on or off:
 * <ul>
 * <li>On: links all districts and environments to climate locations, so the climate screens show them at once. If this
 * fails the feature stays on and the error is logged; the nightly fetch links them again before it runs.</li>
 * <li>On and off: regenerates the district import template, whose climate point columns depend on the feature. Import
 * templates are otherwise only generated at server start.</li>
 * </ul>
 */
@Stateless
@LocalBean
public class ClimateFeatureObserver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ClimateLinkingService climateLinkingService;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void onServerFeatureChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) ServerFeatureChangedEvent event) {

		if (event.getFeatureType() != FeatureType.CLIMATE_MANAGEMENT) {
			return;
		}

		try {
			importFacade.generateDistrictImportTemplateFile(featureConfigurationFacade.getActiveServerFeatureConfigurations());
		} catch (IOException | RuntimeException e) {
			logger.error("Could not regenerate the district import template after switching climate data {}", event.isEnabled() ? "on" : "off", e);
		}

		if (!event.isEnabled()) {
			return;
		}

		try {
			int districts = climateLinkingService.linkAllDistricts();
			int environments = climateLinkingService.linkAllEnvironments();
			logger.info("Climate data switched on: linked {} districts and {} environments to climate locations", districts, environments);
		} catch (RuntimeException e) {
			logger.error("Linking districts and environments after switching on climate data failed; the nightly run will retry", e);
		}
	}
}
