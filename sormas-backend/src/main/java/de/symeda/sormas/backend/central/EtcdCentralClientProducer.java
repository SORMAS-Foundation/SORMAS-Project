package de.symeda.sormas.backend.central;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;

import javax.enterprise.inject.Produces;

public class EtcdCentralClientProducer {

	@Produces
	public EtcdCentralClient etcdCentralClient(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
		return new EtcdCentralClient(configFacadeEjb);
	}
}
