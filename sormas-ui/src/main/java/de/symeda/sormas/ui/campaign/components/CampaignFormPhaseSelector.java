package de.symeda.sormas.ui.campaign.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.WordUtils;
import com.vaadin.data.HasValue;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignPhase;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignFormPhaseSelector extends HorizontalLayout {

	private static final String PHASE_ELEMENTS_SELECTOR = "campaignphases";
	private final ComboBox phaseComboBox;

	public CampaignFormPhaseSelector() {
		setMargin(false);
		setSpacing(false);

		Label formPhaseLabel = new Label("");
		formPhaseLabel.addStyleName("v-caption");
		formPhaseLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
		addComponent(formPhaseLabel);
		setComponentAlignment(formPhaseLabel, Alignment.MIDDLE_CENTER);

		phaseComboBox = new ComboBox<>(" ");

		// phaseComboBox.setItems(CampaignPhase.values());

		List<String> phases = new ArrayList();
		// phases.add("ALL PHASES");
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.PRE.toString()));
		}
		if (UserProvider.getCurrent().hasUserType(UserType.EOC_USER) || UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.INTRA.toString()));
		}
		// if (UserProvider.getCurrent().hasUserRight(UserRight.POST_CAMPAIGN_VIEW)) {
		// phases.add(WordUtils.capitalizeFully(CampaignPhase.POST.toString()));
		// }
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.POST.toString()));
		}

		phaseComboBox.setItems(phases);
		if(UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.PRE.toString()));
		}else {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.INTRA.toString()));
		}
		phaseComboBox.setEmptySelectionAllowed(false);
		// phaseComboBox.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllCampaigns));
		// final CampaignReferenceDto lastStartedCampaign =
		// FacadeProvider.getCampaignFacade().getLastStartedCampaign();
		// phaseComboBox.setItems(CampaignPhase.values());
		// phaseComboBox.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllForms));

		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		// ");

		CssStyles.style(phaseComboBox, CssStyles.SOFT_REQUIRED);
		addComponent(phaseComboBox);
	}

	public CampaignFormPhaseSelector(String seperator) {
		setMargin(false);
		setSpacing(false);

		Label formPhaseLabel = new Label("");
		formPhaseLabel.addStyleName("v-caption");
		formPhaseLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
		addComponent(formPhaseLabel);
		setComponentAlignment(formPhaseLabel, Alignment.MIDDLE_CENTER);

		phaseComboBox = new ComboBox<>(I18nProperties.getCaption(Captions.Campaign_phase));

		// phaseComboBox.setItems(CampaignPhase.values());

		List<String> phases = new ArrayList();
		// phases.add("ALL PHASES");
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.PRE.toString()));
		}
		if (UserProvider.getCurrent().hasUserType(UserType.EOC_USER) || UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.INTRA.toString()));
		}
		if (UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phases.add(WordUtils.capitalizeFully(CampaignPhase.POST.toString()));
		}
		phaseComboBox.setItems(phases);
		//set phase value based on the type of user
		if(UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.PRE.toString()));
		}else {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.INTRA.toString()));
		}
		
		
		phaseComboBox.setEmptySelectionAllowed(false);
		// phaseComboBox.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllCampaigns));
		// final CampaignReferenceDto lastStartedCampaign =
		// FacadeProvider.getCampaignFacade().getLastStartedCampaign();
		// phaseComboBox.setItems(CampaignPhase.values());
		// phaseComboBox.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllForms));

		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		// ");

		CssStyles.style(phaseComboBox, CssStyles.SOFT_REQUIRED);
		addComponent(phaseComboBox);
	}

	public String getValue() {
		return (String) phaseComboBox.getValue();
	}

	public void setValue(CampaignFormPhaseSelector value) {
		phaseComboBox.setValue(value);
	}

	public void clear() {
		if(UserProvider.getCurrent().hasUserType(UserType.WHO_USER)) {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.PRE.toString()));
		}else {
			phaseComboBox.setValue(WordUtils.capitalizeFully(CampaignPhase.INTRA.toString()));
		}
	}

	public void addValueChangeListener(HasValue.ValueChangeListener listener) {
		phaseComboBox.addValueChangeListener(listener);
	}

}
