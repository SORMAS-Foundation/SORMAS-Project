package de.symeda.sormas.backend.caze.porthealthinfo;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class PortHealthInfoService extends BaseAdoService<PortHealthInfo> {

	public PortHealthInfoService() {
		super(PortHealthInfo.class);
	}

}
