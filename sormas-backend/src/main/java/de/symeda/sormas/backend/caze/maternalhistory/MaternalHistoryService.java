package de.symeda.sormas.backend.caze.maternalhistory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class MaternalHistoryService extends BaseAdoService<MaternalHistory> {

	public MaternalHistoryService() {
		super(MaternalHistory.class);
	}

}
