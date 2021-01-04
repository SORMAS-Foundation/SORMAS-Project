package de.symeda.sormas.backend.therapy;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class TherapyService extends AbstractAdoService<Therapy> {

	public TherapyService() {
		super(Therapy.class);
	}

}
