package de.symeda.sormas.backend.caze;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class HospitalizationService extends AbstractAdoService<Hospitalization> {
	
	public HospitalizationService() {
		super(Hospitalization.class);
	}

}
