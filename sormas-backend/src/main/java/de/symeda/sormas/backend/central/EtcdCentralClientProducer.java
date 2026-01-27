package de.symeda.sormas.backend.central;

import javax.enterprise.inject.Produces;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class EtcdCentralClientProducer {

	@Produces
	public EtcdCentralClient etcdCentralClient(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
		return new EtcdCentralClient(configFacadeEjb);
	}
}
