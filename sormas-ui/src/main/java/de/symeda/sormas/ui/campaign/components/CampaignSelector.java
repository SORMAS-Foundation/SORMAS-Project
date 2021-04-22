package de.symeda.sormas.ui.campaign.components;

import java.util.List;

import com.vaadin.data.HasValue;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignSelector extends HorizontalLayout {

	private final ComboBox<CampaignReferenceDto> campaignCombo;

	public CampaignSelector() {
		setMargin(false);
		setSpacing(false);

		Label campaignLabel = new Label(I18nProperties.getCaption(Captions.Campaign));
		campaignLabel.addStyleName("v-caption");
		campaignLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
		addComponent(campaignLabel);
		setComponentAlignment(campaignLabel, Alignment.MIDDLE_CENTER);

		campaignCombo = new ComboBox<>(" ");
		List<CampaignReferenceDto> campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaignCombo.setItems(campaigns);
		campaignCombo.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllCampaigns));
		final CampaignReferenceDto lastStartedCampaign = FacadeProvider.getCampaignFacade().getLastStartedCampaign();
		if (lastStartedCampaign != null) {
			campaignCombo.setValue(lastStartedCampaign);
		}
		CssStyles.style(campaignCombo, CssStyles.SOFT_REQUIRED);
		addComponent(campaignCombo);
	}

	public CampaignReferenceDto getValue() {
		return campaignCombo.getValue();
	}

	public void setValue(CampaignReferenceDto value) {
		campaignCombo.setValue(value);
	}

	public void addValueChangeListener(HasValue.ValueChangeListener listener) {
		campaignCombo.addValueChangeListener(listener);
	}
}
