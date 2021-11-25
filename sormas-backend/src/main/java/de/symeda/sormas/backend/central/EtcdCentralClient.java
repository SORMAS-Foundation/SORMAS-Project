package de.symeda.sormas.backend.central;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.protobuf.ByteString;

import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EtcdCentralClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(EtcdCentralClient.class);

	private final ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	private final ObjectMapper mapper = new ObjectMapper();

	public EtcdCentralClient(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
		this.configFacadeEjb = configFacadeEjb;
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	private KvStoreClient createEtcdClient() {
		String[] hostPort = configFacadeEjb.getCentralEtcdHost().split(":");

		URL truststorePath;
		try {
			truststorePath = Paths.get(configFacadeEjb.getCentralEtcdCaPath()).toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("Etcd Url is malformed: %s", e);
			return null;
		}

		KvStoreClient client;
		try {
			client = EtcdClient.forEndpoint(hostPort[0], Integer.parseInt(hostPort[1]))
				.withCredentials(configFacadeEjb.getCentralEtcdClientName(), configFacadeEjb.getCentralEtcdClientPassword())
				.withCaCert(Resources.asByteSource(truststorePath))
				.withMaxInboundMessageSize(6291456) // 6 MB, yes we need it
				.build();
		} catch (IOException e) {
			LOGGER.error("Could not load Etcd CA cert: %s", e);
			return null;
		}

		LOGGER.info("Etcd client created successfully.");
		return client;
	}

	public <T> T get(String key, Class<T> clazz) throws IOException {
		// use resource auto-close
		try (KvStoreClient etcdClient = createEtcdClient()) {
			if (etcdClient == null) {
				LOGGER.error("Etcd could not be accessed.");
				return null;
			}
			KvClient etcd = etcdClient.getKvClient();

			if (etcd == null) {
				LOGGER.error("Could not create an etcd KV client.");
				return null;
			}

			RangeResponse range = etcd.get(ByteString.copyFromUtf8(key)).sync();
			if (range.getCount() == 0) {
				LOGGER.error("There is no value available for key {}", key);
				return null;
			}
			return deserialize(range.getKvs(0), clazz);
		}
	}

	public <T> List<T> getWithPrefix(String path, Class<T> clazz) throws IOException {
		// use resource auto-close
		try (KvStoreClient etcdClient = createEtcdClient()) {
			if (etcdClient == null) {
				LOGGER.error("Etcd could not be accessed.");
				return Collections.emptyList();
			}
			KvClient etcd = etcdClient.getKvClient();

			if (etcd == null) {
				LOGGER.error("Could not create an etcd KV client.");
				return Collections.emptyList();
			}

			return etcd.get(ByteString.copyFromUtf8(path))
				.asPrefix()
				.sync()
				.getKvsList()
				.stream()
				.map(kv -> deserialize(kv, clazz))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		}
	}

	private <T> T deserialize(KeyValue kv, Class<T> clazz) {

		final String content = kv.getValue().toStringUtf8();
		try {
			return mapper.readValue(content, clazz);
		} catch (JsonProcessingException e) {
			LOGGER.error("Could not serialize value {} into object of type {}: %s ", content, clazz.getSimpleName(), e);
			return null;
		}
	}

}
