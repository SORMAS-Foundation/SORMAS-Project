package de.symeda.sormas.api.disease;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;

@Remote
public interface DiseaseConfigurationFacade {
	
	DiseaseConfigurationDto getDiseaseConfiguration(Disease disease);
	
}
