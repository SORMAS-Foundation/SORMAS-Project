package de.symeda.sormas.api.infrastructure;

import javax.ejb.Remote;

@Remote
public interface MobileAppInfrastructureSyncFacade {

	InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates);
}
