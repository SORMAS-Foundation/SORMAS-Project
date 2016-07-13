package de.symeda.sormas.backend.region;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;



@Stateless
@LocalBean
public class DistrictService extends AbstractAdoService<District> {
	
	public DistrictService() {
		super(District.class);
	}
}
