package de.symeda.sormas.api.campaign.form;

import javax.ejb.Remote;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;

@Remote
public interface CampaignFormMetaFacade {

	CampaignFormMetaDto saveCampaignFormMeta(CampaignFormMetaDto campaignFormMetaDto);

	/**
	 * Validates the campaign form by checking whether mandatory elements are included, only supported types are used
	 * and elements used in associations are included in the schema. In addition, cleans any elements that are used
	 * in the UI from any HTML tags but those defined in {@link CampaignFormElement#ALLOWED_HTML_TAGS}.
	 */
	void validateAndClean(CampaignFormMetaDto campaignFormMetaDto);

	void validateAllFormMetas();

	CampaignFormMetaDto buildCampaignFormMetaFromJson(String formId, String languageCode, String schemaDefinitionJson, String translationsJson)
		throws IOException;

	List<CampaignFormMetaReferenceDto> getAllCampaignFormMetasAsReferences();

	CampaignFormMetaDto getCampaignFormMetaByUuid(String campaignFormUuid);

    List<CampaignFormMetaDto> getAllAfter(Date campaignFormMetaChangeDate);

	List<String> getAllUuids();

	List<CampaignFormMetaDto> getByUuids(List<String> uuids);
}
