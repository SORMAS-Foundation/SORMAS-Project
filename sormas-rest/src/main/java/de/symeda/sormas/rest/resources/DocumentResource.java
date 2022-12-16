package de.symeda.sormas.rest.resources;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Document Resource",
	description = "Management of document data. Provides access to documents that have been uploaded as supplementary material to a **Case**, **Contact**, **Action**, **Event** or **Travel Entry**.")
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DocumentResource {

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific document based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns a document by its UUID.", useReturnTypeSchema = true)
	public DocumentDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the document.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getDocumentFacade().getDocumentByUuid(uuid);
	}

	@POST
	@Path("/entityDocuments")
	@Operation(summary = "Get all documents that are related to a specific entity.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map where the key is the UUID of the entity and the value is a list of documents that are related to the specific entity.",
		useReturnTypeSchema = true)
	public Map<String, List<DocumentDto>> getDocumentsRelatedToEntities(
		@RequestBody(description = "Document-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<DocumentCriteria> criteriaWithSorting) {
		return FacadeProvider.getDocumentFacade()
			.getDocumentsRelatedToEntities(criteriaWithSorting.getCriteria(), criteriaWithSorting.getSortProperties());
	}

}
