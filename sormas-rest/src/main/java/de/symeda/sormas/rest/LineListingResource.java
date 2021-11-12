package de.symeda.sormas.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/lineListing")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class LineListingResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	public Page<FeatureConfigurationIndexDto> getIndexList(
		@RequestBody @NotNull CriteriaWithSorting<FeatureConfigurationCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getFeatureConfigurationFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	public void postFeatureConfigurations(@Valid List<FeatureConfigurationIndexDto> dtos) {
		FacadeProvider.getFeatureConfigurationFacade().saveFeatureConfigurations(dtos, FeatureType.LINE_LISTING);

	}

	@POST
	@Path("/delete")
	public void deleteFeatureConfigurations(@Valid FeatureConfigurationCriteria criteria) {
		FacadeProvider.getFeatureConfigurationFacade().deleteAllFeatureConfigurations(criteria);
	}

	@POST
	@Path("/enabled")
	public Map<Disease, List<FeatureConfigurationIndexDto>> getEnabledFeatureConfigurations(@RequestBody FeatureConfigurationCriteria criteria) {
		return FacadeProvider.getFeatureConfigurationFacade().getEnabledFeatureConfigurations(criteria);
	}

}
