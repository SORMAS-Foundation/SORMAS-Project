package de.symeda.sormas.backend.sormastosormas.access;

import javax.enterprise.inject.Produces;

import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;

public class SormasToSormasDiscoveryServiceProducer {

	@Produces
	public SormasToSormasDiscoveryService sormasToSormasDiscoveryService(
		SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb,
		SormasToSormasConfig sormasToSormasConfig) {
		return new SormasToSormasDiscoveryService(sormasToSormasFacadeEjb, configFacadeEjb, sormasToSormasConfig);
	}
}
