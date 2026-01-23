package de.symeda.sormas.backend.central;

import javax.enterprise.inject.Produces;

import de.symeda.sormas.api.ConfigFacade;

public class EtcdCentralClientProducer {

	@Produces
	public EtcdCentralClient etcdCentralClient(ConfigFacade configFacadeEjb) {
		return new EtcdCentralClient(configFacadeEjb);
	}
}
