package de.symeda.sormas.api.disease;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;

@Remote
public interface DiseaseConfigurationFacade {
	
	List<DiseaseConfigurationDto> getAllAfter(Date date);

	List<DiseaseConfigurationDto> getByUuids(List<String> uuids);
	
	List<String> getAllUuids();
	
	boolean isActiveDisease(Disease disease);
	
	List<Disease> getAllActiveDiseases();
	
	List<Disease> getAllActiveDiseases(Disease includedDisease);
	
	boolean isPrimaryDisease(Disease disease);
	
	List<Disease> getAllPrimaryDiseases();
	
	List<Disease> getAllActivePrimaryDiseases();
	
	boolean hasFollowUp(Disease disease);
	
	List<Disease> getAllDiseasesWithFollowUp();
	
	int getFollowUpDuration(Disease disease);
	
	
}
