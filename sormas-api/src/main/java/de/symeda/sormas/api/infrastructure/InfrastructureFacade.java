package de.symeda.sormas.api.infrastructure;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface InfrastructureFacade {

	InfrastructureSyncDto getNewInfrastructureData(Date since);
	
}
