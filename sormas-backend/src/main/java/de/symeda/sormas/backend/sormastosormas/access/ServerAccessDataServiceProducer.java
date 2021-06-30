package de.symeda.sormas.backend.sormastosormas.access;

import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;

import javax.enterprise.inject.Produces;

public class ServerAccessDataServiceProducer {

	@Produces
	public ServerAccessDataService serverAccessDataService(
		SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb,
		SormasToSormasConfig sormasToSormasConfig) {
		return new ServerAccessDataService(sormasToSormasFacadeEjb, configFacadeEjb, sormasToSormasConfig);
	}
}
