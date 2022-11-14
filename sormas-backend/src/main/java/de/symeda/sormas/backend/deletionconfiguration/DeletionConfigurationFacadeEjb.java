package de.symeda.sormas.backend.deletionconfiguration;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.deletionconfiguration.DeletionConfigurationFacade;

@Stateless(name = "DeletionConfigurationFacade")
public class DeletionConfigurationFacadeEjb implements DeletionConfigurationFacade {

	@EJB
	private CoreEntityDeletionService coreEntityDeletionService;

	@Override
	@Asynchronous
	public void executeAutomaticDeletion() {
		coreEntityDeletionService.executeAutomaticDeletion();
	}

	@LocalBean
	@Stateless
	public static class DeletionConfigurationFacadeEjbLocal extends DeletionConfigurationFacadeEjb {

	}
}
