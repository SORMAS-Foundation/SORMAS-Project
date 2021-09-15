package de.symeda.sormas.backend.central;

import com.google.common.io.Resources;
import com.google.protobuf.ByteString;

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
import java.util.stream.Collectors;

public class EtcdCentralClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(EtcdCentralClient.class);

	private final ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	public EtcdCentralClient(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
		this.configFacadeEjb = configFacadeEjb;
	}

	private KvStoreClient createEtcdClient() throws IOException {
		String[] hostPort = configFacadeEjb.getCentralEtcdHost().split(":");


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
				.withCredentials(configFacadeEjb.getCentralEtcdClientName(), configFacadeEjb.getCentralEtcdClientPassword())
				.withCaCert(Resources.asByteSource(truststorePath))
				.build();
		} catch (IOException e) {
			LOGGER.error("Could not load Etcd CA cert: %s", e);
			throw e;
		}

		LOGGER.info("Etcd client created successfully.");
		return client;
	}

	public List<KeyValue> getWithPrefix(String path) throws IOException {
		// use resource auto-close
		try (KvStoreClient etcdClient = createEtcdClient()) {
			KvClient etcd = etcdClient.getKvClient();

			if (etcd == null) {
				LOGGER.error("Could not create an etcd KV client.");
				return Collections.emptyList();
			}
			return etcd.get(ByteString.copyFromUtf8(path)).asPrefix().sync().getKvsList().stream().map(KeyValue::new).collect(Collectors.toList());

		}

	}

	public static class KeyValue {

		String key;
		String value;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}

		KeyValue(com.ibm.etcd.api.KeyValue keyValue) {
			this.key = keyValue.getKey().toStringUtf8();
			this.value = keyValue.getValue().toStringUtf8();
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
