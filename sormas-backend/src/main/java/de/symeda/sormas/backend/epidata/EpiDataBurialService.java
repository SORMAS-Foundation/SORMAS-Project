package de.symeda.sormas.backend.epidata;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class EpiDataBurialService extends AbstractAdoService<EpiDataBurial> {
	
	public EpiDataBurialService() {
		super(EpiDataBurial.class);
	}

}
