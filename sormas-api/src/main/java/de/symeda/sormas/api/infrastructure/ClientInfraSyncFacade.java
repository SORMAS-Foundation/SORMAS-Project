package de.symeda.sormas.api.infrastructure;

import javax.ejb.Remote;

@Remote
public interface ClientInfraSyncFacade {

	InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates);
}
