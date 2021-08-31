package de.symeda.sormas.api.infrastructure;

import javax.ejb.Remote;

@Remote
public interface InfrastructureSyncFacade {

	InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates);
}
