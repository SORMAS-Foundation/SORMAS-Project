package de.symeda.sormas.api.caze;

import javax.ejb.Remote;

@Remote
public interface HospitalizationFacade {
	
	HospitalizationDto saveHospitalization(HospitalizationDto dto);
	
	HospitalizationDto getHospitalizationByUuid(String uuid);
	
}
