package de.symeda.sormas.api.infrastructure;

import javax.ejb.Remote;

@Remote
public interface InfrastructureFacade {

	InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates);
	
}
