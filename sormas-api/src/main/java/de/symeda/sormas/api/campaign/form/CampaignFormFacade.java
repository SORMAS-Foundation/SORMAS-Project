package de.symeda.sormas.api.campaign.form;

import javax.ejb.Remote;
import java.io.IOException;
import java.util.List;

@Remote
public interface CampaignFormFacade {

	CampaignFormDto saveCampaignForm(CampaignFormDto campaignFormDto);

	/**
	 * Validates the campaign form by checking whether mandatory elements are included, only supported types are used
	 * and elements used in associations are included in the schema. In addition, cleans any elements that are used
	 * in the UI from any HTML tags but those defined in {@link CampaignFormElement#ALLOWED_HTML_TAGS}.
	 */
	void validateAndClean(CampaignFormDto campaignFormDto);

	CampaignFormDto buildCampaignFormFromJson(String formId, String languageCode, String schemaDefinitionJson, String translationsJson)
		throws IOException;

	List<CampaignFormReferenceDto> getAllCampaignFormsAsReferences();

	CampaignFormDto getCampaignFormByUuid(String campaignFormUuid);

}
