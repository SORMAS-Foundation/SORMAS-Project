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

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

public class SormasToSormasDiscoveryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasDiscoveryService.class);

	private final SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb;
	private final ConfigFacadeEjbLocal configFacadeEjb;

	public SormasToSormasDiscoveryService(
            SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb,
            ConfigFacadeEjbLocal configFacadeEjb) {
        this.sormasToSormasFacadeEjb = sormasToSormasFacadeEjb;
        this.configFacadeEjb = configFacadeEjb;
    }

	private RedisCommands<String, String> createRedisConnection() {
		String[] redis = configFacadeEjb.getCentralRedisHost().split(":");
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
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
					Paths.get(configFacadeEjb.getCentralRedisKeystorePath()).toUri().toURL(),
					configFacadeEjb.getCentralRedisKeystorePassword().toCharArray())
				.truststore(
					Paths.get(configFacadeEjb.getCentralRedisTruststorePath()).toUri().toURL(),
					configFacadeEjb.getCentralRedisTruststorePassword())
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

	private SormasServerIdentifier buildSormasServerIdentifier(String id, Map<String, String> entry) {
		return new SormasServerIdentifier(id, entry.get("name"), entry.get("hostName"));
	}

	public Optional<SormasServerIdentifier> getSormasIdentifierById(String id) {
		if (!sormasToSormasFacadeEjb.isFeatureConfigured()) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			return Optional.empty();
		}

		try {
			RedisCommands<String, String> redis = createRedisConnection();
			if (redis == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return Optional.empty();
			}

			Map<String, String> serverAccess = redis.hgetall(String.format(configFacadeEjb.getS2SConfig().getKeyPrefixTemplate(), id));
			return Optional.of(buildSormasServerIdentifier(id, serverAccess));

		} catch (Exception e) {
			LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
			LOGGER.error("Unexpected error while reading sormas to sormas server access data", e);
			return Optional.empty();
		}
	}

	public List<SormasServerIdentifier> getOtherSormasServerIdentifiers() {
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		if (sormasToSormasConfig.getId() == null) {
			return Collections.emptyList();
		}

		try {
			RedisCommands<String, String> redis = createRedisConnection();
			if (redis == null) {
				LOGGER.error((I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
				return Collections.emptyList();
			}

			List<String> keys = redis.keys(String.format(sormasToSormasConfig.getKeyPrefixTemplate(), "*"));

			// remove own Id from the set
			keys.remove(String.format(sormasToSormasConfig.getKeyPrefixTemplate(), sormasToSormasConfig.getId()));

			List<SormasServerIdentifier> list = new ArrayList<>();
			keys.forEach(key -> {
				Map<String, String> hgetAll = redis.hgetall(key);
				SormasServerIdentifier sormasServerIdentifier = buildSormasServerIdentifier(key.split(":")[1], hgetAll);
				list.add(sormasServerIdentifier);
			});
			return list;
		} catch (Exception e) {
			LOGGER.error("Unexpected error while reading sormas to sormas server list", e);
			return Collections.emptyList();
		}
	}
}
