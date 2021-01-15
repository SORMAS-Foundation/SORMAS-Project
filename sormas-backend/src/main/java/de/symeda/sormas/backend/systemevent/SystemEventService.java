package de.symeda.sormas.backend.systemevent;

import de.symeda.sormas.backend.common.BaseAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class SystemEventService extends BaseAdoService<SystemEvent> {

	public SystemEventService() {
		super(SystemEvent.class);
	}

}
