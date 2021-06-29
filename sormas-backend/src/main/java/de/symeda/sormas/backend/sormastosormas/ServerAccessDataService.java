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

package de.symeda.sormas.backend.sormastosormas;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SslOptions;
import io.lettuce.core.SslVerifyMode;
import io.lettuce.core.api.sync.RedisCommands;

@Stateless
@LocalBean
public class ServerAccessDataService {

	// todo move this and reject at runtime if setup was wrong
	private static final String S2S_REALM_PREFIX = "s2s:%s";

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerAccessDataService.class);

	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb;

	@EJB
	private ConfigFacadeEjbLocal configFacadeEjb;

	@Inject
	private SormasToSormasConfig sormasToSormasConfig;

	private RedisCommands<String, String> createRedisConnection() {
		String[] redis = configFacadeEjb.getKvRedisHost().split(":");

		RedisURI uri = RedisURI.Builder.redis(redis[0], Integer.parseInt(redis[1]))
			.withAuthentication(sormasToSormasConfig.getRedisClientName(), sormasToSormasConfig.getRedisClientPassword())
			.withSsl(true)
			.withVerifyPeer(SslVerifyMode.FULL)
			.build();

		SslOptions sslOptions;
		try {
			sslOptions = SslOptions.builder()
				.jdkSslProvider()
				.keystore(
					Paths.get(configFacadeEjb.getKvRedisKeystorePath()).toUri().toURL(),
					configFacadeEjb.getKvRedisKeystorePasswor().toCharArray())
				.truststore(Paths.get(configFacadeEjb.getKvRedisTruststorePath()).toUri().toURL(), configFacadeEjb.getKvRedisTruststorePassword())
				.build();
		} catch (MalformedURLException e) {
			LOGGER.error(String.format("Could not load key material to connect to redis: %s", e));
			return null;
		}

		RedisClient redisClient = RedisClient.create(uri);
		ClientOptions clientOptions = ClientOptions.builder().sslOptions(sslOptions).build();
		redisClient.setOptions(clientOptions);
		return redisClient.connect().sync();
	}

	public OrganizationServerAccessData getServerAccessData() {
		if (!sormasToSormasFacadeEjb.isFeatureConfigured()) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			return null;
		}

		try {
			RedisCommands<String, String> redis = createRedisConnection();
			if (redis == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return null;
			}

			Map<String, String> serverAccess = redis.hgetall(String.format(S2S_REALM_PREFIX, sormasToSormasConfig.getId()));
			return buildServerAccessData(sormasToSormasConfig.getId(), serverAccess);

		} catch (Exception e) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			LOGGER.error("Unexpected error while reading sormas to sormas server access data", e);
			return null;
		}
	}

	public Optional<OrganizationServerAccessData> getServerListItemById(String id) {
		return getOrganizationList().stream().filter(i -> i.getId().equals(id)).findFirst();
	}

	private OrganizationServerAccessData buildServerAccessData(String id, Map<String, String> entry) {
		return new OrganizationServerAccessData(id, entry.get("name"), entry.get("hostName"), entry.get("restUserPassword"));
	}

	public List<OrganizationServerAccessData> getOrganizationList() {
		String ownOrganizationId = getServerAccessData().getId();
		if (ownOrganizationId == null) {
			return Collections.emptyList();
		}

		try {
			RedisCommands<String, String> redis = createRedisConnection();
			if (redis == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return Collections.emptyList();
			}
			// todo pin to the same prefix as the scopes s2s:
			List<String> keys = redis.keys(String.format(S2S_REALM_PREFIX, "*"));

			// remove own Id from the set
			keys.remove(String.format(S2S_REALM_PREFIX, sormasToSormasConfig.getId()));

			List<OrganizationServerAccessData> list = new ArrayList<>();
			for (String key : keys) {
				Map<String, String> hgetAll = redis.hgetall(key);
				OrganizationServerAccessData organizationServerAccessData = buildServerAccessData(key.split(":")[1], hgetAll);
				list.add(organizationServerAccessData);
			}
			return list;
		} catch (Exception e) {
			LOGGER.error("Unexpected error while reading sormas to sormas server list", e);
			return Collections.emptyList();
		}

	}

}
