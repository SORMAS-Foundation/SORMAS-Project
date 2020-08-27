package de.symeda.sormas.backend.campaign.diagram;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "CampaignDiagramDefinitionFacade")
public class CampaignDiagramDefinitionFacadeEjb implements CampaignDiagramDefinitionFacade {

	@EJB
	private CampaignDiagramDefinitionService service;

	@Override
	public CampaignDiagramDefinitionDto save(CampaignDiagramDefinitionDto campaignDiagramDefinitionDto) {

		CampaignDiagramDefinition campaignDiagramDefinition = fromDto(campaignDiagramDefinitionDto);
		service.ensurePersisted(campaignDiagramDefinition);
		return toDto(campaignDiagramDefinition);
	}

	@Override
	public List<CampaignDiagramDefinitionDto> getAll() {
		return service.getAll().stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<CampaignDiagramDefinitionDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	public CampaignDiagramDefinition fromDto(@NotNull CampaignDiagramDefinitionDto source) {
		CampaignDiagramDefinition target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new CampaignDiagramDefinition();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setDiagramId(source.getDiagramId());
		target.setDiagramType(source.getDiagramType());
		target.setDiagramCaption(source.getDiagramCaption());
		target.setCampaignDiagramSeries(source.getCampaignDiagramSeriesList());

		return target;
	}

	public CampaignDiagramDefinitionDto toDto(CampaignDiagramDefinition source) {
		if (source == null) {
			return null;
		}

		CampaignDiagramDefinitionDto target = new CampaignDiagramDefinitionDto();
		DtoHelper.fillDto(target, source);

		target.setDiagramId(source.getDiagramId());
		target.setDiagramType(source.getDiagramType());
		target.setDiagramCaption(source.getDiagramCaption());
		target.setCampaignDiagramSeriesList(source.getCampaignDiagramSeries());

		return target;
	}

	@LocalBean
	@Stateless
	public static class CampaignDiagramDefinitionFacadeEjbLocal extends CampaignDiagramDefinitionFacadeEjb {
	}
}
