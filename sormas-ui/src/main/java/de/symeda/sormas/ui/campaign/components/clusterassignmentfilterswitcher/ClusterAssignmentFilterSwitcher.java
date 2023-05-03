package de.symeda.sormas.ui.campaign.components.clusterassignmentfilterswitcher;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.campaign.components.importancefilterswitcher.CampaignFormElementImportance;
import de.symeda.sormas.ui.utils.CssStyles;

public class ClusterAssignmentFilterSwitcher extends OptionGroup{
	
	private static final String ONLY_ASSIGNED_CLUSTERS = "onlyAssignedClusters";

	public ClusterAssignmentFilterSwitcher() {
		CssStyles.style(this, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		setId(ONLY_ASSIGNED_CLUSTERS);
		addItem(ClusterAssignmentImportance.ASSIGNED);
		setItemCaption(ClusterAssignmentImportance.ASSIGNED, I18nProperties.getEnumCaption(ClusterAssignmentImportance.ASSIGNED));
		addItem(ClusterAssignmentImportance.UNASSIGNED);
		setItemCaption(ClusterAssignmentImportance.UNASSIGNED, I18nProperties.getEnumCaption(ClusterAssignmentImportance.UNASSIGNED));
		setValue(ClusterAssignmentImportance.UNASSIGNED);
	}

	public boolean isImportantSelected() {
		return ClusterAssignmentImportance.ASSIGNED.equals(this.getValue());
	}

}
