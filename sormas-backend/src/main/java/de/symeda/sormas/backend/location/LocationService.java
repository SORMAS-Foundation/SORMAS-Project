package de.symeda.sormas.backend.location;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class LocationService extends AbstractAdoService<Location> {
	
	public LocationService() {
		super(Location.class);
	}
}
