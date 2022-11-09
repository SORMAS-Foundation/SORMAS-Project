/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.access;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@LocalBean
public class SormasToSormasDiscoveryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasDiscoveryService.class);

	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb;
	@EJB
	private ConfigFacadeEjbLocal configFacadeEjb;
	@Inject
	private EtcdCentralClient centralClient;

	public SormasServerDescriptor getSormasServerDescriptorById(String id) {
		if (!sormasToSormasFacadeEjb.isFeatureConfigured()) {
			LOGGER.error("Tried to invoke getSormasServerDescriptorById() with S2S disabled");
			return null;
		}

		try {
			String key = String.format(configFacadeEjb.getS2SConfig().getKeyPrefixTemplate(), id);
			SormasServerDescriptor descriptor = centralClient.get(key, SormasServerDescriptor.class);

			LOGGER.info("Fetched SormasServerDescriptor for {}.", id);
			return descriptor;

		} catch (Exception e) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			LOGGER.error("Unexpected error while reading SormasServerDescriptor data.", e);
			return null;
		}
	}

	public List<SormasServerDescriptor> getAllAvailableServers() {
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		if (sormasToSormasConfig.getId() == null) {
			return Collections.emptyList();
		}

		try {
			final String keyPrefix = String.format(sormasToSormasConfig.getKeyPrefixTemplate(), "");
			List<SormasServerDescriptor> availableServers = centralClient.getWithPrefix(keyPrefix, SormasServerDescriptor.class)
				.stream()
				.filter(
					// this ensures that the own key (i.e., /s2s/$instance_id) is removed from the list
					d -> !d.getId().equals(sormasToSormasConfig.getId()))
				.collect(Collectors.toList());

			LOGGER.info("All available SormasServerDescriptors have been collected.");
			return availableServers;
		} catch (Exception e) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			LOGGER.error("Unexpected error while reading sormas to sormas server list", e);
			return Collections.emptyList();
		}
	}

}
