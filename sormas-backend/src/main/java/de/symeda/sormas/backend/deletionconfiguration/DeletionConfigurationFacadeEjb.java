package de.symeda.sormas.backend.deletionconfiguration;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.deletionconfiguration.DeletionConfigurationFacade;
import de.symeda.sormas.backend.common.CronService;

@Stateless(name = "DeletionConfigurationFacade")
public class DeletionConfigurationFacadeEjb implements DeletionConfigurationFacade {

	@EJB
	private CronService cronService;

	@Override
	@Asynchronous
	public void startAutomaticDeletion() {
		cronService.deleteExpiredEntities();
	}

	@LocalBean
	@Stateless
	public static class DeletionConfigurationFacadeEjbLocal extends DeletionConfigurationFacadeEjb {

	}
}
