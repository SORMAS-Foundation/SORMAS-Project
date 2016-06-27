package de.symeda.sormas.backend.facility;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {
	
	public FacilityService() {
		super(Facility.class);
	}
}
