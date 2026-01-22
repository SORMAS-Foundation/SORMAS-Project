package de.symeda.sormas.backend.central;

import javax.enterprise.inject.Produces;

import de.symeda.sormas.backend.systemconfiguration.ConfigFacadeEjbLocal;

public class EtcdCentralClientProducer {

	@Produces
	public EtcdCentralClient etcdCentralClient(ConfigFacadeEjbLocal configFacadeEjb) {
		return new EtcdCentralClient(configFacadeEjb);
	}
}
