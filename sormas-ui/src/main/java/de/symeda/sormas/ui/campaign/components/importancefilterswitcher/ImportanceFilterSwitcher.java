package de.symeda.sormas.ui.campaign.components.importancefilterswitcher;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImportanceFilterSwitcher extends OptionGroup {

	private static final String ONLY_IMPORTANT_FORM_ELEMENTS = "onlyImportantFormElements";

	public ImportanceFilterSwitcher() {
		CssStyles.style(this, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		setId(ONLY_IMPORTANT_FORM_ELEMENTS);
		addItem(CampaignFormElementImportance.IMPORTANT);
		setItemCaption(CampaignFormElementImportance.IMPORTANT, I18nProperties.getEnumCaption(CampaignFormElementImportance.IMPORTANT));
		addItem(CampaignFormElementImportance.ALL);
		setItemCaption(CampaignFormElementImportance.ALL, I18nProperties.getEnumCaption(CampaignFormElementImportance.ALL));
		setValue(CampaignFormElementImportance.ALL);
	}

	public boolean isImportantSelected() {
		return CampaignFormElementImportance.IMPORTANT.equals(this.getValue());
	}
}
