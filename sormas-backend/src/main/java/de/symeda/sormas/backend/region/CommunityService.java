package de.symeda.sormas.backend.region;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;



@Stateless
@LocalBean
public class CommunityService extends AbstractAdoService<Community> {
	
	public CommunityService() {
		super(Community.class);
	}
}
