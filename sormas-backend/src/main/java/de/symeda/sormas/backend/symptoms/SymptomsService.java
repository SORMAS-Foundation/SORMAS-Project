package de.symeda.sormas.backend.symptoms;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class SymptomsService extends AbstractAdoService<Symptoms> {
	
	public SymptomsService() {
		super(Symptoms.class);
	}
}
