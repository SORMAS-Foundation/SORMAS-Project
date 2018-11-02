package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteria;

@Path("/classification")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class ClassificationResource {

	@GET
	@Path("/all")
	public List<DiseaseClassificationCriteria> getAllClassificationCriteria(@Context SecurityContext sc) {
		return FacadeProvider.getCaseClassificationFacade().getAllClassificationCriteria();
	}
	
}
