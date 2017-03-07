package de.symeda.sormas.backend.epidata;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class EpiDataGatheringService extends AbstractAdoService<EpiDataGathering> {
	
	public EpiDataGatheringService() {
		super(EpiDataGathering.class);
	}

}
