package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/formFields")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class FormFieldResource extends EntityDtoResource<FormFieldsDto> {

	@GET
	@Path("/all/{since}")
	public List<FormFieldsDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getFormFieldFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<FormFieldsDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getFormFieldFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getFormFieldFacade().getAllUuids();
	}

	@Override
	public UnaryOperator<FormFieldsDto> getSave() {
		return FacadeProvider.getFormFieldFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<FormFieldsDto> formFieldsDtos) {
		return super.postEntityDtos(formFieldsDtos);
	}
}
