package de.symeda.sormas.rest.resources;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/lineListing")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Line Listing Resource",
	description = "Configuration for the front end display. Allows the creation of cases via the line listing funcitionality for all users.")
public class LineListingResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a list of FeatureConfigurationIndexDtos based on FeatureConfigurationCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a page of feature configurations that have met the filter criteria.",
		useReturnTypeSchema = true)
	public Page<FeatureConfigurationIndexDto> getIndexList(
		@RequestBody(description = "Feature configuration-based query-filter with sorting property.",
			required = true) @NotNull CriteriaWithSorting<FeatureConfigurationCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getFeatureConfigurationFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of feature configurations to the server.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response postFeatureConfigurations(
		@RequestBody(description = "List of FeatureConfigurationIndexDtos to be added to the server.",
			required = true) @Valid List<FeatureConfigurationIndexDto> dtos) {
		FacadeProvider.getFeatureConfigurationFacade().saveFeatureConfigurations(dtos, FeatureType.LINE_LISTING);
		return Response.ok("OK").build();
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete feature configurations based on FeatureConfigurationCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response deleteFeatureConfigurations(@Valid FeatureConfigurationCriteria criteria) {
		FacadeProvider.getFeatureConfigurationFacade().deleteAllFeatureConfigurations(criteria);
		return Response.ok("OK").build();
	}

	@POST
	@Path("/enabled")
	@Operation(summary = "Get all enabled feature configurations based on FeatureConfigurationCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map where the key is a disease and the value is a list with feature configurations that are related to the specific disease.",
		useReturnTypeSchema = true)
	public Map<Disease, List<FeatureConfigurationIndexDto>> getEnabledFeatureConfigurations(
		@RequestBody(description = "Feature configuration-based query-filter.", required = true) FeatureConfigurationCriteria criteria) {
		return FacadeProvider.getFeatureConfigurationFacade().getEnabledFeatureConfigurations(criteria);
	}

}
