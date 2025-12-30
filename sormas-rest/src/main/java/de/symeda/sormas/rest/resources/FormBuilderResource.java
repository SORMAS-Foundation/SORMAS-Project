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
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/formBuilders")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class FormBuilderResource extends EntityDtoResource<FormBuilderDto> {

	@GET
	@Path("/all/{since}")
	public List<FormBuilderDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getFormBuilderFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<FormBuilderDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getFormBuilderFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getFormBuilderFacade().getAllUuids();
	}

	@Override
	public UnaryOperator<FormBuilderDto> getSave() {
		return FacadeProvider.getFormBuilderFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<FormBuilderDto> formBuilderDtos) {
		return super.postEntityDtos(formBuilderDtos);
	}
}