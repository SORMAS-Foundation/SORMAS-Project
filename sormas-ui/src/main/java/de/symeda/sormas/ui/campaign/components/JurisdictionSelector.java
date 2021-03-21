package de.symeda.sormas.ui.campaign.components;

import com.vaadin.ui.ComboBox;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.AREA;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.COMMUNITY;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.DISTRICT;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.REGION;

public class JurisdictionSelector extends ComboBox {

	public JurisdictionSelector() {
		setCaption(I18nProperties.getCaption(Captions.Campaign_grouping));
		setItems(AREA, REGION, DISTRICT, COMMUNITY);
		setEmptySelectionAllowed(false);
		setValue(AREA);
	}
}
