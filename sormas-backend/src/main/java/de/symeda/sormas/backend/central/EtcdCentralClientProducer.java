package de.symeda.sormas.backend.central;

import javax.enterprise.inject.Produces;

import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationAccessorEjb;

public class EtcdCentralClientProducer {

	@Produces
	public EtcdCentralClient etcdCentralClient(SystemConfigurationAccessorEjb configFacadeEjb) {
		return new EtcdCentralClient(configFacadeEjb);
	}
}
