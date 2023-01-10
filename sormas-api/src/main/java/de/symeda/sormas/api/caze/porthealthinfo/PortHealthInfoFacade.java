package de.symeda.sormas.api.caze.porthealthinfo;

import javax.ejb.Remote;

@Remote
public interface PortHealthInfoFacade {

	PortHealthInfoDto getByCaseUuid(String caseUuid);
}
