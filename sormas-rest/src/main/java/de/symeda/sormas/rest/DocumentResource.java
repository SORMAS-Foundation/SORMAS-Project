package de.symeda.sormas.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.document.DocumentCriteria;
import de.symeda.sormas.api.document.DocumentDto;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DocumentResource {

	@GET
	@Path("/{uuid}")
	public DocumentDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getDocumentFacade().getDocumentByUuid(uuid);
	}

	@POST
	@Path("/entityDocuments")
	public Map<String, List<DocumentDto>> getDocumentsRelatedToEntities(CriteriaWithSorting<DocumentCriteria> criteriaWithSorting) {
		return FacadeProvider.getDocumentFacade()
			.getDocumentsRelatedToEntities(criteriaWithSorting.getCriteria(), criteriaWithSorting.getSortProperties());
	}

}
