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

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
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

@Stateless
@LocalBean
public class SormasToSormasDiscoveryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasDiscoveryService.class);

	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb;
	@EJB
	private ConfigFacadeEjbLocal configFacadeEjb;

	private KvStoreClient createEtcdClient() throws IOException {
		String[] hostPort = configFacadeEjb.getCentralEtcdHost().split(":");
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();

		URL truststorePath;
		try {
			truststorePath = Paths.get(configFacadeEjb.getCentralEtcdCaPath()).toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("Etcd Url is malformed: %s", e);
			throw e;
		}

		KvStoreClient client;
		try {
			client = EtcdClient.forEndpoint(hostPort[0], Integer.parseInt(hostPort[1]))
				.withCredentials(sormasToSormasConfig.getEtcdClientName(), sormasToSormasConfig.getEtcdClientPassword())
				.withCaCert(Resources.asByteSource(truststorePath))
				.build();
		} catch (IOException e) {
			LOGGER.error("Could not load Etcd CA cert: %s", e);
			throw e;
		}

		LOGGER.info("Etcd client created successfully.");
		return client;
	}

	private SormasServerDescriptor buildSormasServerDescriptor(KeyValue kv) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		try {
			return mapper.readValue(kv.getValue().toStringUtf8(), SormasServerDescriptor.class);
		} catch (JsonProcessingException e) {
			LOGGER.error("Could not serialize server descriptor.");
			return null;
		}
	}

	public SormasServerDescriptor getSormasServerDescriptorById(String id) {
		if (!sormasToSormasFacadeEjb.isFeatureConfigured()) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			return null;
		}

		// use resource auto-close
		try (KvStoreClient etcdClient = createEtcdClient()) {
			KvClient etcd = etcdClient.getKvClient();

			if (etcd == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return null;
			}

			String key = String.format(configFacadeEjb.getS2SConfig().getKeyPrefixTemplate(), id);
			KeyValue result = etcd.get(ByteString.copyFromUtf8(key)).sync().getKvsList().get(0);

			SormasServerDescriptor descriptor = buildSormasServerDescriptor(result);
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

		// use resource auto-close
		try (KvStoreClient etcdClient = createEtcdClient()) {
			KvClient etcd = etcdClient.getKvClient();

			if (etcd == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return Collections.emptyList();
			}

			final String ownKey = String.format(sormasToSormasConfig.getKeyPrefixTemplate(), sormasToSormasConfig.getId());
			final String keyPrefix = String.format(sormasToSormasConfig.getKeyPrefixTemplate(), "");
			List<SormasServerDescriptor> availableServers = etcd.get(ByteString.copyFromUtf8(keyPrefix))
				.asPrefix()
				.sync()
				.getKvsList()
				.stream()
				.filter(
					// this ensures that the own key (i.e., /s2s/$instance_id) is removed from the list
					kv -> !kv.getKey().toStringUtf8().equals(ownKey))
				.map(this::buildSormasServerDescriptor)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			LOGGER.info("All available SormasServerDescriptors have been collected.");
			return availableServers;
		} catch (Exception e) {
			LOGGER.error("Unexpected error while reading sormas to sormas server list", e);
			return Collections.emptyList();
		}
	}
}
