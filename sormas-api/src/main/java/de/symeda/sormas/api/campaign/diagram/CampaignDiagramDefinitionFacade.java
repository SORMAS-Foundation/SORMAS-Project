package de.symeda.sormas.api.campaign.diagram;

import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

@Remote
public interface CampaignDiagramDefinitionFacade {

	CampaignDiagramDefinitionDto save(@Valid CampaignDiagramDefinitionDto campaignDiagramDefinitionDto);

	List<CampaignDiagramDefinitionDto> getAll();
	
	//List<CampaignDiagramDefinitionDto> getByRound();

	List<CampaignDiagramDefinitionDto> getByUuids(List<String> uuids);

	boolean exists(String diagramId);

	CampaignDiagramDefinitionDto getByDiagramId(String diagramId);
}
