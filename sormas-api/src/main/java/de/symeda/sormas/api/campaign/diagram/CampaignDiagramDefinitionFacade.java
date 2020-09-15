package de.symeda.sormas.api.campaign.diagram;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CampaignDiagramDefinitionFacade {

	CampaignDiagramDefinitionDto save(CampaignDiagramDefinitionDto campaignDiagramDefinitionDto);

	List<CampaignDiagramDefinitionDto> getAll();

	List<CampaignDiagramDefinitionDto> getByUuids(List<String> uuids);
}
